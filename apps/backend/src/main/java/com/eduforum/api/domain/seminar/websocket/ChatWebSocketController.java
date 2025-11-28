package com.eduforum.api.domain.seminar.websocket;

import com.eduforum.api.domain.seminar.dto.ChatMessageRequest;
import com.eduforum.api.domain.seminar.dto.ChatMessageResponse;
import com.eduforum.api.domain.seminar.service.ChatService;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.stereotype.Controller;

import java.security.Principal;

/**
 * WebSocket controller for real-time chat
 */
@Controller
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Chat WebSocket", description = "실시간 채팅 WebSocket API")
public class ChatWebSocketController {

    private final ChatService chatService;
    private final WebSocketEventPublisher eventPublisher;

    /**
     * Handle incoming chat messages via WebSocket
     */
    @MessageMapping("/chat.send")
    public void sendMessage(@Payload ChatMessageRequest request,
                           SimpMessageHeaderAccessor headerAccessor,
                           Principal principal) {
        try {
            // Get user ID from principal (assuming JWT authentication)
            Long userId = extractUserIdFromPrincipal(principal);

            log.debug("Received chat message from user {} for room {}", userId, request.getRoomId());

            // Save message and broadcast
            ChatMessageResponse message = chatService.sendMessage(userId, request);
            eventPublisher.broadcastChatMessage(request.getRoomId(), message);

        } catch (Exception e) {
            log.error("Error processing chat message", e);
            // Could send error message back to sender
        }
    }

    private Long extractUserIdFromPrincipal(Principal principal) {
        // This would extract user ID from JWT token in principal
        // For now, returning a placeholder
        // TODO: Implement proper JWT token extraction
        return 1L;
    }
}
