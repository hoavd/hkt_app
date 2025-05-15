package com.tcb.dashboard

import domain.BaseDomain
import grails.util.Holders

class Alert extends BaseDomain {
    String uuid = UUID.randomUUID().toString()
    String code
    String severity  // muc do
    String type // warning, info, error
    String desc
    String impactDetail
    String rootcause
    String solution

    static mapping = {
        table "${Holders.grailsApplication.config.prefix}_alert"
        desc column: 'description'
    }

    static constraints = {
        code(nullable: true, maxSize: 100)
        type(nullable: true, maxSize: 20)
        desc(nullable: true, maxSize: 2000)
        uuid(nullable: true, maxSize: 100)
        severity(nullable: true, maxSize: 20)
        impactDetail(nullable: true, maxSize: 1000)
        rootcause(nullable: true, maxSize: 2000)
        solution(nullable: true, maxSize: 1000)
    }
}
