package com.tcb.system.log

import org.grails.web.json.JSONArray
import org.grails.web.json.JSONObject

class LogService {
    def serverSideMelaninService
    def commonService

    def findListLogData(def params) {
        StringBuilder sql = new StringBuilder()
        def whereParam = []
        sql.append(""" SELECT id, username, actiontime, ipaddress, actionname, log_data, create_time
                         FROM ( SELECT  id, username, TO_CHAR (actiontime, 'dd/mm/yyyy hh24:MI:ss') create_time, ipaddress,
                                               actionname, parameters log_data, actiontime
                                          FROM fx_log_action a
                                          WHERE 1 = 1 """)
        if (params.query) {
            sql.append(""" AND (a.username like (?) OR a.actionname like (?)) """)
            whereParam << ("%" + params.query + "%")
            whereParam << ("%" + params.query + "%")
        }

        if (params.sort && ['id', 'actiontime'].contains(params.sort)) {
            if ('actiontime'.contains(params.sort)) {
                sql.append(""" ORDER BY actiontime ${'asc'.equalsIgnoreCase(params.order) ? 'asc' : 'desc'} """)
            } else if ('id'.contains(params.sort)) {
                sql.append(""" ORDER BY id ${'asc'.equalsIgnoreCase(params.order) ? 'asc' : 'desc'} """)
            }
        } else {
            sql.append(""" ORDER BY id desc""")
        }
        sql.append(""" ) th """)
        sql.append(" WHERE 1 = 1")
        def listdynamic = serverSideMelaninService.listdynamic(sql.toString(), params, whereParam)
        JSONArray dataAr = listdynamic.get("data") as JSONArray
        JSONArray newData = new JSONArray()
        for (def d in dataAr) {
            JSONObject data = new JSONObject()
            data.putAll([id        : d.id,
                         actiontime: d.create_time,
                         ipaddress : d.ipaddress,
                         actionname: d.actionname,
                         username  : d.username,
                         log_data  : commonService.converClobToString(d.log_data)])
            newData.push(data)
        }
        return listdynamic.put("data", newData)
    }

    def findListLogSystem(def params) {
        StringBuilder sql = new StringBuilder()
        def whereParam = []
        sql.append(""" SELECT id, actiontime, note, exception, username
                         FROM (SELECT id, TO_CHAR (actiontime, 'dd/mm/yyyy hh24:MI:ss') actiontime, note, username,
                                      DBMS_LOB.SUBSTR (exception, 600)||'...' exception
                                FROM fx_log_system a
                               WHERE 1 = 1""")

        if (params.query) {
            sql.append(""" AND (a.note like (?)
                        OR a.username like (?)) """)
            whereParam << ("%" + params.query + "%")
            whereParam << ("%" + params.query + "%")
        }

        if (params.sort && ['id', 'actiontime'].contains(params.sort)) {
            sql.append(""" ORDER BY ${params.sort} ${'desc'.equalsIgnoreCase(params.order) ? 'desc' : 'asc'} """)
        } else {
            sql.append(""" ORDER BY a.actiontime desc, a.id desc""")
        }
        sql.append(""" ) th """)
        sql.append(" WHERE 1 = 1")
        def listdynamic = serverSideMelaninService.listdynamic(sql.toString(), params, whereParam)
        JSONArray dataAr = listdynamic.get("data") as JSONArray
        JSONArray newData = new JSONArray()
        for (def d in dataAr) {
            JSONObject data = new JSONObject()
            data.putAll([id        : d.id,
                         actiontime: d.actiontime,
                         note      : d.note,
                         username  : d.username,
                         exception : d.exception])
            newData.push(data)
        }
        return listdynamic.put("data", newData)
    }
}
