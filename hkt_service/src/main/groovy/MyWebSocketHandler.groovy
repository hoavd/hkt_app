import org.springframework.web.socket.WebSocketHandler
import org.springframework.web.socket.TextMessage
import org.springframework.web.socket.WebSocketSession
import org.springframework.web.socket.handler.TextWebSocketHandler

class MyWebSocketHandler extends TextWebSocketHandler {

    @Override
    void handleTextMessage(WebSocketSession session, TextMessage message) {
        println "Received message: ${message.payload}"
        // Gửi phản hồi sau khi nhận thông điệp
        session.sendMessage(new TextMessage("Message received: ${message.payload}"))
    }
}
