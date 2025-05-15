package com.tcb

import com.tcb.system.common.Branch
import com.tcb.system.common.Module
import com.tcb.system.common.SidebarItem
import com.tcb.system.security.RequestMap
import com.tcb.system.security.Role
import com.tcb.system.security.RoleGroup
import com.tcb.system.security.User
import com.tcb.system.security.UserRole
import grails.gorm.transactions.Transactional

class BootStrap {
    def springSecurityService
    def init = { servletContext ->
        initSystem()
        initBranchs()
        initModule()
        initSidebar()
        for (String url in [
                '/', '/oauth/', '/oauth/*', '/h2-console', '/h2-console/**',
                '/login', '/login.*', '/login/**',
                '/logout', '/logout.*', '/logout/*',
                '/assets/**', '/stomp', '/stomp/**', '/swagger/**', '/stomppy', '/stomppy/**',
                '/api/user/listRoleGroup', '/api/role/clearCachedRequestmaps',
                '/api/fingerprint/navItem', '/api/fingerprint/clearCachedRequestMaps',
                '/chat/**'
        ]) {
            new RequestMap(url: url, configAttribute: 'permitAll').save()
        }
        new RequestMap(url: '/admin/**', configAttribute: 'ROLE_ADMIN').save()
        new RequestMap(url: '/**', configAttribute: 'ROLE_ADMIN').save()

        springSecurityService.clearCachedRequestmaps()
    }
    def destroy = {
    }

    @Transactional
    def initSystem() {
        // role group
        def rolegroups = [
                ['ROLE_GROUP_ADMIN', 'Nhóm role Super Admin', false]
        ]

        RoleGroup roleGroup
        rolegroups.each {
            if (!RoleGroup.findByCode(it[0])) {
                roleGroup = new RoleGroup(code: it[0], name: it[1], display: it[2]).save(flush: true)
            }
        }

        // Tạo role
        Role role
        def roles = [
                ['ROLE_ADMIN', 'Admin', false, 'ROLE_GROUP_ADMIN'],
        ]
        roles.each {
            role = Role.findByAuthority(it[0])
            def rg = RoleGroup.findByCode(it[3])
            if (!role) {
                role = new Role(authority: it[0], active: true, diengiai: it[1], canbedeleted: false, canEdit: it[2])
                role.canbedeleted = false
                role.canEdit = false
                role.roleGroup = rg
                role.save(flush: true)
            } else {
                if (!role.roleGroup) {
                    role.roleGroup = rg
                    role.save(flush: true)
                }
            }
        }

        //tạo user admin
        def users = [['admin', 'admin', 'ROLE_ADMIN', 'adminemail'],
                     ['system', 'AWS', 'ROLE_ADMIN', 'systememail'],]

        users.each {
            if (!User.findByUsername(it[0])) {
                def user = new User(fullname: it[1], adOnly: false, password: '1', enabled: true, email: it[3])
                user.username = it[0]
                user.canbedeleted = false
                user.setId(1)
                user.save(flush: true, failOnError: true)
                def userRole = new UserRole()
                def roleAdmin = Role.findByAuthority('ROLE_ADMIN')
                userRole.setUser(user)
                userRole.setRole(roleAdmin)
                userRole.save(flush: true, failOnError: true)
            }
        }
    }

    @Transactional
    def initBranchs() {
        // add branch
        def branchesRoot = [
                ['TCB', 'TCB', 'TCB', 0, '1']]

        branchesRoot.each {
            if (!Branch.findByCode(it[0])) {
                def m = new Branch(code: it[0], realName: it[1], name: it[1], shortname: it[2], level: it[3] as int, parent: null, active: true)
                m.save(flush: true)
            }
        }
        //end branch
    }

    @Transactional
    def initSidebar() {
        def sideBars = [
                ['HOME', 1, 'sidebar.home', 'ROLE_ADMIN', 10, '/', '', 'home', 'fas fa-user-chart', false, 'Home'],
                ['DASHBOARD', 1, 'sidebar.dashboard.listWarning', '', 12, 'dashboard/listWarning', '', 'sidebarTransList', 'fa fa-exclamation-triangle', false, 'Danh sách cảnh báo sự cố'],
        ]

        sideBars.each {
            def module = Module.findByCode(it[0])
            if (module) {
                if (!SidebarItem.findByHtmlElementId(it[7])) {
                    def sidebar = new SidebarItem(roles: it[3],
                            ordernumber: it[4], active: true, icon: it[8], canEdit: it[9], description: it[10])
                    sidebar.title = it[2]
                    sidebar.module = module
                    sidebar.level = it[1] as Integer
                    sidebar.htmlElementId = it[7]
                    sidebar.parentItemName = it[6]
                    sidebar.href = it[5]

                    if (it[8]) {
                        def parentSideBar = SidebarItem.findByHtmlElementId(it[6])
                        if (parentSideBar) {
                            sidebar.parent = parentSideBar
                        }
                    }
                    sidebar.save(flush: true, failOnError: true)
                }
            } else {
                if (!SidebarItem.findByHtmlElementId(it[7])) {
                    def sidebar = new SidebarItem(title: it[2], roles: it[3],
                            ordernumber: it[4], active: true, icon: it[8], canEdit: it[9], description: it[10])
                    sidebar.title = it[2]
                    sidebar.module = module
                    sidebar.level = it[1] as Integer
                    sidebar.htmlElementId = it[7]
                    sidebar.parentItemName = it[6]
                    sidebar.href = it[5]
                    sidebar.save(flush: true)
                }
            }
        }
    }

    @Transactional
    def initModule() {
        def modules = [
                ['DASHBOARD', 'Dashboard']
        ]
        modules.each {
            if (!Module.findByCode(it[0])) {
                def m = new Module(code: it[0], name: it[1])
                m.save(flush: true)
            }
        }
    }
}