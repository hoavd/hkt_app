package com.tcb.dashboard

import domain.BaseDomain
import grails.util.Holders

class Message extends BaseDomain {
    Alert alert
    String msg
    static mapping = {
        table "${Holders.grailsApplication.config.prefix}_message"
    }

    static constraints = {
        alert(nullable: true)
        msg(nullable: true)
    }
}
