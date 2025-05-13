package com.tcb.dashboard

import domain.BaseDomain
import grails.util.Holders

class Volume extends BaseDomain {
    double successRate
    double errorRate
    double totalRequests

    static mapping = {
        table "${Holders.grailsApplication.config.prefix}_volume"
    }

    static constraints = {
        successRate nullable: true
        errorRate nullable: true
        totalRequests nullable: true
    }
}
