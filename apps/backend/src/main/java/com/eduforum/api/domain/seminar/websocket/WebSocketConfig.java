package com.eduforum.api.domain.seminar.websocket;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;

/**
 * WebSocket configuration for real-time seminar communication
 */
@Configuration
@EnableWebSocketMessageBroker
@RequiredArgsConstructor
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {

    @Override
    public void configureMessageBroker(MessageBrokerRegistry config) {
        // Enable simple memory-based message broker for broadcasting
        config.enableSimpleBroker("/topic", "/queue");

        // Prefix for messages from clients
        config.setApplicationDestinationPrefixes("/app");

        // Prefix for user-specific messages
        config.setUserDestinationPrefix("/user");
    }

    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
        // WebSocket endpoint for room communication
        registry.addEndpoint("/ws/seminar")
            .setAllowedOriginPatterns("*")
            .withSockJS();

        // WebSocket endpoint for chat
        registry.addEndpoint("/ws/chat")
            .setAllowedOriginPatterns("*")
            .withSockJS();
    }
}
