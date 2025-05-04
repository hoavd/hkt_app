package com.tcb.dashboard

import domain.BaseDomain
import grails.util.Holders

class Volume extends BaseDomain {
    double successRate
    double errorRate

    static mapping = {
        table "${Holders.grailsApplication.config.prefix}_volume"
    }

    static constraints = {
        successRate nullable: false
        errorRate nullable: false
    }
}
