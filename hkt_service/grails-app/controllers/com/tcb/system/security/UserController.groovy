package com.tcb.system.security

import commons.ResultMsgConstant
import grails.converters.JSON
import result.ServiceResult
import javax.servlet.ServletContext
import static org.springframework.http.HttpStatus.UNAUTHORIZED

class UserController {
    static responseFormats = ['json', 'xml']
    def userService
    def commonService

    def index() {
        commonService.printlnActionParams(actionUri, params, request, false)
        if (!params.offset) {
            params << [offset: 0]
        }
        if (!params.max) {
            params << [max: 999999999]
        }
        def result = userService.findListUser(params, false)
        if (result) {
            respond([userList: result.data, userTotal: result.recordsTotal as Long])
        } else {
            respond([userList: [], userTotal: 0 as Long])
        }
    }

    def getPublicInfo() {
        commonService.printlnActionParams(actionUri, params, request, false)
        if (params.username) {
            render userService.getPublicInfo(params) as JSON
        } else {
            render status: UNAUTHORIZED
        }
    }

    def savePublicInfo() {
        commonService.printlnActionParams(actionUri, params, request, false, null)
        def json = request.JSON
        def result = userService.savePublicInfo(json)
        respond(result)
    }

    def checkAuth() {
        commonService.printlnActionParams(actionUri, params, request, false)
        render(text: [success: true] as JSON, contentType: 'text/json', encoding: "UTF-8")
    }

    def getUser() {
        commonService.printlnActionParams(actionUri, params, request, false)
        if (params.id) {
            def result = userService.getUser(params)
            respond(result)
        } else {
            respond([])
        }
    }

    def createUser() {
        def json = request.JSON
        def result = userService.saveUser(params, json)
        json.remove('password')
        commonService.printlnActionParams(actionUri, params, request, true, json)
        respond(result)
    }

    def editUser() {
        if (params.id) {
            def json = request.JSON
            def result = userService.saveUser(params, json)
            json.remove('password')
            commonService.printlnActionParams(actionUri, params, request, true, json)
            respond(result)
        } else {
            commonService.printlnActionParams(actionUri, params, request, true, null)
            respond(new ServiceResult(success: false, msg: ResultMsgConstant.DATA_NOT_FOUND))
        }
    }

    def changePassWord() {
        commonService.printlnActionParams(actionUri, params, request, true, null)
        def json = request.JSON
        def result = userService.changePassWord(json)
        respond(result)
    }

    def deleteUser() {
        commonService.printlnActionParams(actionUri, params, request, true)
        if (params.id) {
            def result = userService.deleteUser(params)
            respond(result)
        } else {
            respond(new ServiceResult(success: false, msg: ResultMsgConstant.DATA_NOT_FOUND))
        }
    }

    def listRole() {
        params << [active: true]
        forward(controller: 'roleManager', action: 'index')
    }

    def downloadFileHDSD() {
        commonService.printlnActionParams(actionUri, params, request, false)
        try {
            ServletContext servletContext = getServletContext()
            String contextPath = servletContext.getRealPath(File.separator)
            String path = contextPath
            path += "${File.separator}template${File.separator}DocTemplate${File.separator}TNTECH_QLTS_Tai lieu huong dan su dung.docx"

            def file = new File(path)
            if (file.exists()) {
                response.setContentType("application/octet-stream")
                response.setHeader("Content-disposition", "filename=\"${file.name}\"")
                response.outputStream << file.bytes
            }
        } catch (Exception e) {
            throw e
        }
    }

    def getListRoleGroup() {
        commonService.printlnActionParams(actionUri, params, request, false)
        def result = userService.getListRoleGroup()
        if (result) {
            respond([dataList: result, dataTotal: result.size()])
        } else {
            respond([roleGroupList: []])
        }
    }

    def getListBranch() {
        commonService.printlnActionParams(actionUri, params, request, false)
        params << [autocomplete: true]
        params << [active: "true"]
        forward(controller: 'branch', action: 'index')
    }
}
