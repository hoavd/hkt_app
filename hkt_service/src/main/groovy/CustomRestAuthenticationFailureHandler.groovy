import grails.plugin.springsecurity.rest.token.storage.TokenNotFoundException
import grails.util.Holders
import groovy.json.JsonBuilder
import org.springframework.context.MessageSource
import org.springframework.security.authentication.*
import org.springframework.security.core.AuthenticationException
import org.springframework.security.web.authentication.AuthenticationFailureHandler

import javax.servlet.ServletException
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse

class CustomRestAuthenticationFailureHandler implements AuthenticationFailureHandler {
/**
 * Configurable status code, by default: conf.rest.login.failureStatusCode?:HttpServletResponse.SC_FORBIDDEN
 */
    Integer statusCode

    MessageSource messageSource

    /**
     * Called when an authentication attempt fails.
     * @param request the request during which the authentication attempt occurred.
     * @param response the response.
     * @param exception the exception which was thrown to reject the authentication request.
     */
    void onAuthenticationFailure(HttpServletRequest request, HttpServletResponse response, AuthenticationException exception) throws IOException, ServletException {
        response.setStatus(statusCode)
        response.addHeader('WWW-Authenticate', Holders.config.get("grails.plugin.springsecurity.rest.token.validation.headerName").toString())
        def errorMessage
        if (exception instanceof AccountExpiredException) {
//            errorMessage = messageSource.getMessage("springSecurity.errors.login.expired", null as Object[], LocaleContextHolder.getLocale())
            errorMessage = "Tài khoản đã hết hạn!"
        } else if (exception instanceof CredentialsExpiredException) {
//            errorMessage = messageSource.getMessage("springSecurity.errors.login.passwordExpired", null as Object[], LocaleContextHolder.getLocale())
            errorMessage = "Mật khẩu hết hạn!"
        } else if (exception instanceof DisabledException) {
//            errorMessage = messageSource.getMessage("springSecurity.errors.login.disabled", null as Object[], LocaleContextHolder.getLocale())
            errorMessage = "Tài khoản đã bị vô hiệu hóa!"
        } else if (exception instanceof LockedException) {
//            errorMessage = messageSource.getMessage("springSecurity.errors.login.locked", null as Object[], LocaleContextHolder.getLocale())
            errorMessage = "Tài khoản đã bị khóa!"
        } else if (exception instanceof InternalAuthenticationServiceException) {
//            errorMessage = messageSource.getMessage("springSecurity.errors.login.locked", null as Object[], LocaleContextHolder.getLocale())
            errorMessage = "Kết nối với máy chủ xác thực bị gián đoạn!"
        } else if (exception instanceof BadCredentialsException) {
            //            errorMessage = messageSource.getMessage("springSecurity.errors.login.fail", null as Object[], LocaleContextHolder.getLocale())
            errorMessage = "Thông tin tài khoản hoặc mật khẩu không chính xác!"
        } else if (exception instanceof TokenNotFoundException) {
            errorMessage = "Token hết hạn hoặc không hợp lệ!"
        } else {
            errorMessage = exception.message
        }
        PrintWriter out = response.getWriter()
        response.setContentType("application/json")
        response.setCharacterEncoding("UTF-8")
        out.print(new JsonBuilder([message: errorMessage]).toString())
        out.flush()
    }
}
