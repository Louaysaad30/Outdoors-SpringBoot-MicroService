package tn.esprit.spring.userservice.Config;

import org.slf4j.LoggerFactory;
import org.springframework.context.event.EventListener;
import org.springframework.web.socket.messaging.SessionDisconnectEvent;

import java.security.Principal;
import java.util.logging.Logger;


public class WebSocketEventListener {

    private static final Logger logger = (Logger) LoggerFactory.getLogger(WebSocketEventListener.class);

    @EventListener
    public void handleSessionDisconnect(SessionDisconnectEvent event) {
        Principal user = event.getUser();
        if (user != null) {
            String username = user.getName(); // or extract userId
            // Remove from connected list
//            connectedUserIds.remove(Long.parseLong(username));
//            messagingTemplate.convertAndSend("/topic/connected-users", connectedUserIds);
//            logger.info("User {} disconnected", username);
        }
    }
}
