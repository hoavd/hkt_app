package com.tcb

class UrlMappings {
    static mappings = {
        delete "/$controller/$id(.$format)?"(action:"delete")
        get "/$controller(.$format)?"(action:"index")
        get "/$controller/$id(.$format)?"(action:"show")
        post "/$controller(.$format)?"(action:"save")
        put "/$controller/$id(.$format)?"(action:"update")
        patch "/$controller/$id(.$format)?"(action:"patch")
        "/"(controller: 'application', action:'index')
        "500"(view:'/error')
        "404"(view:'/notFound')

        get '/api/user/publicInfo'(controller: 'user', action: 'getPublicInfo')
        put "/api/user/savePublicInfo"(controller: 'user', action: 'savePublicInfo')

        //--controler: fingerprint
        "/api/fingerprint/navItem"(controller: 'fingerprint', action: 'getNavItem')
        "/api/fingerprint/clearCachedRequestMaps"(controller: 'fingerprint', action: 'clearCachedRequestMaps')
        //--end

        //--dashBoard
        "/api/dashboard/pushAlert"(controller: 'dashBoard', action: 'pushAlert')
        "/api/dashboard/pushVolume"(controller: 'dashBoard', action: 'pushVolume')
        "/api/dashboard/pushMessage"(controller: 'dashBoard', action: 'pushMessage')

        get "/api/dashboard/findAlert"(controller: 'dashBoard', action: 'findAlert')
        //--
    }
}
