package com.tcb.system.common

class SidebarItem {
    Module module
    String title
    String description
    String htmlElementId
    String href
    String icon = 'fas fa-home'
    String roles
    Double ordernumber = 0
    boolean canEdit = true
    Integer level

    String parentItemName
    static belongsTo = [parent: SidebarItem]

    boolean active = false
    static mapping = {
        level column: 'levels'
    }

    static constraints = {
        module(nullable:true, bindable: false)
        level(nullable:true,bindable:false)
        icon(nullable: true)
        htmlElementId(maxSize: 500, minSize: 2,bindable:false)
        parent(nullable: true,bindable:false)
        roles(maxSize: 2000, nullable: true)
        title(nullable: true, bindable: false)
        parentItemName(nullable:true,bindable:false)
        href(nullable: true, bindable: false)
        canEdit(bindable:false)
        description(maxSize: 500, nullable: true)
    }
}