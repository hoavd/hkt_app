package com.tcb.system.fingerprint

import com.tcb.system.common.SidebarItem
import com.tcb.system.security.Role
import commons.ResultMsgConstant
import grails.gorm.transactions.Transactional
import grails.web.servlet.mvc.GrailsParameterMap
import org.grails.web.json.JSONArray
import org.grails.web.json.JSONObject
import org.springframework.context.MessageSource
import org.springframework.context.i18n.LocaleContextHolder
import org.springframework.http.HttpStatus
import result.ServiceResult

import java.lang.reflect.Array

class SidebarItemService {
    def serverSideMelaninService
    def commonService
    MessageSource messageSource

    def findListSideBar(def params) {
        def c = SidebarItem.createCriteria()
        def listItem = c.list(max: params.max, offset: params.offset) {
            if (params.query) {
                or {
                    ilike('title', "%${params.query}%")
                    ilike('href', "%${params.query}%")
                    ilike('roles', "%${params.query}%")
                    ilike('description', "%${params.query}%")
                    eq('ordernumber', commonService.formatStringToDouble("${params.query}"))
                    eq('level', commonService.formatStringToDouble("${params.query}") as int)
                }
            }

            if (params?.active != null) {
                eq('active', params?.active as Boolean)
            }
            if (params.sort) {
                if (params.sort == 'title') {
                    order('description', params.order)
                } else {
                    order(params.sort, params.order)
                }
            } else {
                order("ordernumber", "asc")
            }
        }
        return listItem
    }

    def getSidebarItem(GrailsParameterMap params) {
        StringBuilder sql = new StringBuilder()
        def whereParam = []
        sql.append(""" SELECT a.id, a.levels, a.ordernumber, a.html_element_id, a.description,
                              a.can_edit, a.active, a.roles, a.title, a.icon, a.href, 
                              a.parent_item_name, b.id parent_id, b.title parent_title,
                              m.id module_id, m.code module_code, m.name module_name
                         FROM sidebar_item a, sidebar_item b, module m
                        WHERE a.parent_id = b.id(+) 
                          AND a.module_id = m.id(+) 
                          AND a.html_element_id <> 'dashboard'
                          AND a.id = ? 
                     ORDER BY a.ordernumber """)
        whereParam << (params.id as Long)

        def dataAr = serverSideMelaninService.query(sql.toString(), whereParam)
        params << [roleId: params.id]
        JSONObject data = new JSONObject()
        for (def d in dataAr) {
            data = commonService.setDataObject([id           : d.ID,
                                                title        : d.title ?
                                                        messageSource.getMessage(d.title as String, null as Object[],
                                                                d.description as String, LocaleContextHolder.getLocale()) : null,
                                                htmlElementId: d.html_element_id,
                                                ordernumber  : d.ordernumber,
                                                roles        : changeRoleStrToArray(d.roles),
                                                icon         : d.icon,
                                                href         : d.href,
                                                level        : d.levels,
                                                parent       : d.parent_id ? commonService.setDataObject([id   : d.parent_id,
                                                                                                          title: d.parent_title]) : null,
                                                module       : d.module_id ? commonService.setDataObject([id  : d.module_id,
                                                                                                          code: d.module_code,
                                                                                                          name: d.module_name]) : null,
                                                active       : commonService.decodeBoolean(d.ACTIVE),
                                                canEdit      : commonService.decodeBoolean(d.can_edit)],

            )
        }
        if (data) {
            return data
        } else {
            return []
        }
    }

    @Transactional
    def saveSidebarItem(GrailsParameterMap params, def json) {
        try {
            SidebarItem.withTransaction { def status ->
                try {
                    def sidebarItem
                    if (params.id) {
                        sidebarItem = SidebarItem.get(params.id)
                        if (sidebarItem) {
                            def jsonTemp = json
//                            sidebarItem.title = jsonTemp.title
                            sidebarItem.icon = jsonTemp.icon
                            sidebarItem.ordernumber = jsonTemp.ordernumber

                            def listRoleTemp = jsonTemp?.roles
                            if (listRoleTemp) {
                                if (listRoleTemp instanceof String) {
                                    sidebarItem.roles = listRoleTemp
                                } else if (listRoleTemp instanceof JSONArray ||
//                                        listRoleTemp instanceof net.minidev.json.JSONArray ||
                                        listRoleTemp instanceof Array) {
                                    listRoleTemp = listRoleTemp.unique()
//                                    sidebarItem.roles = listRoleTemp.join(',')
                                    def sizeRole = listRoleTemp?.size() ? listRoleTemp?.size() : 0
                                    if (sizeRole == 1) {
                                        sidebarItem.roles = listRoleTemp[0]
                                    } else if (sizeRole > 1) {
                                        StringBuilder listRoleString = new StringBuilder()
                                        for (int i = 0; i <= sizeRole - 1; i++) {
                                            listRoleString.append(listRoleTemp[i] as String)
                                            if (i < sizeRole - 1) {
                                                listRoleString.append(",")
                                            }
                                        }
                                        sidebarItem.roles = listRoleString.toString()
                                    } else {
                                        sidebarItem.roles = ""
                                    }
                                } else {
                                    sidebarItem.roles = ""
                                }
                            } else {
                                sidebarItem.roles = ""
                            }

                            def checkSidebarItem = checkSidebarItem(sidebarItem)
                            if (checkSidebarItem.success) {
                                sidebarItem.save(flush: true, failOnError: true)

                                //Nếu phân quyền sidebar cha thì tự động phân quyền các thằng con của nó
                                if (sidebarItem.level == 1) {
                                    SidebarItem.executeUpdate("update SidebarItem s set s.roles = :roles " +
                                            " where s.parent = :parent",
                                            [roles: sidebarItem.roles, parent: sidebarItem])
                                }

                                jsonTemp << [id: sidebarItem.id]
                                return new ServiceResult(success: true, msg: ResultMsgConstant.SAVE_SUCCESS, data: jsonTemp)
                            } else {
                                status.setRollbackOnly()
                                return checkSidebarItem
                            }
                        } else {
                            return new ServiceResult(success: true, msg: ResultMsgConstant.RECORD_UNDEFINED)
                        }
                    } else {
                        return new ServiceResult(success: false, msg: ResultMsgConstant.DATA_NOT_FOUND, httpStatus: HttpStatus.NOT_FOUND)
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

    def changeRoleStrToArray(def roles) {
        if (roles) {
            return roles.split(",")
        } else {
            return null
        }
    }

    def checkSidebarItem(def sidebarItem) {
        if (!sidebarItem.canEdit) {
            return new ServiceResult(success: false, msg: "Sidebar Item không được phép sửa!")
        } else {
            if (sidebarItem.roles) {
//                changeRoleStrToArray(sidebarItem.roles)
                def listRole = sidebarItem.roles.split(",")
                for (def r : listRole) {
                    def role = Role.findByAuthority(r)
                    if (!role) {
                        return new ServiceResult(success: false, msg: "Role không hợp lệ! Role " + r + " không tồn tại!")
                    }
                }
            }

            return new ServiceResult(success: true, msg: "")
        }
    }
}
