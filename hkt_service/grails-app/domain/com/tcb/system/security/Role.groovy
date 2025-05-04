package com.tcb.system.security

import groovy.transform.EqualsAndHashCode
import groovy.transform.ToString

@EqualsAndHashCode(includes = 'authority')
@ToString(includes = 'authority', includeNames = true, includePackage = false)
class Role implements Serializable {
    private static final long serialVersionUID = 1

    RoleGroup roleGroup        // quyen chung
    String authority
    boolean active            // trang thai
    String diengiai
    boolean canbedeleted = true// đánh dấu xem có xóa được không
    boolean canEdit = true

    static mapping = {
        cache true
    }

    static constraints = {
        authority blank: false, unique: true
        diengiai nullable: true
        canbedeleted bindable: false
        canEdit bindable: false
        roleGroup nullable: true
    }
}
