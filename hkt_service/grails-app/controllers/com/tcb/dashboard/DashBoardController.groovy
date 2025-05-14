package com.tcb.dashboard

class DashBoardController {
    static responseFormats = ['json', 'xml']
    def dashBoardService
    def commonService

    def pushAlert() {
        commonService.printlnActionParams(actionUri, params, request, false)
        def json = request.JSON
        print(json)
        def result = dashBoardService.pushAlert(json)
        respond(result, status: result.httpStatus)
    }

    def pushVolume() {
        commonService.printlnActionParams(actionUri, params, request, false)
        def json = request.JSON
        def result = dashBoardService.pushVolume(json)
        respond(result, status: result.httpStatus)
    }

    def pushMessage() {
        commonService.printlnActionParams(actionUri, params, request, false)
        def json = request.JSON
        def result = dashBoardService.pushMessage(json)
        respond(result, status: result.httpStatus)
    }

    def getVolume() {
        commonService.printlnActionParams(actionUri, params, request, false)
        if (!params.offset) {
            params << [offset: 0]
        }
        if (!params.max) {
            params << [max: 600]
        }
        def result = dashBoardService.getVolume(params)
        if (result) {
            respond([list: result.data])
        } else {
            respond([list: []])
        }
    }

    def getMessage(){
        commonService.printlnActionParams(actionUri, params, request, false)
        if (!params.offset) {
            params << [offset: 0]
        }
        if (!params.max) {
            params << [max: 999999999]
        }

        def listdynamic = dashBoardService.getMessage(params)
        if (listdynamic) {
            respond([list: listdynamic.data, total: listdynamic.recordsTotal as Long])
        } else {
            respond([list: [], total: 0 as Long])
        }
    }

    def findAlert() {
        commonService.printlnActionParams(actionUri, params, request, false)
        if (!params.offset) {
            params << [offset: 0]
        }
        if (!params.max) {
            params << [max: 999999999]
        }

        def listdynamic = dashBoardService.findAlert(params)
        if (listdynamic) {
            respond([list: listdynamic.data, total: listdynamic.recordsTotal as Long])
        } else {
            respond([list: [], total: 0 as Long])
        }
    }

    def getAlert() {
        commonService.printlnActionParams(actionUri, params, request, false)
        if (params.id) {
            def result = dashBoardService.getAlert(params)
            respond(result)
        } else {
            respond([])
        }
    }

    def findSolution(){
        commonService.printlnActionParams(actionUri, params, request, false)
        def result = dashBoardService.findSolution(params)
        respond(result, status: result.httpStatus)
    }

    def saveSolution() {
        commonService.printlnActionParams(actionUri, params, request, false)
        def json = request.JSON
        def result = dashBoardService.saveSolution(json)
        respond(result, status: result.httpStatus)
    }
}
