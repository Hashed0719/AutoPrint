package com.example.autoprint.websocket;

import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@RestController
public class WebSocketController {

    private static final Logger logger = LoggerFactory.getLogger(WebSocketController.class);
    private final Map<String, String> activeConnections = new ConcurrentHashMap<>();

    @Autowired
    private SimpMessagingTemplate messagingTemplate;

    @MessageMapping("/register")
    public void register(@Payload String clientId, SimpMessageHeaderAccessor headerAccessor) {
        String sessionId = headerAccessor.getSessionId();
        if (sessionId != null) {
            activeConnections.put(sessionId, clientId);
            broadcastConnections();
            logger.info("New client registered: {} with session ID: {}", clientId, sessionId);
        }
    }

    @MessageMapping("/unregister")
    public void unregister(SimpMessageHeaderAccessor headerAccessor) {
        String sessionId = headerAccessor.getSessionId();
        if (sessionId != null) {
            String clientId = activeConnections.remove(sessionId);
            if (clientId != null) {
                broadcastConnections();
                logger.info("Client disconnected: {} with session ID: {}", clientId, sessionId);
            }
        }
    }

    @GetMapping("/api/websockets")
    public Map<String, String> getActiveConnections() {
        return activeConnections;
    }

    private void broadcastConnections() {
        messagingTemplate.convertAndSend("/topic/connections", activeConnections);
    }
}
