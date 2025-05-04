import com.tcb.system.security.User
import grails.plugin.springsecurity.rest.RestTokenCreationEvent
import org.springframework.context.ApplicationListener

class TokenCreationEventListener implements ApplicationListener<RestTokenCreationEvent> {

    void onApplicationEvent(RestTokenCreationEvent event) {
        User.withTransaction {
            User user = User.where { username == event.principal.username }.first()
            user.lastLogin = new Date()
            user.save(flush: true)
        }
    }
}