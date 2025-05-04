package com.tcb.system.security

import com.tcb.system.common.Conf
import com.tcb.system.security.Role
import com.tcb.system.security.RoleGroup
import com.tcb.system.security.User
import com.tcb.system.security.UserRole
import commons.ConstantCategories
import commons.Constant
import commons.ResultMsgConstant
import grails.gorm.transactions.Transactional
import grails.web.servlet.mvc.GrailsParameterMap
import org.grails.web.json.JSONArray
import org.grails.web.json.JSONObject
import org.springframework.web.multipart.MultipartFile
import result.ServiceResult
import java.util.regex.Matcher

class UserService {
    def springSecurityService
    def serverSideMelaninService
    def commonService

    def findListUser(def params, boolean status) {
        StringBuilder sql = new StringBuilder()
        def whereParam = []
        sql.append(""" SELECT id, ma_nhan_vien, username, fullname, sodienthoai, chuc_vu, email, enabled, canbedeleted,
                              branch_id, branch_code, branch_name, group_view_id, group_view_code, group_view_name, ad_only, authority
                         FROM (SELECT u.id, u.ma_nhan_vien, u.username, u.fullname, u.sodienthoai, u.chuc_vu, u.email, ro.authority,
                                      u.enabled, u.canbedeleted, to_char(u.last_login,'dd/mm/yyyy') last_login,
                                      b.id branch_id, b.code branch_code, b.name branch_name, 
                                      g.id group_view_id, g.code group_view_code, g.name group_view_name, u.ad_only
                                 FROM users u, branch b, fx_group_view g, (SELECT u.id ,  LISTAGG (r.authority,';') WITHIN GROUP (ORDER BY r.id) authority
                                                            FROM users u, user_role ur, role r
                                                            WHERE u.id = ur.user_id(+) AND ur.role_id = r.id(+)
                                                            GROUP BY u.id) ro
                                WHERE u.branch_id = b.id (+) 
                                AND u.group_view_id = g.id (+) 
                                AND u.id = ro.id """)
        if (params.autocomplete) {
            sql.append(""" AND u.username <> 'admin' """)
        }
        if (status) {
            sql.append(""" AND u.enabled = 1 """)
        }

        if (params.roleId) {
            sql.append(""" AND EXISTS (SELECT 1 FROM user_role r WHERE u.id = r.user_id AND r.role_id = ?) """)
            whereParam << (params.roleId)
        }

        if (params.isApprove) {
            sql.append(""" AND u.is_approve = 1 """)
        }

        if (params.username) {
            sql.append(""" AND u.username like (?) """)
            whereParam << ("%" + params.username + "%")
        }

        if (params.fullname) {
            sql.append(""" AND upper(u.fullname) like upper(?) """)
            whereParam << ("%" + params.fullname + "%")
        }

        if (params.maNhanVien) {
            sql.append(""" AND u.ma_nhan_vien = ? """)
            whereParam << (params.maNhanVien)
        }

        if (params.query) {
            sql.append(""" AND (u.username like (?)
                        OR upper(u.fullname) like upper(?)
                        OR upper(b.name) like upper(?)
                        OR upper(g.code) like upper(?)
                        OR upper(u.ma_nhan_vien) like upper(?)
                        OR EXISTS (SELECT 1 FROM user_role ur, role r WHERE ur.user_id = u.id AND ur.role_id = r.id AND r.authority = upper(?))) """)
            whereParam << ("%" + params.query + "%")
            whereParam << ("%" + params.query + "%")
            whereParam << ("%" + params.query + "%")
            whereParam << ("%" + params.query + "%")
            whereParam << ("%" + params.query + "%")
            whereParam << (params.query)
        }
        if (params.sort && ['username', 'fullname', 'authority'].contains(params.sort)) {
            sql.append(""" ORDER BY upper(${params.sort}) ${'desc'.equalsIgnoreCase(params.order) ? 'desc' : 'asc'} """)
        } else {
            sql.append(""" ORDER BY username """)
        }
        sql.append(""" ) th """)
        sql.append(" WHERE 1 = 1")

        def listdynamic = serverSideMelaninService.listdynamic(sql.toString(), params, whereParam)
        JSONArray dataAr = listdynamic.get("data") as JSONArray
        JSONArray newData = new JSONArray()
        for (def d in dataAr) {
            JSONObject data = new JSONObject()
            data.putAll([id          : d.id,
                         maNhanVien  : d.ma_nhan_vien,
                         username    : d.username,
                         fullname    : d.fullname,
                         sodienthoai : d.sodienthoai,
                         chucVu      : d.chuc_vu,
                         branch      : d.branch_id ? commonService.setDataObject([id  : d.branch_id,
                                                                                  code: d.branch_code,
                                                                                  name: d.branch_name]) : null,
                         groupView   : d.group_view_id ? commonService.setDataObject([id  : d.group_view_id,
                                                                                      code: d.group_view_code,
                                                                                      name: d.group_view_name]) : null,
                         email       : d.email,
                         enabled     : commonService.decodeBoolean(d.enabled),
                         adOnly      : commonService.decodeBoolean(d.ad_only),
                         authority   : d.authority,
                         canbedeleted: commonService.decodeBoolean(d.canbedeleted)])
            newData.push(data)
        }
        return listdynamic.put("data", newData)
    }

    def getPublicInfo(GrailsParameterMap params) {
        def result = [:]
        def principal = springSecurityService.getPrincipal()
        if (principal) {
            User user = User.get(principal?.id)
            //--
            def listTransactionType = []
            listTransactionType = listTransactionType.unique().sort()

            //Nếu null thì gán full
            if (!listTransactionType) {
                listTransactionType.push(Constant.TRANSACTION_TYPE_TODAY)
                listTransactionType.push(Constant.TRANSACTION_TYPE_TOM)
                listTransactionType.push(Constant.TRANSACTION_TYPE_SPOT)
                listTransactionType.push(Constant.TRANSACTION_TYPE_FW)
                listTransactionType.push(Constant.TRANSACTION_TYPE_SWAP)
            }
            //--
            result = [id                 : user.id,
                      username           : user.username,
                      fullname           : user.fullname,
                      email              : user.email ?: "",
                      chucVu             : user.chucVu ?: "",
                      publicInfo         : user.publicInfo
            ]
        }
        return result
    }

    def saveUser(GrailsParameterMap params, def json) {
        try {
            User.withTransaction { def status ->
                try {
                    User userUpdate = springSecurityService.getCurrentUser()
                    def user
                    if (params.id) {
                        user = User.get(params.id)
                    } else {
                        user = new User()
                        user.username = json.username
                    }
                    if (user) {
                        user.properties = json
                        if (json?.adOnly) {
                            user.password = UUID.randomUUID().toString().replace('-', '')
                        } else {
                            if (json.password) {
                                user.password = json.password
                            }
                            if (!user.password) {
                                user.password = Constant.defaultPassWord
                            }
                        }
                        user.updateDate = new Date()
                        user.updateBy = userUpdate.username
                        user.username = user?.username?.toLowerCase()
                        /*Thêm phân quyền currency*/
                        /*Bỏ: phân quyền deal chuyển sang role*/
                        /*if (json?.currency) {
                            user.listCurrency = getDataCurrencyPairByObject(json?.currency)
                        } else {
                            user.listCurrency = ""
                        }*/
                        /**/
                        def checkUser = checkUser(user, json)
                        if (checkUser.success) {
                            user.save(flush: true, failOnError: true)
                            if (json.role) {
                                def listRoleRemove = Role.findAllByIdNotEqual(json.role.id)
                                for (Role roleRemove in listRoleRemove) {
                                    UserRole.remove(user, roleRemove)
                                }
                                def role = Role.findByIdAndActive(json.role.id, true)
                                if (role) {
                                    UserRole.create(user, role, true)
                                }
                            } else {
                                UserRole.removeAll(user)
                            }

                            json << [id: user.id]
                            return new ServiceResult(success: true, msg: ResultMsgConstant.SAVE_SUCCESS, data: json)
                        } else {
                            return checkUser
                        }
                    } else {
                        return new ServiceResult(success: true, msg: ResultMsgConstant.RECORD_UNDEFINED)
                    }
                }
                catch (Exception ex) {
                    status.setRollbackOnly()
                    throw ex
                }
            }
        } catch (Exception e) {
            commonService.printlnException(e, 'saveUser')
            return new ServiceResult(success: false, msg: ResultMsgConstant.SAVE_ERROR)
        }
    }

    @Transactional
    def savePublicInfo(def json) {
        try {
            User.withTransaction { def status ->
                try {
                    if (json.userId) {
                        User user = User.get(json.userId)
                        if (user) {
                            user.publicInfo = json?.publicInfo?.toString()
                            user.save(flush: true, failOnError: true)
                            return new ServiceResult(success: true, msg: ResultMsgConstant.SAVE_SUCCESS)
                        } else {
                            return new ServiceResult(success: false, msg: ResultMsgConstant.RECORD_UNDEFINED)
                        }
                    } else {
                        return new ServiceResult(success: false, msg: ResultMsgConstant.RECORD_UNDEFINED)
                    }
                }
                catch (Exception ex) {
                    status.setRollbackOnly()
                    throw ex
                }
            }
        } catch (Exception e) {
            commonService.printlnException(e, 'savePublicInfo')
            return new ServiceResult(success: false, msg: ResultMsgConstant.SAVE_ERROR)
        }
    }

    boolean isValidPassword(final String password) {
        Matcher matcher = Constant.pattern.matcher(password)
        return matcher.matches()
    }

    def checkUser(User user, def json) {
        if (!user.username) {
            return new ServiceResult(success: false, msg: "Username không được để trống!")
        } else if (!user.fullname) {
            return new ServiceResult(success: false, msg: "Tên nhân viên không được để trống!")
        } else if (!user.branch) {
            return new ServiceResult(success: false, msg: "Đơn vị kinh doanh không được để trống!")
        } else {
            if (user.username == 'admin') {
                return new ServiceResult(success: false, msg: "Username admin không được phép sửa!")
            }
            User userTemp = User.findByUsernameOrMaNhanVien(user.username, user.maNhanVien)
            if (userTemp) {
                if (userTemp.id != user.id) {
                    return new ServiceResult(success: false, msg: "Username hoặc mã nhân viên đã tồn tại trong hệ thống!")
                }
            } else {
                if (!json.password) {
                    return new ServiceResult(success: false, msg: "Mật khẩu không được để trống!")
                }
            }
            if (json.password) {
                if (!isValidPassword(json.password)) {
                    return new ServiceResult(success: false, msg: "Mật khẩu phải có độ dài từ 8-20 ký tự, có ít nhất 1 ký tự số, 1 ký tự thường, 1 ký tự hoa và 1 ký tự đặc biệt!")
                }
            }
            return new ServiceResult(success: true, msg: "")
        }
    }

    def deleteUser(GrailsParameterMap params) {
        try {
            User.withTransaction { def status ->
                try {
                    User user = User.get(params.id as Long)
                    if (user && user?.canbedeleted) {
                        user.delete(flush: true)
                        return new ServiceResult(success: true, msg: ResultMsgConstant.DELETED_SUCCESS)
                    } else {
                        return new ServiceResult(success: false, msg: "Bản ghi này đã phát sinh quan hệ dữ liệu, không thể xóa!")
                    }
                }
                catch (Exception ex) {
                    status.setRollbackOnly()
                    throw ex
                }
            }
        } catch (Exception e) {
            commonService.printlnException(e, '')
            return new ServiceResult(success: false, msg: "Bản ghi này đã phát sinh quan hệ dữ liệu, không thể xóa!")
        }
    }

    def getUser(GrailsParameterMap params) {
        StringBuilder sql = new StringBuilder()
        def whereParam = []
        sql.append(""" SELECT c.id, c.ma_nhan_vien, c.username, c.fullname, c.sodienthoai, c.chuc_vu, c.is_approve,
                              c.email, c.enabled, c.canbedeleted, to_char(c.last_login,'dd/mm/yyyy') last_login, c.ad_only,
                              c.branch_id, b.code branch_code, b.name branch_name, c.is_leader, c.list_currency currency
                         FROM users c, branch b                      
                        WHERE c.branch_id = b.id(+) and c.id = ? """)
        whereParam << (params.id as Long)

        def dataAr = serverSideMelaninService.query(sql.toString(), whereParam)
        def userRole = getUserRole(params.id as Long)
        JSONObject data = new JSONObject()
        for (def d in dataAr) {
            data.putAll([id                 : d.ID,
                         maNhanVien         : d.MA_NHAN_VIEN,
                         username           : d.USERNAME,
                         fullname           : d.FULLNAME,
                         sodienthoai        : d.SODIENTHOAI,
                         chucVu             : d.CHUC_VU,
                         branch             : d.BRANCH_ID ? commonService.setDataObject([id  : d.BRANCH_ID,
                                                                                         code: d.branch_code,
                                                                                         name: d.branch_name]) : null,
                         email              : d.EMAIL,
                         isApprove          : commonService.decodeBoolean(d.is_approve),
                         isLeader           : commonService.decodeBoolean(d.is_leader),
                         enabled            : commonService.decodeBoolean(d.ENABLED),
                         canbedeleted       : commonService.decodeBoolean(d.CANBEDELETED),
                         lastLogin          : d.LAST_LOGIN,
                         role               : userRole,
                         adOnly             : commonService.decodeBoolean(d.ad_only)])
        }
        if (data) {
            return data
        } else {
            return []
        }
    }

    def getUserRole(def userId) {
        StringBuilder sql = new StringBuilder()
        def whereParam = []
        sql.append(""" SELECT r.id, r.authority authority
                        FROM user_role ur, role r
                        WHERE ur.role_id = r.id
                          AND ur.user_id = ?
                          AND r.active = 1 
                        FETCH NEXT 1 ROWS ONLY""")
        whereParam << (userId)

        def dataAr = serverSideMelaninService.query(sql.toString(), whereParam)
        JSONObject data = new JSONObject()
        for (def d in dataAr) {
            data = commonService.setDataObject([
                    id       : d.id,
                    authority: d.authority
            ])
        }
        if (data) {
            return data
        } else {
            return null
        }
    }

    def getListRoleGroup() {
        StringBuilder sql = new StringBuilder()
        def whereParam = []
        def user = springSecurityService.getCurrentUser()

        sql.append(""" SELECT DISTINCT rg.id, rg.code, rg.name
                         FROM user_role ur, role r, role_group rg
                        WHERE ur.role_id = r.id AND r.role_group_id = rg.id
                          AND ur.user_id = ? """)
        whereParam << (user.id)
        def dataAr = serverSideMelaninService.query(sql.toString(), whereParam)
        JSONArray newData = new JSONArray()
        for (def d in dataAr) {
            JSONObject data = new JSONObject()
            data.putAll([id  : d.id,
                         code: d.code,
                         name: d.name])
            newData.push(data)
        }
        return newData
    }

    def changePassWord(def json) {
        try {
            User.withTransaction { def status ->
                try {
                    User currentUser = springSecurityService.getCurrentUser()
                    if (!currentUser.adOnly) {
                        if (springSecurityService.passwordEncoder.matches(json.currentPassword, currentUser.password)) {
                            if (json.newPassword) {
                                if (springSecurityService.passwordEncoder.matches(json.newPassword, currentUser.password)) {
                                    return new ServiceResult(success: false,
                                            msg: "Mật khẩu mới không được trùng mật khẩu cũ!")
                                }

                                if (!isValidPassword(json.newPassword)) {
                                    return new ServiceResult(success: false,
                                            msg: "Mật khẩu phải có độ dài từ 8-20 ký tự, có ít nhất 1 ký tự số, 1 ký tự thường, 1 ký tự hoa và 1 ký tự đặc biệt!")
                                }
                            }
                            currentUser.password = json.newPassword
                            currentUser.save(flush: true)
                            return new ServiceResult(success: true, msg: ResultMsgConstant.SAVE_SUCCESS)
                        } else {
                            return new ServiceResult(success: false, msg: "Mật khẩu cũ không chính xác!")
                        }
                    } else {
                        return new ServiceResult(success: false, msg: "User thiết lập sử dụng AD Only. Không đổi được mật khẩu!")
                    }
                }
                catch (Exception ex) {
                    status.setRollbackOnly()
                    throw ex
                }
            }
        } catch (Exception e) {
            commonService.printlnException(e, 'changePassWord')
            return new ServiceResult(success: false, msg: ResultMsgConstant.SAVE_ERROR)
        }
    }
}
