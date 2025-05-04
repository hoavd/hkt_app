package com.tcb.system.security

import grails.util.Holders
import groovy.transform.EqualsAndHashCode
import groovy.transform.ToString

@EqualsAndHashCode(includes='name')
@ToString(includes='name', includeNames=true, includePackage=false)
class RoleGroup {
	String code
    String name
    boolean display = false

    static constraints = {
        name blank: false, unique: true
    }

    static mapping = {
        cache true
    }
}
