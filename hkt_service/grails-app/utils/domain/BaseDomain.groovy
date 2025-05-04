package domain

@SuppressWarnings("GroovyAssignabilityCheck")
abstract class BaseDomain implements Serializable {
    Date createDate = new Date()
    String createdBy
    Date updateDate = new Date()
    String updatedBy

    static constraints = {
        createdBy  nullable: true, display: false, bindable: false
        createDate bindable: false, nullable: true, display: false
        updatedBy  nullable: true, display: false, bindable: false
        updateDate bindable: false, nullable: true, display: false
    }

    static mapping = {
        createDate sqlType: "TIMESTAMP"
        updateDate sqlType: "TIMESTAMP"
    }
}