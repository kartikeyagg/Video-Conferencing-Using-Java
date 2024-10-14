package Conference.video.Controller;

import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import java.util.HashMap;
import java.util.Map;

public class VideoWebSocketHandler extends TextWebSocketHandler {

    // 
    private Map<String, WebSocketSession> sessions = new HashMap<>();

    @Override
    public void afterConnectionEstablished(WebSocketSession session) throws Exception {
       
        String userId = getUserIdFromSession(session); 
        sessions.put(userId, session);

        System.out.println("Connection established with userId: " + userId);
    }

    @Override
    public void handleTextMessage(WebSocketSession session, TextMessage message) throws Exception {
        
        String payload = message.getPayload();
        System.out.println("Received message: " + payload);

     
        String targetUserId = getTargetUserIdFromPayload(payload); 
        WebSocketSession targetSession = sessions.get(targetUserId);

        if (targetSession != null && targetSession.isOpen()) {
            targetSession.sendMessage(new TextMessage(payload));
        }
    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) throws Exception {
        
        String userId = getUserIdFromSession(session);
        sessions.remove(userId);

        System.out.println("Connection closed with userId: " + userId);
    }


    private String getUserIdFromSession(WebSocketSession session) {
       
        return session.getUri().getQuery().split("=")[1];
    }

 
