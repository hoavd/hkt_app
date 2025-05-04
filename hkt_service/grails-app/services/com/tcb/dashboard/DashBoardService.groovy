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

    @Transactional
    def pushAlert(def json) {
        Alert.withTransaction { def status ->
            try {
                Alert alert = new Alert()
                alert.properties = json
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
        JSONObject data = new JSONObject()
        data = commonService.setDataObject(data: json)
        brokerMessagingTemplate.convertAndSend ConstantWebSocket.TOPIC_VOLUME, data
        return new ServiceResult(success: true, msg: ResultMsgConstant.SUCCESS)
    }

    def pushMessage(def json) {
        JSONObject data = new JSONObject()
        data = commonService.setDataObject(data: json)
        brokerMessagingTemplate.convertAndSend ConstantWebSocket.TOPIC_MESSAGE, data
        return new ServiceResult(success: true, msg: ResultMsgConstant.SUCCESS)
    }

    def getVolume() {

    }

    def findAlert(def params) {
        try {
            StringBuilder sql = new StringBuilder()
            def whereParam = []
            sql.append(""" SELECT id, name, code, type, desc, create_date
                             FROM (SELECT id, name, code, type, desc, 
                                          TO_CHAR(create_date, 'YYYY-MM-DD HH24:MI:SS') create_date
                                     FROM tcb_alert d
                                    WHERE 2 = 2 """)
            if (params.name) {
                sql.append(""" AND upper(d.name) like upper(?)""")
                whereParam << ("%" + params.name + "%")
            }

            if (params.query) {
                sql.append(""" AND (upper(d.code) like upper(?) OR upper(d.name) like upper(?))""")
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
                data.putAll([id        : d.id,
                             name      : d.name,
                             code      : d.code,
                             type      : d.type,
                             createDate: d.create_date,
                             desc      : d.desc])
                newData.push(data)
            }
            return listdynamic.put("data", newData)
        } catch (Exception e) {
            commonService.printlnException(e, 'findAlert')
            return []
        }
    }
}
