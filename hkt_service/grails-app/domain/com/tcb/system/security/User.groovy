package com.tcb.system.security

import com.tcb.system.common.Branch
import grails.plugin.springsecurity.SpringSecurityService
import grails.util.Holders
import groovy.transform.EqualsAndHashCode
import groovy.transform.ToString

@EqualsAndHashCode(includes = 'username')
@ToString(includes = 'username', includeNames = true, includePackage = false)
class User implements Serializable {
    private static final long serialVersionUID = 1
    SpringSecurityService springSecurityService
    String fullname
    String username
    String password
    boolean enabled = true
    boolean accountExpired
    boolean accountLocked
    boolean passwordExpired
    boolean adOnly = false
    Branch branch
    Date lastLogin
    Date lastLogout
    String maNhanVien
    String sodienthoai
    String chucVu
    String email

    //Quyền phê duyệt
    boolean isApprove
    //Lãnh đạo đơn vị
    boolean isLeader

    boolean canbedeleted = true// đánh dấu xem có xóa được không

    //Lưu thông tin chung của users
    String publicInfo

    String updateBy
    Date updateDate = new Date()

    static constraints = {
        username blank: false, unique: true, bindable: false
        password blank: false, password: true
        fullname nullable: true
        branch nullable: true
        adOnly nullable: true
        lastLogin nullable: true, bindable: false
        lastLogout nullable: true, bindable: false
        maNhanVien nullable: true
        sodienthoai nullable: true
        chucVu nullable: true
        email nullable: true
        updateBy nullable: true
        updateDate nullable: true, bindable: false
        canbedeleted bindable: false
        isLeader  nullable: true
        isApprove nullable: true
        publicInfo nullable: true
    }

    static mapping = {
        password column: 'pass'
        table 'USERS'
        lastLogin sqlType: "date"
        lastLogout sqlType: "date"
        updateDate sqlType: "date"
        publicInfo  sqlType: "clob"
    }

    Set<Role> getAuthorities() {
        (UserRole.findAllByUser(this) as List<UserRole>)*.role as Set<Role>
    }

    def beforeInsert() {
        encodePassword()
    }

    def beforeUpdate() {
        if (isDirty('password')) {
            encodePassword()
        }
    }

    protected void encodePassword() {
        password = springSecurityService?.passwordEncoder ? springSecurityService.encodePassword(password) : password
    }
    static transients = ['springSecurityService']
}
