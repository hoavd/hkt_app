package com.tcb.system.common

import grails.web.servlet.mvc.GrailsParameterMap
import groovy.sql.Sql
import org.grails.web.json.JSONArray
import org.grails.web.json.JSONObject

class ServerSideMelaninService {
    def dataSource

    def row(String query_input, GrailsParameterMap params) {
        Sql sql = new Sql(dataSource)
        int soluong = 0
        try {
            def result = []
            def rs = sql.rows(query_input.toString())
            if (rs) {
                for (r in rs) {
                    result << r
                }
            }
            if (result) {
                //soluong = result.unique().size()
                soluong = result.get("count")
            }
            return soluong
        }
        catch (Exception e) {
            e.printStackTrace()
            throw e
        } finally {
            sql.close()
        }
    }

    //replace chuoi co ky tu ' thanh '' khi chay cau lenh select
    def replace_apostrophe(String str) {
        str = str.replaceAll("\'", "''")
        return str
    }

    def getListFromQuery(String query) {
//		println "--- query input: "+query
        def re = []
        String[] selectlist_arr
        query = query
        String selectlist = ""
        String fromtable = ""
        String wherecondition = ""
        int _to_1st_FROM_length = 0 // include FROM
        int _from_last_WHERE_to_end_length = 0 // include WHERE
        // 1. tach ra select
        String[] strSplit = query.split(" FROM ")
        if (strSplit) {
            String beforfrom = strSplit[0] //truoc chu FROM đầu tiên của câu query
//			println "----beforfrom: "+beforfrom
            selectlist = beforfrom.substring(7, strSplit[0].length()) // select + 1 khoang trang = 7 ki tu
//			println "----selectlist: "+selectlist
            selectlist_arr = selectlist.split(",") // mảng những cái cần select
            _to_1st_FROM_length = strSplit[0].length() + 6 // include FROM and 2 space chars
//			println "----_to_1st_FROM_length: "+_to_1st_FROM_length
            //2. Cắt chuỗi đằng sau chữ FROM đầu tiên = cắt từ đống trước FROM trên + 6 kí tự cho chính chữ FROM (2 dấu cách) ( tại sao k dùng strSplit[1] ? Vì có thể đằng sau có nhiều chữ FROM nên bị cắt nhiều khúc )
            String afterfrom = query.substring(_to_1st_FROM_length)
//			println "----afterfrom: "+afterfrom
            //3. Tìm chữ WHERE cuối cùng của chuỗi và cắt ra
            String[] splitbyWhere = afterfrom.split(" WHERE ")
            // có thể cắt ra rất nhiều chuỗi do WHERE => chỉ lấy cái cuối cùng thôi
            int splitbyWhere_count = splitbyWhere.size()
            // = 0 thì vô lý, bằng 1 thì có 2 cái, bằng 2 thì lấy cái cuối cùng
            if (splitbyWhere_count == 0) {
                wherecondition = "1 = 1"
            } else {
                wherecondition = splitbyWhere[splitbyWhere_count - 1]
            }
//			println "----wherecondition: "+wherecondition
//			println "----query.length(): "+query.length()
            _from_last_WHERE_to_end_length = query.length() - wherecondition.length() - 7
            // include WHERE & 2 space chars

            //4. Có sau FROM đầu và WHERE cuối rồi => xác định được cái đống tablename ( có thể là inner join trong 1 đống ngoặc )
//			println "_to_1st_FROM_length: "+_to_1st_FROM_length
//			println "_from_last_WHERE_to_end_length: "+_from_last_WHERE_to_end_length

            fromtable = query.substring(_to_1st_FROM_length, _from_last_WHERE_to_end_length)
            re << selectlist_arr
            re << fromtable
            re << wherecondition
            return re
        }
    }
    //=====================================================

    def query(String query_input, def whereParam) {
        Sql sql = new Sql(dataSource)
        try {
//            println query_input
            def result = sql.rows(query_input, whereParam)
            return result
        }
        catch (Exception e) {
            e.printStackTrace()
            throw e
        } finally {
            sql.close()
        }
    }

    /**
     * @param query_input : query truyền vào phải có dạng SELECT a,b,c FROM d WHERE e
     * Hàm sẽ tự động phân tích và truyền về kết quả cần thiết
     * @return
     */
    def listdynamic(String query_input, def params, def whereParam) {
        def sql = new Sql(dataSource)
        try {
            //TYPE YOUR OWN CODE HERE
            JSONObject result = new JSONObject()
            JSONArray data = new JSONArray()
            int start = 0, length = 0, end = 0
            long recordsTotal = 0

            def abc = getListFromQuery(query_input)
            def col_array = abc[0]
            def col_array_alias = abc[0]
            String table_name = abc[1]

            def whereAndOder = (abc[2] as String).toUpperCase()
                    .split("ORDER BY")
            String wherecondition = whereAndOder[0]
            String ordercondition = ""
            if (whereAndOder.size() >= 2) {
                ordercondition = whereAndOder[1]
            }


            String cols_str = ""
            for (int i = 0; i < col_array.length; i++) {
                if (i != col_array.length - 1) {
                    cols_str += col_array[i].toString().trim() + ","
                } else {
                    cols_str += col_array[i].toString().trim()
                }
            }

            // convert dung alias hoac as name
            def arr = []
            for (int i = 0; i < col_array_alias.length; i++) {
                String ss = col_array_alias[i].toString()
                if (ss.contains(" AS ")) {
                    arr = ss.split(" AS ")
                    col_array_alias[i] = arr[1].toString().trim()
                } else if (ss.contains(".")) {
                    arr = ss.split("\\.")
                    col_array_alias[i] = arr[1].toString().trim()
                } else {
                    col_array_alias[i] = ss.trim()
                }
            }

            StringBuilder query = new StringBuilder("SELECT " + cols_str)
            query.append(" FROM " + table_name)
            if (wherecondition.equals("1 = 1")) {
                query.append(" WHERE 1=1")
            } else {
                query.append(" WHERE 1=1 AND " + wherecondition + (ordercondition ? " ORDER BY " + ordercondition : ""))
            }


            //1. recordsTotal : total record of table you want to show
            String totalrecord_query = new StringBuilder("SELECT COUNT(1) AS TOTALRECORD FROM " + table_name + " WHERE 1 = 1 AND " + wherecondition)
            def totalrecord = sql.rows(totalrecord_query.toString(), whereParam)
            if (totalrecord) {
                recordsTotal = totalrecord[0].totalrecord
            }

            //2 data
            if (params.offset || params.page) {
                if (params.page) {
                    start = Integer.parseInt(params.page) * Integer.parseInt(params.max)
                } else {
//                    start = Integer.parseInt(params.offset) * Integer.parseInt(params.max)
                    start = Integer.parseInt(params.offset)
                }
            }
            if (params.max) {
                length = params.max as int
            }

            if (params["order[0][column]"]) {
                int order_col_num = Integer.parseInt(params["order[0][column]"])
                String order_col_name = params["columns[" + order_col_num + "][name]"]
                String order_by = params["order[0][dir]"]
                query.append(" order by " + order_col_name + " " + order_by)
            }
            //kieu postgree
            query.append(" LIMIT "+length+" OFFSET "+params.offset+"")
            def resultset = sql.rows(query.toString(), whereParam)
            //25102019 xoa vi dung kieu oracle
//            end = start + length
//            StringBuilder queryTemp = new StringBuilder()
//            queryTemp.append("SELECT * FROM (SELECT zz.*, ROWNUM rnum FROM (")
//            queryTemp.append(query.toString())
//            queryTemp.append(") zz WHERE ROWNUM <= " + end + ") WHERE rnum >" + start)
//            def resultset = sql.rows(queryTemp.toString(), whereParam)

            if (resultset) {
                for (p in resultset) {
                    Map<String, String> val = new HashMap<>()
                    for (int i = 0; i < col_array_alias.length; i++) {
                        val.put(col_array_alias[i], p[col_array_alias[i]])
                    }
                    data.push(val)
                }
            }
            result.put("recordsTotal", recordsTotal)
            result.put("data", data)
            return result
        } catch (Exception e) {
            e.printStackTrace()
            throw e
        } finally {
            sql.close()
        }
    }
}
