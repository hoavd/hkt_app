//grails.plugin.springsecurity.rejectIfNoRule = false
//grails.plugin.springsecurity.fii.rejectPublicInvocations = false
spring.groovy.template.'check-template-location' = false
grails.plugin.springsecurity.filterChain.chainMap = [
        //Traditional, stateful chain
        [
                pattern: '/api/**',
                filters: 'JOINED_FILTERS,-exceptionTranslationFilter,-authenticationProcessingFilter,-securityContextPersistenceFilter,-rememberMeAuthenticationFilter'
        ],
        //Stateless chain
        [
                pattern: '/**',
                filters: 'JOINED_FILTERS,-restTokenValidationFilter,-restExceptionTranslationFilter'
        ]
]
grails.plugin.springsecurity.controllerAnnotations.chainMap = [
        //Traditional, stateful chain
        [
                pattern: '/api/**',
                filters: 'JOINED_FILTERS,-exceptionTranslationFilter,-authenticationProcessingFilter,-securityContextPersistenceFilter,-rememberMeAuthenticationFilter'
        ],
        //Stateless chain
        [
                pattern: '/**',
                filters: 'JOINED_FILTERS,-restTokenValidationFilter,-restExceptionTranslationFilter'
        ]
]
grails.plugin.springsecurity.controllerAnnotations.staticRules = [
        [pattern: '/', access: ['permitAll']],
        [pattern: '/h2-console', access: ['permitAll']],
        [pattern: '/h2-console/**', access: ['permitAll']],
        [pattern: '/error', access: ['permitAll']],
        [pattern: '/index', access: ['permitAll']],
        [pattern: '/index.gsp', access: ['permitAll']],
        [pattern: '/shutdown', access: ['permitAll']],
        [pattern: '/assets/**', access: ['permitAll']],
        [pattern: '/**/js/**', access: ['permitAll']],
        [pattern: '/**/css/**', access: ['permitAll']],
        [pattern: '/**/images/**', access: ['permitAll']],
        [pattern: '/**/favicon.ico', access: ['permitAll']]
]
grails.plugin.springsecurity.interceptUrlMap = [
        [pattern: '/', access: ['permitAll']],
        [pattern: '/h2-console', access: ['permitAll']],
        [pattern: '/h2-console/**', access: ['permitAll']],
        [pattern: '/error', access: ['permitAll']],
        [pattern: '/index', access: ['permitAll']],
        [pattern: '/index.gsp', access: ['permitAll']],
        [pattern: '/shutdown', access: ['permitAll']],
        [pattern: '/assets/**', access: ['permitAll']],
        [pattern: '/**/js/**', access: ['permitAll']],
        [pattern: '/**/css/**', access: ['permitAll']],
        [pattern: '/**/images/**', access: ['permitAll']],
        [pattern: '/**/favicon.ico', access: ['permitAll']],
        [pattern: '/login/**', access: ['permitAll']],
        [pattern: '/logout/**', access: ['permitAll']]
]
grails.plugin.springsecurity.rest.token.storage.jwt.secret = 'tcbfxtK6S9503Q06Y6Rfk21TErImPYqa'
//grails.plugin.springsecurity.rest.token.storage.jwt.expiration = 36000
grails.plugin.springsecurity.rest.token.storage.jwt.useSignedJwt = true
grails.plugin.springsecurity.rest.token.validation.headerName = 'X-Auth-Token'
//grails.plugin.springsecurity.rest.token.rendering.authoritiesPropertyName = 'permissions'
grails.plugin.springsecurity.rest.login.active = true
grails.plugin.springsecurity.rest.login.endpointUrl = '/api/login'
grails.plugin.springsecurity.rest.logout.endpointUrl = '/api/logout'
grails.plugin.springsecurity.rest.login.failureStatusCode = 401
grails.plugin.springsecurity.roleHierarchy = '''
   ROLE_ADMIN > ROLE_USER
'''
