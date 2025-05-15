package com.tcb.system.security

import com.tcb.system.common.Module

class RoleDetail {
    Role role
    Module module
    String dschucnang
    static mapping = {
        dschucnang sqlType: "text"
    }

    static constraints = {
        dschucnang nullable: true
    }
}
