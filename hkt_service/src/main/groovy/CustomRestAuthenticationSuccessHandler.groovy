import com.tcb.log.LogAction
import grails.gorm.transactions.Transactional
import grails.plugin.springsecurity.rest.token.AccessToken
import grails.plugin.springsecurity.rest.token.rendering.AccessTokenJsonRenderer
import org.springframework.security.core.Authentication
import org.springframework.security.web.authentication.AuthenticationSuccessHandler
import javax.servlet.ServletException
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse

class CustomRestAuthenticationSuccessHandler implements AuthenticationSuccessHandler {
    AccessTokenJsonRenderer renderer
    /**
     * Called when a user has been successfully authenticated.
     *
     * @param request the request which caused the successful authentication
     * @param response the response
     * @param authentication the <tt>Authentication</tt> object which was created during the authentication process.
     */
    @Transactional
    void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException, ServletException {
        //Save log login
        LogAction log = new LogAction()
        log.username = authentication.principal.username
        log.actionname = "login"
        log.ipaddress = "${request?.remoteAddr}"
        log.parameters = [userAgent: request.getHeader("User-Agent")]
//        println request.getRequestURL().replaceAll(request.forwardURI,"")
        log.save(flush: true)

        response.contentType = 'application/json'
        response.characterEncoding = 'UTF-8'
        response.addHeader 'Cache-Control', 'no-store'
        response.addHeader 'Pragma', 'no-cache'
        response << renderer.generateJson(authentication as AccessToken)
    }
}
