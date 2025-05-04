package com.tcb.system.common

import grails.util.Holders

class Conf {
    String dataType = 'Text'
    String label
    String value
    String type
    int ord
    static constraints = {
        value maxSize: 4000
        type blank: false
        dataType nullable: true
        ord nullable: true
    }

    static mapping = {
    }
}
