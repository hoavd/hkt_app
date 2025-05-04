package com.tcb.system.fingerprint

import commons.ResultMsgConstant
import grails.converters.JSON
import static org.springframework.http.HttpStatus.UNAUTHORIZED

class FingerprintController {
    def commonService
    def fingerprintService
    def springSecurityService

    static responseFormats = ['json', 'xml']

    def getNavItem() {
        commonService.printlnActionParams(actionUri, params, request, false)
        if (springSecurityService.principal.username) {
            def roleGroup = request.getHeader("roleGroup")
            if (roleGroup) {
                params << [roleGroup: roleGroup]
                respond(fingerprintService.getNavItem(params))
            } else {
                render status: UNAUTHORIZED
            }
        } else {
            render status: UNAUTHORIZED
        }
    }

    def clearCachedRequestMaps() {
        println "${actionUri}: " + new Date() + ": " + params
        try {
            springSecurityService.clearCachedRequestmaps()
            render(text: [success: true, msg: ResultMsgConstant.SUCCESS] as JSON, contentType: 'text/json', encoding: "UTF-8")
        } catch (Exception e) {
            e.printStackTrace()
            render(text: [success: false, msg: ResultMsgConstant.SOMETHING_WENT_WRONG] as JSON, contentType: 'text/json', encoding: "UTF-8")
        }
    }
}
