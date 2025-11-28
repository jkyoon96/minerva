package com.eduforum.api.domain.seminar.websocket;

import com.eduforum.api.domain.seminar.dto.WebSocketMessage;
import com.eduforum.api.domain.seminar.entity.WebSocketEventType;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Component;

/**
 * WebSocket event publisher for broadcasting real-time events
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class WebSocketEventPublisher {

    private final SimpMessagingTemplate messagingTemplate;

    /**
     * Broadcast event to all participants in a room
     */
    public <T> void broadcastToRoom(Long roomId, WebSocketEventType eventType, Long senderId, T data) {
        WebSocketMessage<T> message = WebSocketMessage.create(eventType, roomId, senderId, data);
        String destination = "/topic/room/" + roomId;

        log.debug("Broadcasting {} to room {}", eventType, roomId);
        messagingTemplate.convertAndSend(destination, message);
    }

    /**
     * Send event to specific user
     */
    public <T> void sendToUser(Long userId, WebSocketEventType eventType, Long roomId, T data) {
        WebSocketMessage<T> message = WebSocketMessage.create(eventType, roomId, null, data);
        String destination = "/queue/user/" + userId;

        log.debug("Sending {} to user {}", eventType, userId);
        messagingTemplate.convertAndSend(destination, message);
    }

    /**
     * Broadcast chat message to room
     */
    public <T> void broadcastChatMessage(Long roomId, T messageData) {
        broadcastToRoom(roomId, WebSocketEventType.CHAT_MESSAGE, null, messageData);
    }

    /**
     * Broadcast participant joined event
     */
    public <T> void broadcastParticipantJoined(Long roomId, T participantData) {
        broadcastToRoom(roomId, WebSocketEventType.PARTICIPANT_JOINED, null, participantData);
    }

    /**
     * Broadcast participant left event
     */
    public <T> void broadcastParticipantLeft(Long roomId, T participantData) {
        broadcastToRoom(roomId, WebSocketEventType.PARTICIPANT_LEFT, null, participantData);
    }

    /**
     * Broadcast hand raised event
     */
    public <T> void broadcastHandRaised(Long roomId, Long userId, T participantData) {
        broadcastToRoom(roomId, WebSocketEventType.HAND_RAISED, userId, participantData);
    }

    /**
     * Broadcast hand lowered event
     */
    public <T> void broadcastHandLowered(Long roomId, Long userId, T participantData) {
        broadcastToRoom(roomId, WebSocketEventType.HAND_LOWERED, userId, participantData);
    }

    /**
     * Broadcast reaction event
     */
    public <T> void broadcastReaction(Long roomId, Long userId, T reactionData) {
        broadcastToRoom(roomId, WebSocketEventType.REACTION, userId, reactionData);
    }

    /**
     * Broadcast screen share started event
     */
    public <T> void broadcastScreenShareStarted(Long roomId, Long userId, T data) {
        broadcastToRoom(roomId, WebSocketEventType.SCREEN_SHARE_STARTED, userId, data);
    }

    /**
     * Broadcast screen share stopped event
     */
    public <T> void broadcastScreenShareStopped(Long roomId, Long userId, T data) {
        broadcastToRoom(roomId, WebSocketEventType.SCREEN_SHARE_STOPPED, userId, data);
    }

    /**
     * Broadcast layout changed event
     */
    public <T> void broadcastLayoutChanged(Long roomId, T layoutData) {
        broadcastToRoom(roomId, WebSocketEventType.LAYOUT_CHANGED, null, layoutData);
    }

    /**
     * Broadcast room started event
     */
    public <T> void broadcastRoomStarted(Long roomId, T roomData) {
        broadcastToRoom(roomId, WebSocketEventType.ROOM_STARTED, null, roomData);
    }

    /**
     * Broadcast room ended event
     */
    public <T> void broadcastRoomEnded(Long roomId, T roomData) {
        broadcastToRoom(roomId, WebSocketEventType.ROOM_ENDED, null, roomData);
    }
}
