package com.tcb.system.common

import com.fasterxml.jackson.databind.ObjectMapper
import com.tcb.log.LogAction
import com.tcb.system.security.User
import commons.Constant
import grails.converters.JSON
import grails.gorm.transactions.Transactional
import groovy.sql.Sql
import json.JSONDataObject
//import oracle.sql.CLOB
import org.apache.commons.codec.binary.Base64
import org.grails.web.json.JSONArray
import org.grails.web.json.JSONObject
import org.springframework.web.multipart.MultipartFile
import java.sql.Clob
import java.text.DateFormat
import java.text.SimpleDateFormat
import java.util.regex.Matcher
import java.util.regex.Pattern

//@Transactional
class CommonService {
    def sessionFactory
    def grailsApplication
    def dateTimeUtilService
    def springSecurityService
    def serverSideMelaninService
    def dataSource

    @Transactional
    def printlnActionParams(def actionUri, def params, def request = null, boolean isSaveLog = false, def json = null) {
        println "${actionUri}: " + new Date()
        println params as String
        if (isSaveLog) {
            String userName = springSecurityService?.principal?.username ?: "admin"
            LogAction log = new LogAction()
            log.username = userName
            log.actionname = "${actionUri}"
            log.ipaddress = "${request?.remoteAddr}"
            log.parameters = [params: params.toString(), json: json?.toString()]
            log.save(flush: true)
        }
    }

//    @Transactional
    def printlnException(Exception e, String note) {
        println e.printStackTrace()
    }


    @Transactional
    def saveLogAction(def actionname, def msg) {
        String userName = springSecurityService?.principal?.username ?: "admin"
        LogAction log = new LogAction()
        log.username = userName
        log.actionname = actionname
        log.parameters = msg
        log.save(flush: true)
    }

    def decodeBoolean(def number) {
        return number as String == "1"
    }

    def decodeStrToBoolean(def value) {
        if (value) {
            return true
        } else {
            return false
        }
    }

    def setDataObject(def map) {
        JSONObject data = new JSONObject()
        data.putAll(map)
        return data
    }

    def setGStringDataObject(String property, def value) {
        JSONObject data = new JSONObject()
        data[property] = value
        return data
    }

    def convertString2Map(def input) {
        def map = []
        if (input && input.toString().contains(',')) {
            map = input.replaceAll(~/^\[|\]$/, '').split(',')
        } else {
            map.add(input)
        }
        return map.sort()
    }

    def getListRoleUser() {
        try {
            String authorities = springSecurityService.principal.authorities.join(";")
            return authorities.split(";")
        } catch (Exception e) {
            return []
        }
    }

    def getListRoleUser(String username) {
        try {
            StringBuilder sql = new StringBuilder()
            def whereParam = []
            sql.append(""" SELECT r.authority role
                            FROM users u, user_role ur, role r
                           WHERE u.id = ur.user_id AND r.id = ur.role_id AND u.username = ?""")
            whereParam << username
            def dataAr = serverSideMelaninService.query(sql.toString(), whereParam)
            def newData = []
            for (def d in dataAr) {
                newData.push(d.role)
            }
            return newData
        } catch (Exception e) {
            printlnException(e, 'getListRoleUser')
            return []
        }
    }

    def converClobToString(Clob clob) {
//        return clob?.asciiStream?.text
        return clob?.characterStream?.text
    }

    def changeListObjIdToStringId(def listObj) {
        StringBuilder listObjString = new StringBuilder()
        if (listObj) {
            for (def obj : listObj) {
                listObjString.append(obj.id)
                listObjString.append(",")
            }
            listObjString.append("0")
            return listObjString.toString()
        } else {
            return ""
        }
    }

    def getListDataById(def table, def strListid) {
        if (strListid) {
            StringBuilder sql = new StringBuilder()
            def whereParam = []
            sql.append(""" SELECT * FROM ${table} t WHERE id in (${strListid})""")
            def dataAr = serverSideMelaninService.query(sql.toString(), whereParam)
            JSONArray newData = new JSONArray()
            if (table == "users") {
                for (def d in dataAr) {
                    JSONObject data = new JSONObject()
                    data.putAll([id      : d.id,
                                 username: d.username,
                                 fullname: d.fullname])
                    newData.push(data)
                }
            } else {
                for (def d in dataAr) {
                    JSONObject data = new JSONObject()
                    data.putAll([id  : d.id,
                                 code: d.code,
                                 name: d.name])
                    newData.push(data)
                }
            }
            if (newData) {
                return newData
            } else {
                return null
            }
        } else {
            return null
        }
    }

    def getTableName(String className) {
        def dc = grailsApplication.domainClasses.find { it.clazz.simpleName.equalsIgnoreCase(className) }
        Class<?> Domain = dc.clazz
        return sessionFactory.getClassMetadata(Domain).tableName
    }

    def getListRoleIdToStringId(User user) {
        def listUserRole = UserRole.findAllByUser(user)
        StringBuilder listRoleIdString = new StringBuilder()
        if (listUserRole) {
            for (def userRole : listUserRole) {
                listRoleIdString.append(userRole.roleId)
                listRoleIdString.append(",")
            }
            listRoleIdString.append("0")
            return listRoleIdString.toString()
        } else {
            return ""
        }
    }

//params: 1, 2, 0
    def changeStrIdToListLongId(String listStrId) {
        String[] listId = listStrId.replace(",0", "").split(",")
        Long[] listIdLong = new Long[listId.length]
        for (int i = 0; i < listIdLong.size(); i++) {
            listIdLong[i] = Long.parseLong(listId[i])
        }
        return listIdLong
    }

    def changeObjToLinkedHashMap(def obj) {
        if (obj) {
            ObjectMapper oMapper = new ObjectMapper()
            return oMapper.convertValue(obj, Map.class)
        } else {
            return null
        }
    }

    def getParamsData(JSONObject json, def obj) {
        def params = [:]
        //println json
        Iterator<String> keys = json.keys()
        while (keys.hasNext()) {
            String key = keys.next()
            if (obj.metaClass.properties.count { it.name == key } == 0) {
                def val = null
                try {
                    val = json.getJSONObject(key)
                } catch (Exception ignored) {
                    val = json.get(key)
                }
                if (val != null) {
                    params.put(key, val)
                }
            }
        }
        return params
    }

//Chuyển cột lưu giá trị chuỗi json thành col obj đối với object
    def processObjBeforeReturn(def obj, String col_json) {
        if (obj && !obj.isAttached())
            obj.attach()
//        def j = JSON.use('deep') {
//            obj as JSON
//        }
        if (obj) {
//            def json = new JSONDataObject(j.toString())
            def json = new JSONDataObject((obj as JSON).toString())
            return processJSONBeforeReturn(json, col_json)
        } else {
            return null
        }
    }

//Chuyển cột lưu giá trị chuỗi json thành col obj đối với dynamic sql
    def processJSONBeforeReturn(JSONObject json, String col_json) {
        if (col_json) {
            def params = json.get(col_json)
            if (params) {
                try {
                    if (params != "{}") {
                        def it = new JSONObject(params as JSONObject)
                        Iterator<?> keys = it.keys()
                        while (keys.hasNext()) {
                            String key = (String) keys.next()
                            json.put(key, it.get(key))
                        }
                    }
                } catch (Exception e) {
//                    printlnException(e, '')
                }
            }
            json.remove(col_json)
        }
        return json
    }

//Chuyển cột date thành định dạng chuẩn
    def processJSONFormatDate(JSONObject json, def list_col_date) {
        for (col in list_col_date) {
            def colData = json.get(col) as String
            if (colData) {
                json.remove(col)
                String key = (String) col

                json.put(key, dateTimeUtilService.formatDateToString(dateTimeUtilService.formatStringToDate(
                        colData, "yyyy-MM-dd'T'HH:mm:ss'Z'", Constant.TIMEZONE_VN), "dd/MM/yyyy"))
            }
        }
        return json
    }

    def underscoreString(String s) {
        return s.replaceAll(/\B[A-Z]/) { '_' + it }.toLowerCase()
    }

//
/**
 * Generate Params
 * @param flatProfile
 * @param comboParams
 * @param textParams
 * @param booleanParams
 * @param numberParams
 * @return array params
 */
    def generateParam(Map flatProfile, def params) {
        def result = [:]
        params?.comboParams?.each { cp ->
            if (cp != null) {
                if (flatProfile.containsKey(cp.code))
//                if (flatProfile.containsKey(cp.code) && flatProfile."$cp.code")
                    result.put(cp.code, getIdFromComboParams(cp.code, flatProfile."${cp.code}"))
            }
        }
        params?.textParams?.each { tp ->
            if (tp != null)
                result.put(tp.code, flatProfile."${tp.code}")
        }

        params?.booleanParams?.each { boolp ->
            if (boolp != null) {
                result.put(boolp.code, flatProfile."${boolp.code}")
            }

        }

        params?.numberParams?.each { np ->
            if (np != null && flatProfile."${np.code}" != null)
                result.put(np.code, flatProfile."${np.code}".toString().substring(0, flatProfile."${np.code}".toString().lastIndexOf(".")))
        }

        params?.checkBoxParams?.each { ckp ->
            if (ckp != null)
                result.put(ckp.code, flatProfile."${ckp.code}")
        }

        params?.currencyParams?.each { crp ->
            if (crp != null)
                result.put(crp.code, flatProfile."${crp.code}")
        }

        return result
    }

    def getIdFromComboParams(def code, def obj) {
        try {
            return obj?.id
        } catch (Exception e) {
            return "Lỗi lấy id của combo params: ${code}"
        }
    }

    def formatStringToDate(String para, String type) {
        if (para) {
            SimpleDateFormat format = new SimpleDateFormat(type)
            Date date = format.parse(para)
            return date
        } else {
            return null
        }

    }

    def formatDateToString(Date date, String type) {
        if (date) {
            SimpleDateFormat format = new SimpleDateFormat(type)
            String strDate = format.format(date)
            return strDate
        } else {
            return ""
        }

    }

    def formatStringToBigDecimal(String para) {
        BigDecimal decimal = new BigDecimal(0)
        try {
            decimal = new BigDecimal(para.trim())
        }
        catch (Exception e) {
            decimal = new BigDecimal(0)
        }
        return decimal
    }

    def formatStringToInteger(String para) {
        Integer num = 0
        try {
            num = Integer.valueOf(para.trim())
        }
        catch (Exception e) {
            num = 0
        }
        return num
    }

    def formatStringToLong(String para) {
        Long num = 0
        try {
            num = Long.valueOf(para.trim())
        }
        catch (Exception e) {
            num = 0
        }
        return num
    }

    def formatStringToDouble(String para) {
        Double num = 0
        try {
            num = Double.valueOf(para.trim())
        }
        catch (Exception e) {
            num = 0
        }
        return num
    }

    def addSeconds(Date date, int seconds) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        cal.add(Calendar.SECOND, seconds);
        return cal.getTime();
    }

    def decodeStrToByte(String strBase64) {
        if (Base64.isBase64(strBase64)) {
            byte[] bytes = Base64.decodeBase64(strBase64)
            return bytes
        }
    }

    def processOperator(String operator) {
        switch (operator.trim()) {
            case 'eq':
                return "="
            case 'ne':
                return "<>"
            case 'gt':
                return ">="
            case 'lt':
                return "<="
            case 'like':
                return "like"
            case 'ilike':
                return "like"
            case 'in':
                return "in"
        }
    }

    def validateEmail(String email) {
        String EMAIL_PATTERN =
                "^(?=.{1,64}@)[A-Za-z0-9_-]+(\\.[A-Za-z0-9_-]+)*@[^-][A-Za-z0-9-]+(\\.[A-Za-z0-9-]+)*(\\.[A-Za-z]{2,})\$"
        Pattern pattern = Pattern.compile(EMAIL_PATTERN)
        Matcher matcher = pattern.matcher(email)
        return matcher.matches()
    }

    def validateAndSave(def saveObj, String name) {
        if (!saveObj.validate()) {
            saveObj.errors.each {
                println "ERROR WHEN VALIDATING " + name
                println it
            }
        } else {
            if (!saveObj.save(flush: true)) {
                saveObj.errors.each {
                    println "ERROR WHEN SAVING " + name
                    println it
                }
            }
        }
    }

    def formatNumber(def numberA, def numberB, def item) {
        BigDecimal data = numberA / numberB
        def result
        switch (item) {
            case 0:
                result = numberA / numberB
                return result
            case 1:
                result = ((BigDecimal) Math.round(data * 10) / 10)
                return result
            case 2:
                result = ((BigDecimal) Math.round(data * 100) / 100)
                return result
            case 3:
                result = ((BigDecimal) Math.round(data * 1000) / 1000)
                return result
            case 4:
                result = ((BigDecimal) Math.round(data * 10000) / 10000)
                return result
            case 5:
                result = ((BigDecimal) Math.round(data * 100000) / 100000)
                return result
            case 6:
                result = ((BigDecimal) Math.round(data * 1000000) / 1000000)
                return result
            case 7:
                result = ((BigDecimal) Math.round(data * 10000000) / 10000000)
                return result
            case 8:
                result = ((BigDecimal) Math.round(data * 100000000) / 100000000)
                return result
        }
    }

    def formatNumberMulti(def numberA, def numberB, def item) {
        BigDecimal data = numberA * numberB
        def result
        switch (item) {
            case 0:
                result = numberA * numberB
                return result
            case 1:
                result = ((BigDecimal) Math.round(data * 10) / 10)
                return result
            case 2:
                result = ((BigDecimal) Math.round(data * 100) / 100)
                return result
            case 3:
                result = ((BigDecimal) Math.round(data * 1000) / 1000)
                return result
            case 4:
                result = ((BigDecimal) Math.round(data * 10000) / 10000)
                return result
            case 5:
                result = ((BigDecimal) Math.round(data * 100000) / 100000)
                return result
            case 6:
                result = ((BigDecimal) Math.round(data * 1000000) / 1000000)
                return result
            case 7:
                result = ((BigDecimal) Math.round(data * 10000000) / 10000000)
                return result
            case 8:
                result = ((BigDecimal) Math.round(data * 100000000) / 100000000)
                return result
        }
    }

    def getFileWithBase64(def file) {
        try {
            if (!file.empty && file.getSize() <= 12582912) {
                byte[] encoded = java.util.Base64.getEncoder().encode(file.bytes)
                String outputStream = new String(encoded)
                return outputStream
            }
            return ''
        }
        catch (Exception e) {
            return ''
        }
    }

    /**
     * Xử lý file upload
     * @param type
     * @param maxfilesize
     * check null
     * check định dạng
     * check dung lượng
     * check tạo thư mục lưu thành công
     */
    def static uploadfile(def fileupload, def type, def maxfilesize, String file_upload_type) {
        try {
            def sampleMap = [:]
            //1. check null file
            if (fileupload) {
                //CommonsMultipartFile file = fileupload
                MultipartFile file = fileupload
                if (!file.empty) {
                    //
                    Date date = Calendar.getInstance().getTime();
                    DateFormat monthFormat = new SimpleDateFormat("yyyyMM");
                    def currentMonth = monthFormat.format(date);
                    DateFormat dateFormat = new SimpleDateFormat("yyyyMMdd");
                    def currentDate = dateFormat.format(date);
                    //
                    def currentTime = System.currentTimeMillis()
                    def fileName = file.getOriginalFilename().replaceAll(' ', '_')
                    //2. check file type
                    String extension = ""
                    int i = fileName.lastIndexOf('.')
                    if (i > 0) {
                        extension = fileName.substring(i + 1).toLowerCase()
                    }
                    String[] filetype = file_upload_type.split(",")
                    int checkfile = 0
                    for (int j = 0; j < filetype.size(); j++) {
                        if (extension == filetype[j] || extension.equals(filetype[j])) {
                            checkfile++
                        }
                    }
                    if (checkfile > 0) {
                        //3. check file size
                        if (file.getSize() <= maxfilesize) {
                            def path_dynamic = "\\" + currentMonth + "\\" + currentDate + "\\" + currentTime
                            def path = com.tcb.system.common.Conf.findByLabel("UploadDir").value + path_dynamic
                            File f = new File(path)
                            if (f.mkdirs()) {
                                path_dynamic = path_dynamic + "\\" + fileName
                                path = path + "\\" + fileName
                                file.transferTo(new File(path))
                                sampleMap = ['filename': fileName, 'extension': extension, 'path': path_dynamic, 'fullpath': path, 'filesize': file.getSize()]
                            }
                        }
                    }
                }
            }
            return sampleMap
        } catch (Exception e) {
            throw e
        }
    }
}
