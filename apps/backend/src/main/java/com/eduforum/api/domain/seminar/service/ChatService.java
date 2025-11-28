package com.eduforum.api.domain.seminar.service;

import com.eduforum.api.common.exception.BusinessException;
import com.eduforum.api.common.exception.ErrorCode;
import com.eduforum.api.domain.auth.entity.User;
import com.eduforum.api.domain.auth.repository.UserRepository;
import com.eduforum.api.domain.seminar.dto.ChatMessageRequest;
import com.eduforum.api.domain.seminar.dto.ChatMessageResponse;
import com.eduforum.api.domain.seminar.entity.ChatMessage;
import com.eduforum.api.domain.seminar.entity.MessageType;
import com.eduforum.api.domain.seminar.entity.SeminarRoom;
import com.eduforum.api.domain.seminar.repository.ChatMessageRepository;
import com.eduforum.api.domain.seminar.repository.RoomParticipantRepository;
import com.eduforum.api.domain.seminar.repository.SeminarRoomRepository;
import com.eduforum.api.domain.seminar.websocket.WebSocketEventPublisher;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Service for managing chat messages
 */
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
@Slf4j
public class ChatService {

    private final ChatMessageRepository messageRepository;
    private final SeminarRoomRepository roomRepository;
    private final UserRepository userRepository;
    private final RoomParticipantRepository participantRepository;
    private final WebSocketEventPublisher eventPublisher;

    /**
     * Send chat message
     */
    @Transactional
    public ChatMessageResponse sendMessage(Long userId, ChatMessageRequest request) {
        log.info("User {} sending message to room {}", userId, request.getRoomId());

        // Get room
        SeminarRoom room = roomRepository.findById(request.getRoomId())
            .orElseThrow(() -> new BusinessException(ErrorCode.ROOM_NOT_FOUND));

        // Verify user is in room
        if (!participantRepository.isUserInRoom(request.getRoomId(), userId)) {
            throw new BusinessException(ErrorCode.NOT_IN_ROOM);
        }

        // Check if chat is enabled
        Boolean chatEnabled = (Boolean) room.getSettings().getOrDefault("allowChat", true);
        if (!chatEnabled) {
            throw new BusinessException(ErrorCode.CHAT_DISABLED);
        }

        // Get sender
        User sender = userRepository.findById(userId)
            .orElseThrow(() -> new BusinessException(ErrorCode.USER_NOT_FOUND));

        // Create message
        ChatMessage message = ChatMessage.builder()
            .room(room)
            .sender(sender)
            .messageType(MessageType.TEXT)
            .content(request.getContent())
            .build();

        message = messageRepository.save(message);

        log.info("Message {} created in room {}", message.getId(), request.getRoomId());
        return mapToResponse(message);
    }

    /**
     * Send system message
     */
    @Transactional
    public ChatMessageResponse sendSystemMessage(Long roomId, String content) {
        log.info("Sending system message to room {}", roomId);

        SeminarRoom room = roomRepository.findById(roomId)
            .orElseThrow(() -> new BusinessException(ErrorCode.ROOM_NOT_FOUND));

        ChatMessage message = ChatMessage.builder()
            .room(room)
            .messageType(MessageType.SYSTEM)
            .content(content)
            .build();

        message = messageRepository.save(message);
        return mapToResponse(message);
    }

    /**
     * Upload file to chat
     */
    @Transactional
    public ChatMessageResponse uploadFile(Long userId, Long roomId, String fileName, String fileUrl, Long fileSize) {
        log.info("User {} uploading file {} to room {}", userId, fileName, roomId);

        // Get room
        SeminarRoom room = roomRepository.findById(roomId)
            .orElseThrow(() -> new BusinessException(ErrorCode.ROOM_NOT_FOUND));

        // Verify user is in room
        if (!participantRepository.isUserInRoom(roomId, userId)) {
            throw new BusinessException(ErrorCode.NOT_IN_ROOM);
        }

        // Get sender
        User sender = userRepository.findById(userId)
            .orElseThrow(() -> new BusinessException(ErrorCode.USER_NOT_FOUND));

        // Create file message
        ChatMessage message = ChatMessage.builder()
            .room(room)
            .sender(sender)
            .messageType(MessageType.FILE)
            .content("Shared a file: " + fileName)
            .fileUrl(fileUrl)
            .fileName(fileName)
            .fileSize(fileSize)
            .build();

        message = messageRepository.save(message);

        // Broadcast file shared event
        ChatMessageResponse response = mapToResponse(message);
        eventPublisher.broadcastChatMessage(roomId, response);

        log.info("File message {} created in room {}", message.getId(), roomId);
        return response;
    }

    /**
     * Get chat history (paginated)
     */
    public Page<ChatMessageResponse> getMessages(Long roomId, Pageable pageable) {
        Page<ChatMessage> messages = messageRepository.findByRoomId(roomId, pageable);
        return messages.map(this::mapToResponse);
    }

    /**
     * Get recent messages
     */
    public List<ChatMessageResponse> getRecentMessages(Long roomId, int limit) {
        Pageable pageable = PageRequest.of(0, limit);
        List<ChatMessage> messages = messageRepository.findRecentMessages(roomId, pageable);
        return messages.stream()
            .map(this::mapToResponse)
            .collect(Collectors.toList());
    }

    /**
     * Get messages since timestamp (for real-time sync)
     */
    public List<ChatMessageResponse> getMessagesSince(Long roomId, OffsetDateTime since) {
        List<ChatMessage> messages = messageRepository.findMessagesSince(roomId, since);
        return messages.stream()
            .map(this::mapToResponse)
            .collect(Collectors.toList());
    }

    /**
     * Get files shared in room
     */
    public List<ChatMessageResponse> getSharedFiles(Long roomId) {
        List<ChatMessage> files = messageRepository.findFilesByRoomId(roomId);
        return files.stream()
            .map(this::mapToResponse)
            .collect(Collectors.toList());
    }

    /**
     * Get message count in room
     */
    public Long getMessageCount(Long roomId) {
        return messageRepository.countByRoomId(roomId);
    }

    /**
     * Map entity to response DTO
     */
    private ChatMessageResponse mapToResponse(ChatMessage message) {
        ChatMessageResponse.ChatMessageResponseBuilder builder = ChatMessageResponse.builder()
            .id(message.getId())
            .roomId(message.getRoom().getId())
            .messageType(message.getMessageType())
            .content(message.getContent())
            .fileUrl(message.getFileUrl())
            .fileName(message.getFileName())
            .fileSize(message.getFileSize())
            .createdAt(message.getCreatedAt());

        // Add sender info if not system message
        if (message.getSender() != null) {
            builder.senderId(message.getSender().getId())
                   .senderName(message.getSender().getName());
        }

        return builder.build();
    }
}
