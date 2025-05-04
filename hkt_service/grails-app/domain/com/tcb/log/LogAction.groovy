package com.tcb.log

import grails.util.Holders

/** Bảng lưu thông tin thao tác của người dùng*/
class LogAction {
    String username
    Date actiontime = new Date()
    String actionname
    String ipaddress
    String parameters
    static mapping = {
        table "${Holders.grailsApplication.config.prefix}_log_action"
        actiontime sqlType: "date"
        parameters sqlType: "clob"
    }

    static constraints = {
        actionname (nullable: true, maxSize: 4000)
        parameters(nullable: true)
        ipaddress(nullable: true)
        username(nullable: true)
    }
}
