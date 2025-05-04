package com.tcb.system.fingerprint

import com.tcb.system.common.Conf
import commons.ResultMsgConstant
import grails.gorm.transactions.Transactional
import grails.web.servlet.mvc.GrailsParameterMap
import org.grails.web.json.JSONObject
import org.springframework.http.HttpStatus
import result.ServiceResult

//@Transactional
class ConfService {
    def serverSideMelaninService
    def commonService

    def findListConfig(def params) {
        def c = Conf.createCriteria()
        def listItem = c.list(max: params.max, offset: params.offset) {
            if (params.query) {
                or {
                    ilike('type', "%${params.query}%")
                    ilike('value', "%${params.query}%")
                    ilike('label', "%${params.query}%")
                }
            }

            if (params.sort) {
                order(params.sort, params.order)
            } else {
                order("ord", "asc")
                order("label", "asc")
            }
        }
        return listItem
    }

    def getConf(GrailsParameterMap params) {
        StringBuilder sql = new StringBuilder()
        def whereParam = []
        sql.append(""" SELECT id, type, value, label
                         FROM conf a
                          WHERE a.id = ?  """)
        whereParam << (params.id as Long)

        def dataAr = serverSideMelaninService.query(sql.toString(), whereParam)
        JSONObject data = new JSONObject()
        for (def d in dataAr) {
            data = commonService.setDataObject([id   : d.id,
                                                type : d.type,
                                                value: d.value,
                                                label: d.label,
            ])
        }
        if (data) {
            return data
        } else {
            return []
        }
    }

    @Transactional
    def saveConf(GrailsParameterMap params, def json) {
        try {
            Conf.withTransaction { def status ->
                try {
                    def conf
                    if (params.id) {
                        conf = Conf.get(params.id)
                        if (conf) {
                            def jsonTemp = json
                            conf.value = jsonTemp.value
//                            conf.type = jsonTemp.type
//                            conf.label = jsonTemp.label
                            def checkConf = checkConf(conf)
                            if (checkConf.success) {
                                conf.save(flush: true, failOnError: true)
                                jsonTemp << [id: conf.id]
                                return new ServiceResult(success: true, msg: ResultMsgConstant.SAVE_SUCCESS, data: jsonTemp)
                            } else {
                                status.setRollbackOnly()
                                return checkConf
                            }
                        } else {
                            return new ServiceResult(success: true, msg: ResultMsgConstant.RECORD_UNDEFINED)
                        }
                    } else {
                        return new ServiceResult(success: false, msg: ResultMsgConstant.DATA_NOT_FOUND, httpStatus: HttpStatus.NOT_FOUND)
                    }
                }
                catch (Exception ex) {
                    status.setRollbackOnly()
                    throw ex
                }
            }
        } catch (Exception e) {
            commonService.printlnException(e, '')
            return new ServiceResult(success: false, msg: ResultMsgConstant.SAVE_ERROR)
        }
    }

    def checkConf(Conf conf) {
        if (!conf.value) {
            return new ServiceResult(success: false, msg: "Value không được để trống!")
        } else if (!conf.label) {
            return new ServiceResult(success: false, msg: "Label không được để trống!")
        } else if (!conf.type) {
            return new ServiceResult(success: false, msg: "Type không được để trống!")
        } else {
            return new ServiceResult(success: true, msg: "")
        }
    }
}
