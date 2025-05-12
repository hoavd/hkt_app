import javax.servlet.http.HttpServletResponse

// Place your Spring DSL code here
beans = {
    //websocket config
    webSocketConfig(CustomWebSocketConfig)
    textWebSocketHandler(MyWebSocketHandler)
    //--

    userPasswordEncoderListener(UserPasswordEncoderListener)
    tokenCreationEventListener(TokenCreationEventListener)
    accessTokenJsonRenderer(CustomAccessTokenJsonRenderer)
    userPasswordEncoderListener(UserPasswordEncoderListener)

    restAuthenticationFailureHandler(CustomRestAuthenticationFailureHandler) {
        statusCode = HttpServletResponse.SC_UNAUTHORIZED
        messageSource = ref("messageSource")
    }

    restAuthenticationSuccessHandler(CustomRestAuthenticationSuccessHandler) {
        renderer = ref("accessTokenJsonRenderer")
    }
}