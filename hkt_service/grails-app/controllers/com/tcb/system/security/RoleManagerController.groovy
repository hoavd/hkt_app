package com.tcb.system.security

import commons.ResultMsgConstant
import grails.converters.JSON
import grails.plugin.springsecurity.annotation.Secured
import grails.util.Holders
import result.ServiceResult

import static org.springframework.http.HttpStatus.UNAUTHORIZED

@Secured("ROLE_ADMIN")
class RoleManagerController {
    static responseFormats = ['json', 'xml']

    def commonService
    def roleManagerService
    def springSecurityService

    def index() {
        commonService.printlnActionParams(actionUri, params, request, false)
        if (!params.offset) {
            params << [offset: 0]
        }
        if (!params.max) {
            params << [max: 999999999]
        }

        def result = roleManagerService.findRole(params)
        if (result) {
            respond([roleList: result.listRole, roleTotal: result.totalRole])
        } else {
            respond([roleList: [], roleTotal: 0])
        }
    }

    def getRole() {
        commonService.printlnActionParams(actionUri, params, request, false)
        if (params.id) {
            def result = roleManagerService.getRole(params)
            respond(result)
        } else {
            respond([])
        }
    }

    def getListModule() {
        commonService.printlnActionParams(actionUri, params, request, false)
        respond roleManagerService.getListModule(), formats: responseFormats
    }

    def getDsChucNang() {
        commonService.printlnActionParams(actionUri, params, request, false)
        if (!params.offset) {
            params << [offset: 0]
        }
        if (!params.max) {
            params << [max: 999999999]
        }
        respond roleManagerService.getDsChucNang(params)
    }

    def getDsChucNangTheoModule() {
        commonService.printlnActionParams(actionUri, params, request, false)
        if (params.roleId && params.moduleId) {
            respond roleManagerService.getDsChucNangDaPhanQuyen(params, params.moduleId)
        } else {
            response.status = 500
            render(text: [success: false, msg: "roleId và moduleId không được để trống"] as JSON, contentType: 'text/json', encoding: "UTF-8")
        }
    }

    def createRole() {
        commonService.printlnActionParams(actionUri, params, request, true, request?.JSON)
        def json = request.JSON
        def result = roleManagerService.saveRole(params, json)
        respond(result)
    }

    def editRole() {
        commonService.printlnActionParams(actionUri, params, request, true, request?.JSON)
        if (params.id) {
            def json = request.JSON
            def result = roleManagerService.saveRole(params, json)
            respond(result)
        } else {
            respond(new ServiceResult(success: false, msg: ResultMsgConstant.DATA_NOT_FOUND))
        }
    }

    def deleteRole() {
        commonService.printlnActionParams(actionUri, params, request, true)
        if (params.id) {
            def result = roleManagerService.deleteRole(params)
            respond(result)
        } else {
            respond(new ServiceResult(success: false, msg: ResultMsgConstant.DATA_NOT_FOUND))
        }

    }

    def clearCachedRequestmaps() {
        commonService.printlnActionParams(actionUri, params, request, false)
        if (springSecurityService.principal.username) {
            springSecurityService.clearCachedRequestmaps()
            def checkEnableRabbitMQ = Holders.grailsApplication.config.rabbitmq?.enabled
            if (checkEnableRabbitMQ) {
                rabbitMessagePublisher.send {
                    exchange = "requestMapExchange"
//                    routingKey = "requestMapRoutingKey"
                    body = [clear: true]
                }
            }
            respond(new ServiceResult(success: true, msg: "Thành công!"))
        } else {
            render status: UNAUTHORIZED
        }
    }

    def getListSpecializedBank() {
        commonService.printlnActionParams(actionUri, params, request, false)
        forward(controller: 'specializedBank', action: 'index')
    }

    def listTransactionType() {
        commonService.printlnActionParams(actionUri, params, request, false)
        forward(controller: 'margin', action: 'listTransactionType')
    }

    def getListRoleGroup() {
        commonService.printlnActionParams(actionUri, params, request, false)
        if (springSecurityService.principal.username) {
            if (!params.offset) {
                params << [offset: 0]
            }
            if (!params.max) {
                params << [max: 999999999]
            }

            def result = roleManagerService.findRoleGroup(params)
            if (result) {
                respond([roleGroupList: result.listRoleGroup, roleGroupTotal: result.totalRoleGroup])
            } else {
                respond([roleGroupList: [], roleGroupTotal: 0])
            }
        } else {
            render status: UNAUTHORIZED
        }

    }

    def configConditionDeal() {
        commonService.printlnActionParams(actionUri, params, request, true, request?.JSON)
        def json = request.JSON
        def result = roleManagerService.configConditionDeal(params, json)
        respond(result)
    }

    def listConditionDeal() {
        commonService.printlnActionParams(actionUri, params, request, false)
        if (!params.offset) {
            params << [offset: 0]
        }
        if (!params.max) {
            params << [max: 999999999]
        }
        params << [status: true]
        forward(controller: 'conditionDeal', action: 'index')
    }

    def configSidebarItem() {
        commonService.printlnActionParams(actionUri, params, request, true, request?.JSON)
        def json = request.JSON
        def result = roleManagerService.configSidebarItem(params, json)
        respond(result)
    }

    def listSidebarItem() {
        commonService.printlnActionParams(actionUri, params, request, false)
        if (!params.offset) {
            params << [offset: 0]
        }
        if (!params.max) {
            params << [max: 999999999]
        }
        params << [status: true]
        forward(controller: 'sidebarItem', action: 'index')
    }

    def listCurrencyPair() {
        commonService.printlnActionParams(actionUri, params, request, false)
        if (!params.offset) {
            params << [offset: 0]
        }
        if (!params.max) {
            params << [max: 999999999]
        }
        params << [status: true]
        forward(controller: 'currencyPair', action: 'index')
    }


    def listGroupCif() {
        commonService.printlnActionParams(actionUri, params, request, false)
        if (!params.offset) {
            params << [offset: 0]
        }
        if (!params.max) {
            params << [max: 999999999]
        }
        params << [status: true]
        forward(controller: 'groupCif', action: 'index')
    }

    def permissionGroupCif() {
        commonService.printlnActionParams(actionUri, params, request, true, request?.JSON)
        def json = request.JSON
        def result = roleManagerService.permissionGroupCif(params, json)
        respond(result)
    }


    def getRoleGroupCif() {
        commonService.printlnActionParams(actionUri, params, request, false)
        if (params.id) {
            def result = roleManagerService.getRoleGroupCif(params)
            respond(result)
        } else {
            respond([])
        }
    }
/**/
}
