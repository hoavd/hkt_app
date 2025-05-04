package com.tcb.system.common

class Branch {
    String code
    String name
    String shortname
    int level
    boolean active
    boolean canbedeleted = true// đánh dấu xem có xóa được không
    //tên chính xác của đơn vị phục vụ in báo cáo
    String realName
    String region
    Boolean deleted = false

    static hasMany = [children: Branch]
    static belongsTo = [parent: Branch]

    static mapping = {
        children cache: 'nonstrict-read-write'
        children lazy: true
        level column: 'levels'
    }

    static constraints = {
        name(nullable: true, maxSize: 500)
        shortname(nullable: true, maxSize: 500)
        realName(nullable: true, maxSize: 500)
        parent nullable: true, bindable: false
        deleted nullable: true, bindable : false
        code nullable: true
        level bindable: false
        canbedeleted bindable: false
        region nullable: true
    }
}
