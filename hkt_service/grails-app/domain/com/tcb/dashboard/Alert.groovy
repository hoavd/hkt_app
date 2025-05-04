package com.tcb.dashboard

import domain.BaseDomain
import grails.util.Holders

class Alert extends BaseDomain {
    String code
    String name
    String type // warning, info, error
    String desc
    static mapping = {
        table "${Holders.grailsApplication.config.prefix}_alert"
    }

    static constraints = {
        code(nullable: false, maxSize: 100)
        type(nullable: false, maxSize: 20)
        name(nullable: false, maxSize: 500)
        desc(nullable: true, maxSize: 1000)
    }
}
