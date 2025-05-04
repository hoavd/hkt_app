package com.tcb

import grails.boot.GrailsApp
import grails.boot.config.GrailsAutoConfiguration
import grails.plugin.springsecurity.SpringSecurityService
import groovy.transform.CompileStatic
import grails.plugins.metadata.*

@CompileStatic
class Application extends GrailsAutoConfiguration {
    static void main(String[] args) {
        GrailsApp.run(Application, args)

    }
}
