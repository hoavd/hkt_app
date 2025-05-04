package com.tcb.log

import domain.BaseDomain
import grails.util.Holders

/**Bảng lưu thông tin log lỗi hệ thống*/
class LogSystem extends BaseDomain {
    Date actiontime = new Date()
    String username
    String exception
    String message
    String note
    static mapping = {
        table "${Holders.grailsApplication.config.prefix}_log_system"
        exception sqlType: "clob"
        message sqlType: "clob"
        actiontime sqlType: "date"
    }

    static constraints = {
        username nullable: true
        exception nullable: true
        message nullable: true
        note(nullable: true, maxSize: 2000)
    }
}
