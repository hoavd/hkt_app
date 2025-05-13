package com.tcb.dashboard

import commons.ConstantWebSocket
import commons.ResultMsgConstant
import org.grails.web.json.JSONArray
import org.grails.web.json.JSONObject
import result.ServiceResult

import javax.transaction.Transactional

class DashBoardService {
    def serverSideMelaninService
    def brokerMessagingTemplate
    def commonService
    def springSecurityService

    @Transactional
    def pushAlert(def json) {
        Alert.withTransaction { def status ->
            try {
                Alert alert = new Alert()
                alert.properties = json
                alert.createdBy = springSecurityService.principal.username
                alert.save(flush: true, failOnError: true)
                JSONObject data = new JSONObject()
                data = commonService.setDataObject(data: json)
                brokerMessagingTemplate.convertAndSend ConstantWebSocket.TOPIC_ALERT, data
                return new ServiceResult(success: true, msg: ResultMsgConstant.SUCCESS)
            }
            catch (Exception e) {
                status.setRollbackOnly()
                e.printStackTrace()
                throw e
            }
        }
    }

    def pushVolume(def json) {
        Volume.withTransaction { def status ->
            try {
                Volume volume = new Volume()
                volume.properties = json
                volume.createdBy = springSecurityService.principal.username
                volume.save(flush: true, failOnError: true)
                JSONObject data = new JSONObject()
                data = commonService.setDataObject(data: json)
                brokerMessagingTemplate.convertAndSend ConstantWebSocket.TOPIC_VOLUME, data
                return new ServiceResult(success: true, msg: ResultMsgConstant.SUCCESS)
            }
            catch (Exception e) {
                status.setRollbackOnly()
                e.printStackTrace()
                throw e
            }
        }
    }

    def pushMessage(def json) {
        Message.withTransaction { def status ->
            try {
                Alert alert = Alert.findByUuid(json.uuid)
                Message message = new Message()
                message.alert = alert
                message.properties = json
                message.createdBy = springSecurityService.principal.username
                message.save(flush: true, failOnError: true)

                JSONObject data = commonService.setDataObject(type: ConstantWebSocket.TOPIC_MESSAGE_TYPE_MESSAGE,
                        data: [alerId    : alert.id,
                               uuid      : alert.uuid,
                               id        : message.id,
                               msg       : message.msg,
                               createdBy : message.createdBy,
                               createDate: commonService.formatDateToString(message.createDate, "dd/MM/yyyy HH:mm:ss")
                        ])
                brokerMessagingTemplate.convertAndSend ConstantWebSocket.TOPIC_MESSAGE + "-${alert.uuid}", data
                return new ServiceResult(success: true, msg: ResultMsgConstant.SUCCESS)
            }
            catch (Exception e) {
                status.setRollbackOnly()
                e.printStackTrace()
                throw e
            }
        }
    }

    def getVolume(def params) {
        try {
            StringBuilder sql = new StringBuilder()
            def whereParam = []
            sql.append(""" SELECT id, success_Rate, error_Rate, total_Requests, create_date, timestamp
                             FROM (SELECT id, success_Rate, error_Rate, total_Requests, 
                                          TO_CHAR(create_date, 'YYYY-MM-DD HH24:MI:SS') create_date,
                                          TO_CHAR(create_date, 'HH24:MI:SS') timestamp
                                     FROM tcb_volume d
                                    WHERE 2 = 2 """)

            if (params.selectedDate) {
                if (params.fromTime) {
                    null
                }
                if (params.toTime) {
                    null
                }
            }
            sql.append(""" ) th """)
            sql.append(" WHERE 1 = 1 order by id asc")

            def listdynamic = serverSideMelaninService.listdynamic(sql.toString(), params, whereParam)
            JSONArray dataAr = listdynamic.get("data") as JSONArray
            JSONArray newData = new JSONArray()
            for (def d in dataAr) {
                JSONObject data = new JSONObject()
                data.putAll([id           : d.id,
                             successRate  : d.success_Rate,
                             errorRate    : d.error_Rate,
                             totalRequests: d.total_Requests,
                             timestamp    : d.timestamp,
                             createDate   : d.create_date])
                newData.push(data)
            }
            return listdynamic.put("data", newData)
        } catch (Exception e) {
            commonService.printlnException(e, 'findAlert')
            return []
        }
    }

    def findAlert(def params) {
        try {
            StringBuilder sql = new StringBuilder()
            def whereParam = []
            sql.append(""" SELECT id, uuid, code, type, desc, severity, impact_Detail, rootcause, solution, create_date
                             FROM (SELECT id, uuid, code, type, desc, severity, impact_Detail, rootcause, solution,
                                          TO_CHAR(create_date, 'YYYY-MM-DD HH24:MI:SS') create_date
                                     FROM tcb_alert d
                                    WHERE 2 = 2 """)

            if (params.query) {
                sql.append(""" AND (upper(d.code) like upper(?))""")
                whereParam << ("%" + params.query + "%")
                whereParam << ("%" + params.query + "%")
            }
            sql.append(""" ) th """)
            sql.append(" WHERE 1 = 1 order by ")
            if (params?.sort) {
                sql.append(""" ${params?.sort} ${params?.order} """)
            } else {
                sql.append("  id desc")
            }

            def listdynamic = serverSideMelaninService.listdynamic(sql.toString(), params, whereParam)
            JSONArray dataAr = listdynamic.get("data") as JSONArray
            JSONArray newData = new JSONArray()
            for (def d in dataAr) {
                JSONObject data = new JSONObject()
                data.putAll([id          : d.id,
                             uuid        : d.uuid,
                             code        : d.code,
                             type        : d.type,
                             severity    : d.severity,
                             impactDetail: d.impact_Detail,
                             rootcause   : d.rootcause,
                             solution    : d.solution,
                             createDate  : d.create_date,
                             desc        : d.desc])
                newData.push(data)
            }
            return listdynamic.put("data", newData)
        } catch (Exception e) {
            commonService.printlnException(e, 'findAlert')
            return []
        }
    }

    def getAlert(def params) {
        StringBuilder sql = new StringBuilder()
        def whereParam = []
        sql.append(""" SELECT id, uuid, code, type, desc, severity, impact_Detail, rootcause, solution
                         FROM (SELECT id, uuid, code, type, desc, severity, impact_Detail, rootcause, solution,
                                          TO_CHAR(create_date, 'YYYY-MM-DD HH24:MI:SS') create_date
                                     FROM tcb_alert d
                                    WHERE 2 = 2
                                AND d.uuid = ? """)
        whereParam << (params.id)
        sql.append(""" ) th """)
        sql.append(" WHERE 1 = 1")

        def dataAr = serverSideMelaninService.query(sql.toString(), whereParam)
        JSONObject data = new JSONObject()
        for (def d in dataAr) {
            data = commonService.setDataObject([uuid        : d.uuid,
                                                code        : d.code,
                                                type        : d.type,
                                                severity    : d.severity,
                                                impactDetail: d.impact_Detail,
                                                rootcause   : d.rootcause,
                                                solution    : d.solution,
                                                desc        : d.desc
            ])
        }
        if (data) {
            return data
        } else {
            return []
        }
    }

    def findSolution(def json) {
        JSONObject data = new JSONObject()
        data = commonService.setDataObject(data: json)
        brokerMessagingTemplate.convertAndSend ConstantWebSocket.TOPIC_MESSAGE, data
        return new ServiceResult(success: true, msg: ResultMsgConstant.SUCCESS)
    }
}
