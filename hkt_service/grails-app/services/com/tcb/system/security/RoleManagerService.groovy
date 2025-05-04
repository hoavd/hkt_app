package com.tcb.system.security

import com.tcb.system.common.SidebarItem
import com.tcb.system.security.RequestMap
import com.tcb.system.security.Role
import com.tcb.system.security.RoleDetail
import commons.ResultMsgConstant
import grails.web.servlet.mvc.GrailsParameterMap
import org.grails.web.json.JSONArray
import org.grails.web.json.JSONObject
import result.ServiceResult

class RoleManagerService {
    def commonService
    def serverSideMelaninService
    def springSecurityService

    def findRole(GrailsParameterMap params) {
        def c = Role.createCriteria()
        def listRole = c.list(max: params.max, offset: params.offset) {
            if (params.query) {
                or {
                    ilike('authority', "%${params.query}%")
                    ilike('diengiai', "%${params.query}%")
                }
            }
            if (params.active) {
                eq('active', true)
            }
            if (params.sort) {
                order(params.sort, params.order)
            } else {
                order('authority', 'asc')
            }
        }
        def listUserRole = UserRole.findAll()
        JSONArray result = new JSONArray()
        for (def role in listRole) {
            def roleTemp = [:]
            roleTemp.putAll(role.properties)

            def roleGroupTemp = [:]
            if (roleTemp?.roleGroup) {
                roleGroupTemp << [roleGroup: commonService.setDataObject([id  : roleTemp.roleGroup?.id,
                                                                          code: roleTemp.roleGroup?.code,
                                                                          name: roleTemp.roleGroup?.name
                ])]
            }
            roleTemp.remove('roleGroup')
            roleTemp.remove('roleGroupId')
            if (roleGroupTemp) {
                roleTemp << roleGroupTemp
            }
            roleTemp << [id: role.id]

            def countUser = listUserRole.findAll({ it -> it.role == role }).size()
            roleTemp << [countUser: countUser]
            result << roleTemp
        }
        def totalRole = listRole.totalCount
        return [listRole: result, totalRole: totalRole]
    }

    def getRole(GrailsParameterMap params) {
        StringBuilder sql = new StringBuilder()
        def whereParam = []
        sql.append(""" SELECT a.id, a.diengiai, a.authority, a.active, a.transaction_type, a.list_currency currency,
                              b.id role_group_id, b.code role_group_code, b.name role_group_name
                         FROM role a, role_group b
                        WHERE 1 = 1  
                          AND a.role_group_id = b.id
                          AND a.id = ? """)
        whereParam << (params.id as Long)

        def dataAr = serverSideMelaninService.query(sql.toString(), whereParam)
        params << [roleId: params.id]
        def dsChucNang = getDsChucNangDaPhanQuyen(params, null)
        def listCondionDealRole = getListCondionDealRole(params.id)
        def listSidebarItemRole = getListSidebarItemRole(dataAr[0].AUTHORITY)

        JSONObject data = new JSONObject()
        for (def d in dataAr) {
            data = commonService.setDataObject([id                 : d.ID,
                                                authority          : d.AUTHORITY,
                                                diengiai           : d.DIENGIAI,
                                                transactionType    : d.transaction_type,
                                                active             : commonService.decodeBoolean(d.ACTIVE),
                                                roleGroup          : d.role_group_id ?
                                                        commonService.setDataObject([id  : d.role_group_id,
                                                                                     code: d.role_group_code,
                                                                                     name: d.role_group_name]) : null,
                                                dsModule           : dsChucNang,
                                                currency           : commonService.getListDataById(commonService.getTableName("currencyPair"), d.currency),
                                                listCondionDealRole: listCondionDealRole ?: [],
                                                listSidebarItemRole: listSidebarItemRole ?: []])
        }
        if (data) {
            return data
        } else {
            return []
        }
    }

    def getDsChucNangDaPhanQuyen(GrailsParameterMap params, def moduleId) {
        StringBuilder sql = new StringBuilder()
        def whereParam = []
        sql.append(""" SELECT module_id, TO_CHAR (dschucnang) dschucnang 
                         FROM role_detail a
                        WHERE 1 = 1  
                          AND a.role_id = ? """)
        whereParam << (params.roleId as Long)

        if (moduleId) {
            sql.append(""" AND module_id = ?""")
            whereParam << (params.moduleId as Long)
        }

        def dataAr = serverSideMelaninService.query(sql.toString(), whereParam)
        JSONArray newData = new JSONArray()
        for (def d in dataAr) {
            JSONObject data = new JSONObject()
            data = commonService.setDataObject([module    : d.MODULE_ID,
                                                dschucnang: d.DSCHUCNANG ? commonService.changeStrIdToListLongId(d.DSCHUCNANG) : ''])
            newData.push(data)
        }
        if (newData) {
            return newData
        } else {
            return []
        }
    }

    def getDsChucNang(GrailsParameterMap params) {
        def result = []
        def listModule
        if (params.moduleId) {
            listModule = Module.findAllById(params.moduleId as Long)
        } else {
            return result
        }

        for (Module module in listModule) {
            def listUrlGroup = getlistUrlGroup(module, params)
            result << commonService.setDataObject([module      : module.id,
                                                   dsChucNang  : listUrlGroup.data,
                                                   recordsTotal: listUrlGroup.recordsTotal])
        }
        return result
    }

    def getlistUrlGroup(Module module, GrailsParameterMap params) {
        StringBuilder sql = new StringBuilder()
        def whereParam = []
        sql.append(""" SELECT id, name, usecasename, usecase, module_id
                        FROM (SELECT id, name, usecasename, usecase, module_id
                                FROM url_group
                               WHERE 1 = 1  
                                 AND module_id = ? """)
        whereParam << (module.id)
        if (params.query) {
            sql.append(""" AND (upper(usecasename) like upper(?) OR upper(name) like upper(?))""")
            whereParam << ("%" + params.query + "%")
            whereParam << ("%" + params.query + "%")
        }
        sql.append(""" ) th """)
        sql.append(" WHERE 1 = 1 order by usecasename, name")


        def listdynamic = serverSideMelaninService.listdynamic(sql.toString(), params, whereParam)
        JSONArray dataAr = listdynamic.get("data") as JSONArray
        JSONArray newData = new JSONArray()
        for (def d in dataAr) {
            JSONObject data = new JSONObject()
            data.putAll([id         : d.id,
                         name       : d.name,
                         usecasename: d.usecasename,
                         usecase    : d.usecase,
                         module     : d.module_id])
            newData.push(data)
        }
        return listdynamic.put("data", newData)

        /* def dataAr = serverSideMelaninService.query(sql.toString(), whereParam)
         JSONArray newData = new JSONArray()
         for (def d in dataAr) {
             JSONObject data = new JSONObject()
             data = commonService.setDataObject()
             newData.push(data)
         }
         if (newData) {
             return newData
         } else {
             return []
         }*/
    }

    def getListModule() {
        return Module.findAll()
    }

    def saveRole(GrailsParameterMap params, def json) {
        try {
            Role.withTransaction { def status ->
                try {
                    def role
                    String authorityOld = ""
                    if (params.id) {
                        role = Role.get(params.id)
                    } else {
                        role = new Role()
                    }
                    if (role) {
                        if (!json?.roleGroup?.id) {
                            return new ServiceResult(success: false, msg: "Role Group không được để trống!")
                        }
                        role.properties = json
                        authorityOld = role.authority
                        role.authority = (json?.authority as String).toUpperCase()
                        role.diengiai = json?.diengiai
                        role.active = json.active
                        /*Thêm phân quyền currency*/
                        if (json?.currency) {
                            role.listCurrency = getDataCurrencyPairByObject(json?.currency)
                        } else {
                            role.listCurrency = ""
                        }
                        def checkRole = checkRole(role, authorityOld)
                        if (checkRole.success) {
                            role.save(flush: true, failOnError: true)
                            saveDsChucNang(json?.dsModule, role, authorityOld)
                            json << [id: role.id]
                            return new ServiceResult(success: true, msg: ResultMsgConstant.SAVE_SUCCESS, data: json)
                        } else {
                            return checkRole
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
            commonService.printlnException(e, 'saveRole')
            return new ServiceResult(success: false, msg: ResultMsgConstant.SAVE_ERROR)
        }
    }

    def configConditionDeal(GrailsParameterMap params, def json) {
        try {
            Role.withTransaction { def status ->
                try {
                    def role = Role.get(params.id as Long)
                    if (role) {
                        /*Thêm phân quyền currency*/
                        if (json?.listCondionDealRole) {
                            saveListCondionDealRole(role, json.listCondionDealRole)
                        } else {
                            ConditionDealRole.executeUpdate 'DELETE FROM ConditionDealRole WHERE role = :role', [role: role]
                        }
                        /**/
                        json << [id: role.id]
                        return new ServiceResult(success: true, msg: ResultMsgConstant.SAVE_SUCCESS, data: json)
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
            commonService.printlnException(e, '')
            return new ServiceResult(success: false, msg: ResultMsgConstant.SAVE_ERROR)
        }
    }

    def checkRole(def role, String authorityOld) {
        if (!role.authority) {
            return new ServiceResult(success: false, msg: "Tên Role không được để trống!")
        } else {
            if (authorityOld) {
                Role roleTemp = Role.findByAuthority(authorityOld)
                if (roleTemp) {
                    if (roleTemp.id != role.id) {
                        return new ServiceResult(success: false, msg: "Tên role đã tồn tại trong hệ thống!")
                    }
                    if (!roleTemp.canEdit) {
                        return new ServiceResult(success: false, msg: "Role này không cho phép sửa!")
                    }
                    if (role.authority != authorityOld) {
                        return new ServiceResult(success: false, msg: "Tên role không cho phép sửa!")
                    }
                }
            }
            return new ServiceResult(success: true, msg: "")
        }
    }

    def saveDsChucNang(def dsModule, Role role, String authorityOld) {
        try {
            //Nếu là sửa role thì cập nhật loại bỏ toàn bộ role cũ
            if (authorityOld) {
                //Tạm thời rào lại do khi update cần loop nhiều row
                //Cách 1: Nếu sử dụng cách này thì không cần phải call api clearCachedRequestmaps
                /*def listRequestMapOld = RequestMap.findAll("from RequestMap r where r.configAttribute like :role ", [role: "%" + role.authority + "%"])
                for (rq in listRequestMapOld) {
                    rq.configAttribute = rq.configAttribute.replace(role.authority + ",", "")
                    rq.save(flush: true, failOnError: true)
                }
                springSecurityService.clearCachedRequestmaps()*/
                //--end cách 1

                //Cách 2: Nếu sử dụng cách này câu lệnh nhanh hơn nhưng phải call lại api clearCachedRequestmaps ở controller
                RequestMap.executeUpdate("update RequestMap r set configAttribute= replace(configAttribute,:authority,'') where r.configAttribute like :role",
                        [authority: role.authority + ",", role: "%" + role.authority + "%"])
                //--end cách 2
                RoleDetail.executeUpdate("update RoleDetail r set dschucnang='' where r.role = :role",
                        [role: role])
            }

            if (dsModule) {
                for (def d in dsModule) {
                    if (d.module && d?.dschucnang) {
                        Module module = Module.get(d.module as Long)
                        def listUrlGroup = UrlGroup.findAllByIdInListAndModule(d?.dschucnang, module)
                        if (listUrlGroup) {
                            def strListId = commonService.changeListObjIdToStringId(listUrlGroup)
                            def roleDtl = RoleDetail.findByRoleAndModule(role, module)
                            if (!roleDtl) {
                                roleDtl = new RoleDetail(module: module, role: role, dschucnang: strListId)
                            } else {
                                roleDtl.dschucnang = strListId
                            }
                            roleDtl.save(flush: true, failOnError: true)

                            def urls = Url.findAllByUrlGroupInList(listUrlGroup)
                            def listRmOld = RequestMap.findAllByUrlInList(urls*.url)
                            for (def u in urls) {
                                //Nếu không tìm thấy url thì tạo row có ROLE mặc định là ROLE_ADMIN
                                if (!listRmOld.find { it.url == u.url && it.httpMethod == u.httpMethod }) {
                                    def rm = new RequestMap()
                                    rm.url = u.url
                                    rm.httpMethod = u.httpMethod
                                    rm.configAttribute = "ROLE_ADMIN"
                                    rm.save(flush: true, failOnError: true)
                                }
                            }

                            //Tạm thời rào lại do khi update cần loop nhiều row
                            //Cách 1: Nếu sử dụng cách này thì không cần phải call api clearCachedRequestmaps
                            /*def listRequestMap = RequestMap.findAll("from RequestMap r where (r.url, r.httpMethod) " +
                                    " in (select u.url, u.httpMethod from Url u where u.id in :urls)", [urls: urls*.id])
                            for (rq in listRequestMap) {
                                rq.configAttribute = role.authority + "," + rq.configAttribute
                                rq.save(flush: true, failOnError: true)
                            }
                            springSecurityService.clearCachedRequestmaps()*/
                            //--end cách 1

                            //Cách 2: Nếu sử dụng cách này câu lệnh nhanh hơn do ko phải loop nhưng phải call lại api clearCachedRequestmaps ở controller
                            //Cập nhật lại ROLE phân quyền cho rm
                            RequestMap.executeUpdate("update RequestMap r " +
                                    " set configAttribute= concat(:authority,configAttribute)" +
                                    " where (r.url, r.httpMethod) in " +
                                    " (select u.url, u.httpMethod from Url u where u.id in :urls)",
                                    [authority: role.authority + ",", urls: urls*.id])
                            //--end cách 2
                        }
                    }
                }
            }
        }
        catch (Exception e) {
            commonService.printlnException(e, 'saveDsChucNang')
            throw e
        }
    }

    def deleteRole(GrailsParameterMap params) {
        try {
            Role.withTransaction { def status ->
                try {
                    Role role = Role.get(params.id as Long)
                    if (role && role.canbedeleted) {
//                    role.delete(flush: true, failOnError: true)
                        RoleDetail.executeUpdate("delete RoleDetail r where r.role = :role", [role: role])
                        springSecurityService.deleteRole(role)
                        return new ServiceResult(success: true, msg: ResultMsgConstant.DELETED_SUCCESS)
                    } else {
                        return new ServiceResult(success: false, msg: "Không tìm thấy role này hoặc role không được xóa!")
                    }
                }
                catch (Exception ex) {
                    status.setRollbackOnly()
                    throw ex
                }
            }
        } catch (Exception e) {
            commonService.printlnException(e, '')
            return new ServiceResult(success: false, msg: ResultMsgConstant.DELETED_ERROR)
        }
    }

    def findRoleGroup(GrailsParameterMap params) {
        def c = RoleGroup.createCriteria()
        def listRoleGroup = c.list(max: params.max, offset: params.offset) {
            eq('display', true)
            if (params.query) {
                or {
                    ilike('code', "%${params.query}%")
                    ilike('name', "%${params.query}%")
                }
            }
            if (params.sort) {
                order(params.sort, params.order)
            } else {
                order('code', 'asc')
            }
        }

        def totalRoleGroup = listRoleGroup.totalCount
        return [listRoleGroup: listRoleGroup, totalRoleGroup: totalRoleGroup]
    }

    def configSidebarItem(GrailsParameterMap params, def json) {
        try {
            Role.withTransaction { def status ->
                try {
                    def role = Role.get(params?.id as Long)
                    if (role) {
                        /*Thêm phân quyền sidebar*/
                        saveListSidebarItem(role, json.listSidebarItem)
                        return new ServiceResult(success: true, msg: ResultMsgConstant.SAVE_SUCCESS)
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
            commonService.printlnException(e, '')
            return new ServiceResult(success: false, msg: ResultMsgConstant.SAVE_ERROR)
        }
    }

    def getListSidebarItemRole(def authority) {
        StringBuilder sql = new StringBuilder()
        def whereParam = []
        sql.append(""" SELECT LISTAGG (th.id, ',') WITHIN GROUP (ORDER BY th.id) sidebar_items 
                         FROM (  SELECT id
                                   FROM sidebar_item s
                                  WHERE INSTR (roles || ',', '${authority},') > 0
                                ) th""")
        def dataAr = serverSideMelaninService.query(sql.toString(), whereParam)
        JSONArray newData = new JSONArray()
        for (def d in dataAr) {
//            JSONObject data = new JSONObject()
//            data = commonService.setDataObject([sidebar_items: d.sidebar_items ? commonService.changeStrIdToListLongId(d.sidebar_items) : ''])
            if (d?.sidebar_items) {
                newData = commonService.changeStrIdToListLongId(d?.sidebar_items as String)
            }
        }
        if (newData) {
            return newData
        } else {
            return []
        }
    }

    def permissionGroupCif(GrailsParameterMap params, def json) {
        try {
            def role = Role.get(params?.id as Long)
            if (role) {
                /*Thêm phân quyền group cif*/
                saveListGroupCif(role, json.listGroupCif)
                return new ServiceResult(success: true, msg: ResultMsgConstant.SAVE_SUCCESS)
            } else {
                return new ServiceResult(success: true, msg: ResultMsgConstant.RECORD_UNDEFINED)
            }
        } catch (Exception e) {
            commonService.printlnException(e, '')
            return new ServiceResult(success: false, msg: ResultMsgConstant.SAVE_ERROR)
        }
    }

    def saveListSidebarItem(Role role, def listSidebarIdCheck) {
        try {
            def listSidebar = SidebarItem.findAllByActive(true)
            def listIdSidebarRole = getListSidebarItemRole(role.authority)
            def listSidebarRoleIn = listSidebar?.findAll({ it -> it.id in listIdSidebarRole })
            def listSidebarRoleCheck = listSidebar?.findAll({ it ->
                if (listSidebarIdCheck.find({ id -> id == it.id })) {
                    return true
                }
            })

            if (listSidebarIdCheck) {
                for (SidebarItem sidebarRole in listSidebarRoleCheck) {
                    if (!listSidebarRoleIn.find({ it.id == sidebarRole.id })) {
                        if (sidebarRole?.roles) {
                            def listRole = []
                            def listRoleTemp = sidebarRole?.roles?.split(",")
                            listRoleTemp.each { it ->
                                listRole.push(it)
                            }
                            listRole.push(role.authority)
                            sidebarRole.roles = listRole.join(",")
                        } else {
                            sidebarRole.roles = role.authority
                        }
                        sidebarRole.save(flush: true, failOnError: true)
                    }
                }

                def listSidebarIdCheckLong = listSidebarRoleCheck*.id
                def listRoleClear = listSidebarRoleIn.findAll({ it ->
                    !(it.id in (listSidebarIdCheckLong))
                })
                if (listRoleClear) {
                    for (SidebarItem sidebarRole in listRoleClear) {
                        def listRoleTemp = sidebarRole?.roles?.split(",")
                        if (listRoleTemp) {
                            listRoleTemp = listRoleTemp.findAll({ it -> it != role.authority })
                            sidebarRole.roles = listRoleTemp.join(",")
                        } else {
                            sidebarRole.roles = ""
                        }
                        sidebarRole.save(flush: true, failOnError: true)
                    }
                }
            } else {
                if (listSidebarRoleIn) {
                    for (SidebarItem sidebarRole in listSidebarRoleIn) {
                        def listRoleTemp = sidebarRole?.roles?.split(",")
                        if (listRoleTemp) {
                            listRoleTemp = listRoleTemp.findAll({ it -> it != role.authority })
                            sidebarRole.roles = listRoleTemp.join(",")
                        } else {
                            sidebarRole.roles = ""
                        }
                        sidebarRole.save(flush: true, failOnError: true)
                    }
                }
            }
        } catch (Exception e) {
            throw e
        }
    }


}
