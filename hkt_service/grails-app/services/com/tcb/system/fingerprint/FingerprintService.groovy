package com.tcb.system.fingerprint

import com.tcb.system.common.SidebarItem
import com.tcb.system.security.Role
import com.tcb.system.security.RoleGroup
import grails.plugin.springsecurity.SpringSecurityUtils
import grails.web.servlet.mvc.GrailsParameterMap
import org.grails.web.json.JSONArray
import org.grails.web.json.JSONObject
import org.springframework.context.i18n.LocaleContextHolder

class FingerprintService {
    def commonService
    def messageSource

    def getNavItem(GrailsParameterMap params) {
        JSONArray listData = new JSONArray()
        def roleGroup = RoleGroup.findByCode(params.roleGroup)
        def listRole = Role.findAllByRoleGroup(roleGroup)*.authority
        def listSideBar = SidebarItem.findAll("from SidebarItem where active = true order by ordernumber")
        def parentSidebars = listSideBar.findAll { it.level == 1 }.sort { it.ordernumber }

        for (parent in parentSidebars) {
            if (!parent.roles || (SpringSecurityUtils.ifAnyGranted(parent.roles) && checkRole(listRole, parent.roles))) {
                JSONObject data = commonService.setDataObject([id   : parent.id,
                                                               href : parent.href,
                                                               icon : parent.icon,
                                                               title: messageSource.getMessage("${parent.title}",
                                                                       null as Object[], parent.description, LocaleContextHolder.getLocale()),
                                                               items: loadChildSidebar(parent, listSideBar, listRole)])
                listData << data
            }
        }
        if (listData) {
            return listData
        } else {
            return []
        }
    }

    def loadChildSidebar(SidebarItem parent, List<SidebarItem> listSideBar, def listRole) {
        JSONArray listData = new JSONArray()
        def listChildSidebar = listSideBar.findAll { it.parent == parent && it.level == 2 }
        if (listChildSidebar) {
            for (sidebar in listChildSidebar) {
                if (!sidebar.roles || (SpringSecurityUtils.ifAnyGranted(sidebar.roles) && checkRole(listRole, parent.roles))) {
                    JSONObject data = commonService.setDataObject([id   : sidebar.id,
                                                                   href : sidebar.href,
                                                                   icon : sidebar.icon,
                                                                   title: messageSource.getMessage("${sidebar.title}",
                                                                           null as Object[], sidebar.description, LocaleContextHolder.getLocale())])
                    listData << data
                }
            }
        }
        if (listData) {
            return listData
        } else {
            return []
        }
    }

    def checkRole(def listRole, String roles) {
        if (listRole) {
            if (roles) {
                for (def r in listRole) {
                    if (r in roles.replaceAll(" ", "").split(",")) {
                        return true
                    }
                }
                return false
            } else {
                return true
            }
        } else {
            return false
        }
    }
}
