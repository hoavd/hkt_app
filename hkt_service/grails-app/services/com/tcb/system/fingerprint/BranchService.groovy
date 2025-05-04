package com.tcb.system.fingerprint

import com.tcb.system.common.Branch
import com.tcb.system.security.User
import commons.Constant
import commons.ResultMsgConstant
import grails.gorm.transactions.Transactional
import grails.web.servlet.mvc.GrailsParameterMap
import org.grails.web.json.JSONArray
import org.grails.web.json.JSONObject
import org.springframework.dao.DataIntegrityViolationException
import result.ServiceResult

class BranchService {
    def springSecurityService
    def serverSideMelaninService
    def commonService
    def permissionDataService

    def findListBranch(GrailsParameterMap params) {
        try {
            StringBuilder sql = new StringBuilder()
            def whereParam = []
            sql.append(""" SELECT id, code, name, levels, active, shortname, real_name, region, parent_id, parent_name, parent_code, canbedeleted
                         FROM (SELECT a.id, a.levels, a.code, a.name, a.active, a.canbedeleted, a.real_name, a.region,
                                      a.shortname, a.parent_id, p.name parent_name, p.code parent_code
                                 FROM branch a, branch p
                                WHERE a.parent_id = p.id(+)
                                  AND a.deleted = 0 """)

            if (params.code) {
                sql.append(""" AND a.code like (?) """)
                whereParam << ("%" + params.code + "%")
            }

            if (params.name) {
                sql.append(""" AND a.name like (?) """)
                whereParam << ("%" + params.name + "%")
            }

            if (params.active && (params.active == "true" || params.active == "1")) {
                sql.append(""" AND a.active = 1 """)
            }

            if (params.query) {
                sql.append(""" AND (a.code like (?)
                        OR upper(a.name) like upper(?)) """)
                whereParam << ("%" + params.query + "%")
                whereParam << ("%" + params.query + "%")
            }

            if (params.autocomplete) {
                sql.append(""" AND a.levels <> 0 """)
            }

            if (params.checkBranchChild) {
                User currentUser = springSecurityService.getCurrentUser()
                def branch = currentUser.branch
                if (branch) {
                    sql.append(""" AND a.id in (${commonService.changeListObjIdToStringId(getTreeBranchId(branch))}) """)
                }
            }

            if (params.checkPermissionData) {
                User currentUser = springSecurityService.getCurrentUser()
                def branch = currentUser.branch
//                def listCurrentUserBranchId = commonService.changeListObjIdToStringId(getTreeBranchId(branch))
                def permissionData = permissionDataService.getPermissionDataByUser(currentUser)
                if (permissionData) {
                    sql.append(""" AND (1 = 2 """)
                    for (def data in permissionData) {
                        sql.append(""" OR (1 = 2 """)
                        if (branch) {
                            sql.append(""" OR a.id in (${appendTreeBranchId()}) """)
                            whereParam << branch.id

                        }
                        sql.append("""OR a.id in (${data.branchs ? data.branchs : 0})) """)
                        sql.append(""" AND a.id not in (${data.not_in_branchs ? data.not_in_branchs : 0}) """)
                    }
                    sql.append(""" ) """)
                } else {
                    if (branch) {
                        sql.append(""" AND a.id in (${appendTreeBranchId()}) """)
                        whereParam << branch.id
                    }
                }
            }

            if (params.sort && ['a.id', 'a.code', 'a.name'].contains(params.sort)) {
                sql.append(""" ORDER BY ${params.sort} ${'desc'.equalsIgnoreCase(params.order) ? 'desc' : 'asc'} """)
            } else {
                sql.append(""" ORDER BY a.code""")
            }

            sql.append(""" ) th """)
            sql.append(" WHERE 1 = 1 order by code desc")
            def listdynamic = serverSideMelaninService.listdynamic(sql.toString(), params, whereParam)
            JSONArray dataAr = listdynamic.get("data") as JSONArray
            JSONArray newData = new JSONArray()
            for (def d in dataAr) {
                JSONObject data = new JSONObject()
                data.putAll([id          : d.id,
                             code        : d.code,
                             name        : d.name,
                             levels      : d.levels,
                             shortname   : d.shortname,
                             real_name   : d.real_name,
                             region      : d.region,
                             parent      : d.parent_id ?
                                     commonService.setDataObject([id  : d.parent_id,
                                                                  code: d.parent_name,
                                                                  name: d.parent_code]) : null,
                             canbedeleted: commonService.decodeBoolean(d.canbedeleted),
                             active      : commonService.decodeBoolean(d.active)])
                newData.push(data)
            }

            return listdynamic.put("data", newData)
        } catch (Exception e) {
            e.printStackTrace()
            return []
        }
    }

    def getBranch(GrailsParameterMap params) {
        try {
            StringBuilder sql = new StringBuilder()
            def whereParam = []
            sql.append(""" SELECT a.id, a.levels, a.code, a.name, a.active, a.shortname, a.region
                              a.real_name, a.parent_id, p.name parent_name, p.code parent_code
                          FROM branch a, branch p
                        WHERE a.parent_id = p.id(+)
                          AND a.id = ? """)
            whereParam << (params.id as Long)

            def dataAr = serverSideMelaninService.query(sql.toString(), whereParam)
            JSONObject data = new JSONObject()
            for (def d in dataAr) {
                data = commonService.setDataObject([id       : d.ID,
                                                    code     : d.CODE,
                                                    name     : d.NAME,
                                                    shortname: d.SHORTNAME,
                                                    level    : d.levels,
                                                    region   : d.region,
                                                    real_name: d.real_name,
                                                    parent   : d.PARENT_ID ? commonService.setDataObject([id  : d.PARENT_ID,
                                                                                                          code: d.PARENT_CODE,
                                                                                                          name: d.PARENT_NAME]) : null,
                                                    active   : commonService.decodeBoolean(d.ACTIVE)])
            }
            if (data) {
                return data
            } else {
                return []
            }
        } catch (Exception e) {
            e.printStackTrace()
            return []
        }
    }

    def branchTree() {
        def tree = []
        try {
            def rootNodes = Branch.createCriteria().list {
                isNull('parent')
                eq('active', true)
                order('name', 'asc')
            }
            rootNodes.each {
                tree << findSubBranchesAsJsonAntd(it)
            }
            return tree
        } catch (Exception e) {
            e.printStackTrace()
            return tree
        }
    }

    def findSubBranchesAsJson(def branch) {
        def currentNode = [name: branch.name, attr: [id: branch.id, code: branch.code]]
        def children = []
        def childTemp = Branch.createCriteria().list {
            eq('parent', branch)
            order('code', 'asc')
        }

        childTemp.each {
            children << findSubBranchesAsJson(it)
        }
        currentNode['children'] = children
        return currentNode
    }

    // trả tree data cho thư viện antd
    def findSubBranchesAsJsonAntd(def branch) {
        def currentNode = [
                title: "${branch.code} - ${branch.name}".toString(),
                key  : branch.id,
                data : [
                        id          : branch.id,
                        name        : branch.name,
                        code        : branch.code,
                        shortName   : branch.shortname,
                        realName    : branch.realName,
                        region      : branch.region,
                        level       : branch.level,
                        active      : branch.active,
                        parentId    : branch.parentId,
                        cpty_k      : branch.cpty_k,
                        clearingMode: branch.clearingMode,
                ]
        ]
        def children = []
        def childTemp = Branch.createCriteria().list {
            eq('parent', branch)
            order('code', 'asc')
        }

        childTemp.each {
            children << findSubBranchesAsJsonAntd(it)
        }
        currentNode['children'] = children
        return currentNode
    }

    @Transactional
    def saveBranch(GrailsParameterMap params, def json) {
        Branch.withTransaction { def status ->
            try {
                def branch
                def oldLevel = 0
                if (params.id) {
                    branch = Branch.get(params.id)
                    oldLevel = branch.level
                } else {
                    branch = new Branch()
                    branch.level = 1
                }
                if (branch) {
                    if (branch.level == 0) {
                        return new ServiceResult(success: false, msg: ResultMsgConstant.EDIT_FALSE)
                    }

                    branch.code = json?.code
                    branch.name = json?.name
                    branch.shortname = json?.shortName
                    branch.realName = json?.realName
                    branch.region = json?.region
                    branch.active = json?.active
                    branch.cpty_k = json?.cpty_k
                    branch.clearingMode = json?.clearingMode
                    if (json?.parent) {
                        branch.parent = Branch.get(json.parent as Long)
                        branch.level = branch.parent.level + 1
                    } else {
                        branch.parent = null
                        branch.level = 0
                    }
                    def checkBranch = checkBranch(branch)
                    if (checkBranch.success) {
                        branch.save(flush: true, failOnError: true)

                        def cLevel = branch.level - oldLevel
                        updateLevelBranchTree(branch, cLevel, params.id)
                        json << [id: branch.id]
                        return new ServiceResult(success: true, msg: ResultMsgConstant.SAVE_SUCCESS, data: json)
                    } else {
                        status.setRollbackOnly()
                        return checkBranch
                    }
                } else {
                    return new ServiceResult(success: true, msg: ResultMsgConstant.RECORD_UNDEFINED)
                }
            }
            catch (Exception ex) {
                status.setRollbackOnly()
                commonService.printlnException(ex, 'saveBranch')
                return new ServiceResult(success: false, msg: ResultMsgConstant.SAVE_ERROR)
            }
        }
    }

    def checkBranch(def branch) {
        if (!branch.code) {
            return new ServiceResult(success: false, msg: ResultMsgConstant.REQUIRED_CODE)
        } else {
            if (branch?.parent?.id && branch?.parent?.id == branch?.id) {
                return new ServiceResult(success: false, msg: "Đơn vị quản lý không hợp lệ!")
            }

            if (branch.id) {
                def listId = getChildBranchId(branch)*.ID
                if (branch?.parent?.id && listId.find { it as Long == branch?.parent?.id }) {
                    return new ServiceResult(success: false, msg: "Đơn vị quản lý không hợp lệ!")
                }
            }

            Branch branchTemp = Branch.findByCode(branch.code)
            if (branchTemp) {
                if (branchTemp.id != branch.id) {
                    return new ServiceResult(success: false, msg: ResultMsgConstant.CODE_DUPLICATE)
                }
            }
            branchTemp = Branch.findByName(branch.name)
            if (branchTemp) {
                if (branchTemp.id != branch.id) {
                    return new ServiceResult(success: false, msg: "Tên đã tồn tại trong hệ thống!")
                }
            }

            return new ServiceResult(success: true, msg: "")
        }
    }

    //Lấy danh sách id của branch con thuộc branch cha nhưng không kèm id cha
    def getChildBranchId(def branch) {
        StringBuilder sql = new StringBuilder()
        def whereParam = []
        sql.append(""" SELECT id
                         FROM branch
                        WHERE id <> ${branch.id}
                          AND INSTR( (SELECT CONCAT (LISTAGG (PATH,'') WITHIN GROUP (ORDER BY id), '/') PATH 
                                        FROM (SELECT id, name, parent_id, SYS_CONNECT_BY_PATH (id, '/') PATH,
                                                     ROW_NUMBER () OVER (PARTITION BY code ORDER BY LEVEL DESC) lv                                                     
                                              FROM branch
                                              START WITH id =  ${branch.id} 
                                              CONNECT BY NOCYCLE PRIOR id = parent_id)
                                        WHERE lv = 1), '/' || id || '/') >= 1 """)
        return serverSideMelaninService.query(sql.toString(), whereParam)

    }

    //Lấy danh sách id của branch con thuộc branch cha nhưng kèm id cha
    def getTreeBranchId(def branch) {
        if (branch) {
            StringBuilder query = new StringBuilder()
            query.append(""" SELECT id
                           FROM branch
                         START WITH id = ?
                         CONNECT BY NOCYCLE PRIOR id = parent_id """)
            def whereParam = []
            whereParam << (branch?.id)

            return serverSideMelaninService.query(query.toString(), whereParam)
        } else {
            return []
        }
    }

    //Lấy danh sách id của branch con thuộc danh sách branch cha kèm id cha
    //ví dụ listId: 1,2,3,4
    def getTreeBranchListId(String listBranchId) {
        if (listBranchId) {
            StringBuilder query = new StringBuilder()
            query.append(""" SELECT distinct id
                           FROM branch
                         START WITH id in (${listBranchId})
                         CONNECT BY NOCYCLE PRIOR id = parent_id """)
            def whereParam = []
            return serverSideMelaninService.query(query.toString(), whereParam)
        } else {
            return []
        }
    }

    def appendTreeBranchId() {
        StringBuilder query = new StringBuilder()
        query.append(""" SELECT id
                           FROM branch
                         START WITH id = ?
                         CONNECT BY NOCYCLE PRIOR id = parent_id """)
        return query.toString()
    }

    def appendTreeBranchInListId(String listBranchId) {
        StringBuilder query = new StringBuilder()
        query.append(""" SELECT distinct id
                           FROM branch
                         START WITH id in (${listBranchId})
                         CONNECT BY NOCYCLE PRIOR id = parent_id """)
        return query.toString()
    }

    def getListTreeBranchUser(def branchId) {
        StringBuilder query = new StringBuilder()
        //mysql
/*        query.append("""
         WITH RECURSIVE branch_paths AS
         (SELECT id, parent_id , 1 lvl  FROM branch  WHERE parent_id = ${branchId}
           UNION ALL
          SELECT e.id, e.parent_id, lvl + 1  FROM branch e  INNER JOIN branch_paths ep ON ep.id = e.parent_id)
          SELECT id, parent_id, lvl  FROM branch_paths
           UNION ALL
          SELECT id, parent_id , levels lvl  FROM branch  WHERE id = ${branchId} """)*/

        //oracle
        query.append(""" SELECT id
                           FROM branch
                         START WITH id = ?
                         CONNECT BY NOCYCLE PRIOR id = parent_id """)
        def whereParam = []
        whereParam << (branchId)
        def result = serverSideMelaninService.query(query.toString(), whereParam)
        def listIdBranch = []
        if (result) {
            result.each { item ->
                listIdBranch << item[0]
            }
        } else {
            return []
        }
        def listBranch = Branch.findAllByIdInList(listIdBranch)
        return listBranch
    }

    def getListBranchUser(def params, def branchId) {
        try {
            StringBuilder query = new StringBuilder()
            query.append(""" SELECT  id, code, name, levels, active, shortname
                           FROM (SELECT b.id, code, name, levels, active, shortname
                                   FROM branch b, (SELECT id FROM branch
                                                    START WITH id = ?
                                                  CONNECT BY NOCYCLE PRIOR id = parent_id) th 
                                  WHERE b.id = th.id """)
            def whereParam = []
            whereParam << (branchId)
            if (params.query) {
                query.append(""" AND (b.code like (?)
                              OR upper(b.name) like upper(?)) """)
                whereParam << ("%" + params.query + "%")
                whereParam << ("%" + params.query + "%")
            }
            query.append(" ORDER BY b.id )")
            query.append(" WHERE 1 = 1")
            def listdynamic = serverSideMelaninService.listdynamic(query.toString(), params, whereParam)
            JSONArray dataAr = listdynamic.get("data") as JSONArray
            JSONArray newData = new JSONArray()
            for (def d in dataAr) {
                JSONObject data = new JSONObject()
                data.putAll([id       : d.id,
                             code     : d.code,
                             name     : d.name,
                             levels   : d.levels,
                             shortname: d.shortname,
                             active   : commonService.decodeBoolean(d.active)])
                newData.push(data)
            }
            return listdynamic.put("data", newData)
        } catch (Exception e) {
            e.printStackTrace()
            commonService.printlnException(e, "getListBranchUser")
            throw e
        }
    }

    @Transactional
    def updateLevelBranchTree(def branch, def cLevel, def branchId) {
        if (branchId && cLevel != 0) {
            def listChildTemp = getChildBranchId(branch)*.ID
            def listChildBranchId = []
            listChildTemp.each {
                listChildBranchId.push(it as Long)
            }
            Branch.executeUpdate("update Branch b set level = (level + :cLevel) where b.id in :listChildBranchId",
                    [cLevel: cLevel, listChildBranchId: listChildBranchId])
        }
    }

    @Transactional
    def deleteBranch(GrailsParameterMap params) {
        Branch.withTransaction { def status ->
            try {
                Branch branch = Branch.get(params.id as Long)
                if (branch && branch.canbedeleted) {
                    if (branch.level == 0) {
                        return new ServiceResult(success: false, msg: ResultMsgConstant.DELETED_FALSE)
                    } else {
                        branch.delete(flush: true, failOnError: true)
                        return new ServiceResult(success: true, msg: ResultMsgConstant.DELETED_SUCCESS)
                    }
                } else {
                    return new ServiceResult(success: false, msg: ResultMsgConstant.DELETED_FALSE)
                }
            }
            catch (DataIntegrityViolationException violationException) {
                status.setRollbackOnly()
                return new ServiceResult(success: false, msg: "Không thể xóa được bản ghi do vi phạm điều kiện khóa ngoại!")
            }
            catch (Exception ex) {
                status.setRollbackOnly()
                commonService.printlnException(ex, 'deleteBranch')
                return new ServiceResult(success: false, msg: ResultMsgConstant.DELETED_ERROR)
            }
        }
    }

    def getListClearingMode() {
        return Constant.BRANCH_CLEARING_MODE
    }

    def getListBranchRegion() {
        return Constant.BRANCH_REGION
    }

}
