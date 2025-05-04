package result

import org.springframework.http.HttpStatus

class ServiceResult implements Serializable {
    boolean success
    boolean warning = false
    String code
    String msg
    def data
    def list
    Long total
    Integer annoucementId
    HttpStatus httpStatus = HttpStatus.OK
}
