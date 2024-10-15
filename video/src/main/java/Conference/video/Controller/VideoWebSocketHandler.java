package Conference.video.Controller;

import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class VideoWebSocketHandler extends TextWebSocketHandler {

    private Map<String, WebSocketSession> sessions = new HashMap<>();
    private static int count =0  ;

    @Override
    public void afterConnectionEstablished(WebSocketSession session) throws Exception {
       
        String userId = getUserIdFromSession(session); 
        sessions.put(userId+count++, session);

        System.out.println("Connection established with userId: " + userId);
    }

    @Override
    public void handleTextMessage(WebSocketSession session1, TextMessage message) throws Exception {
        
		String payload = message.getPayload(); 
		System.out.println("Received message: " + payload);

		sessions.forEach((id, session) -> {

			if (! session.equals(session1)) {

				try {
					session.sendMessage(new TextMessage(payload));
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
        		
        	}
        	
        });

 
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

 
    private String getTargetUserIdFromPayload(String payload) {
       
        return "targetUserId"; // Parse the actual userId from the message
    }
}
