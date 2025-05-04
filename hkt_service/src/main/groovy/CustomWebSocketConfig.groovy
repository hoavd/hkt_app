import grails.plugin.springwebsocket.GrailsSimpAnnotationMethodMessageHandler
import grails.util.Holders
import io.jsonwebtoken.*
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.messaging.Message
import org.springframework.messaging.MessageChannel
import org.springframework.messaging.simp.SimpMessagingTemplate
import org.springframework.messaging.simp.config.ChannelRegistration
import org.springframework.messaging.simp.config.MessageBrokerRegistry
import org.springframework.messaging.simp.stomp.StompCommand
import org.springframework.messaging.simp.stomp.StompHeaderAccessor
import org.springframework.messaging.support.ChannelInterceptor
import org.springframework.messaging.support.MessageHeaderAccessor
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.security.core.userdetails.UserDetailsService
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker
import org.springframework.web.socket.config.annotation.StompEndpointRegistry
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer

@Configuration
@EnableWebSocketMessageBroker
class CustomWebSocketConfig implements WebSocketMessageBrokerConfigurer {
    @Value('*')
    String allowedOrigin

    @Override
    void configureMessageBroker(MessageBrokerRegistry messageBrokerRegistry) {
        messageBrokerRegistry.enableSimpleBroker "/queue", "/user", "/topic"
        messageBrokerRegistry.setApplicationDestinationPrefixes "/app"
    }

    @Override
    void registerStompEndpoints(StompEndpointRegistry stompEndpointRegistry) {
//        stompEndpointRegistry.addEndpoint("/stomp").setAllowedOriginPatterns(allowedOrigin).withSockJS()
        stompEndpointRegistry.addEndpoint("/stomp").setAllowedOrigins("http://localhost:4000", "http://localhost:8090",
                "http://localhost:8080",).withSockJS()

    }

    static final String TOKEN_PREFIX = "Bearer"
    static final String SIGNING_KEY = "tcbfxtK6S9503Q06Y6Rfk21TErImPYqa"

    @Autowired
    UserDetailsService userDetailsService;

    @Override
    void configureClientInboundChannel(ChannelRegistration registration) {
        registration.interceptors(new ChannelInterceptor() {
            @Override
            Message<?> preSend(Message<?> message, MessageChannel channel) {
                StompHeaderAccessor accessor =
                        MessageHeaderAccessor.getAccessor(message, StompHeaderAccessor.class);
                if (StompCommand.CONNECT.equals(accessor.getCommand())) {
                    List<String> auth = accessor.getNativeHeader("Authorization");
                    try {
                        String username = Jwts.parser()
                                .setSigningKey(SIGNING_KEY.getBytes())
                                .parseClaimsJws(auth.get(0).replace(TOKEN_PREFIX, ""))
                                .getBody()
                                .getSubject()

                        UserDetails userDetails = userDetailsService.loadUserByUsername(username)
                        UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(
                                userDetails, null, userDetails.getAuthorities())
                        SecurityContextHolder.getContext().setAuthentication(authentication)
                        accessor.setUser(authentication)
                    } catch (SignatureException e) {
                        throw e
                    } catch (MalformedJwtException e) {
                        throw e
                    } catch (ExpiredJwtException e) {
                        throw e
                    } catch (UnsupportedJwtException e) {
                        throw e
                    } catch (IllegalArgumentException e) {
                        throw e
                    } catch (Exception e) {
                        throw e
                    }
                }
                return message
            }
        });
    }

    @Bean
    GrailsSimpAnnotationMethodMessageHandler grailsSimpAnnotationMethodMessageHandler(
            MessageChannel clientInboundChannel,
            MessageChannel clientOutboundChannel,
            SimpMessagingTemplate brokerMessagingTemplate
    ) {
        def handler = new GrailsSimpAnnotationMethodMessageHandler(clientInboundChannel, clientOutboundChannel, brokerMessagingTemplate)
        handler.destinationPrefixes = ["/app"]
        return handler
    }
}