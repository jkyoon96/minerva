package com.eduforum.api.domain.seminar.service;

import com.eduforum.api.common.exception.BusinessException;
import com.eduforum.api.common.exception.ErrorCode;
import com.eduforum.api.domain.auth.entity.User;
import com.eduforum.api.domain.auth.repository.UserRepository;
import com.eduforum.api.domain.course.entity.CourseSession;
import com.eduforum.api.domain.course.repository.CourseSessionRepository;
import com.eduforum.api.domain.seminar.dto.*;
import com.eduforum.api.domain.seminar.entity.*;
import com.eduforum.api.domain.seminar.repository.RoomParticipantRepository;
import com.eduforum.api.domain.seminar.repository.SeminarRoomRepository;
import com.eduforum.api.domain.seminar.websocket.WebSocketEventPublisher;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Service for managing seminar rooms
 */
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
@Slf4j
public class RoomService {

    private final SeminarRoomRepository roomRepository;
    private final CourseSessionRepository sessionRepository;
    private final UserRepository userRepository;
    private final RoomParticipantRepository participantRepository;
    private final WebSocketEventPublisher eventPublisher;

    /**
     * Create a new seminar room for a session
     */
    @Transactional
    public RoomResponse createRoom(Long userId, RoomCreateRequest request) {
        log.info("Creating room for session {} by user {}", request.getSessionId(), userId);

        // Check if room already exists for session
        if (roomRepository.existsBySessionId(request.getSessionId())) {
            throw new BusinessException(ErrorCode.ROOM_ALREADY_EXISTS);
        }

        // Get session
        CourseSession session = sessionRepository.findById(request.getSessionId())
            .orElseThrow(() -> new BusinessException(ErrorCode.SESSION_NOT_FOUND));

        // Get host user
        User host = userRepository.findById(userId)
            .orElseThrow(() -> new BusinessException(ErrorCode.USER_NOT_FOUND));

        // Create room
        SeminarRoom room = SeminarRoom.builder()
            .session(session)
            .host(host)
            .maxParticipants(request.getMaxParticipants())
            .settings(request.getSettings() != null ? request.getSettings() : room.getSettings())
            .build();

        room = roomRepository.save(room);

        // Create host participant
        RoomParticipant hostParticipant = RoomParticipant.builder()
            .room(room)
            .user(host)
            .role(ParticipantRole.HOST)
            .status(ParticipantStatus.WAITING)
            .build();

        participantRepository.save(hostParticipant);

        log.info("Created room {} for session {}", room.getId(), session.getId());
        return mapToResponse(room);
    }

    /**
     * Get room by ID
     */
    public RoomResponse getRoom(Long roomId) {
        SeminarRoom room = roomRepository.findById(roomId)
            .orElseThrow(() -> new BusinessException(ErrorCode.ROOM_NOT_FOUND));
        return mapToResponse(room);
    }

    /**
     * Get room by session ID
     */
    public RoomResponse getRoomBySessionId(Long sessionId) {
        SeminarRoom room = roomRepository.findBySessionId(sessionId)
            .orElseThrow(() -> new BusinessException(ErrorCode.ROOM_NOT_FOUND));
        return mapToResponse(room);
    }

    /**
     * Start room (activate from waiting)
     */
    @Transactional
    public RoomResponse startRoom(Long userId, Long roomId) {
        log.info("Starting room {} by user {}", roomId, userId);

        SeminarRoom room = roomRepository.findById(roomId)
            .orElseThrow(() -> new BusinessException(ErrorCode.ROOM_NOT_FOUND));

        // Verify user is host
        if (!room.getHost().getId().equals(userId)) {
            throw new BusinessException(ErrorCode.PARTICIPANT_NOT_HOST);
        }

        // Check if already started
        if (room.isActive()) {
            throw new BusinessException(ErrorCode.ROOM_ALREADY_STARTED);
        }

        // Start room
        room.start();
        room = roomRepository.save(room);

        // Broadcast room started event
        RoomResponse response = mapToResponse(room);
        eventPublisher.broadcastRoomStarted(roomId, response);

        log.info("Room {} started", roomId);
        return response;
    }

    /**
     * End room
     */
    @Transactional
    public RoomResponse endRoom(Long userId, Long roomId) {
        log.info("Ending room {} by user {}", roomId, userId);

        SeminarRoom room = roomRepository.findById(roomId)
            .orElseThrow(() -> new BusinessException(ErrorCode.ROOM_NOT_FOUND));

        // Verify user is host
        if (!room.getHost().getId().equals(userId)) {
            throw new BusinessException(ErrorCode.PARTICIPANT_NOT_HOST);
        }

        // Check if already ended
        if (room.hasEnded()) {
            throw new BusinessException(ErrorCode.ROOM_ALREADY_ENDED);
        }

        // End room
        room.end();
        room = roomRepository.save(room);

        // Broadcast room ended event
        RoomResponse response = mapToResponse(room);
        eventPublisher.broadcastRoomEnded(roomId, response);

        log.info("Room {} ended", roomId);
        return response;
    }

    /**
     * Update room layout
     */
    @Transactional
    public RoomResponse updateLayout(Long userId, Long roomId, LayoutUpdateRequest request) {
        log.info("Updating layout for room {} by user {}", roomId, userId);

        SeminarRoom room = roomRepository.findById(roomId)
            .orElseThrow(() -> new BusinessException(ErrorCode.ROOM_NOT_FOUND));

        // Verify user is host
        if (!room.getHost().getId().equals(userId)) {
            throw new BusinessException(ErrorCode.PARTICIPANT_NOT_HOST);
        }

        // Update layout
        room.updateLayout(request.getLayout());
        room = roomRepository.save(room);

        // Broadcast layout changed event
        eventPublisher.broadcastLayoutChanged(roomId, request.getLayout());

        log.info("Layout updated to {} for room {}", request.getLayout(), roomId);
        return mapToResponse(room);
    }

    /**
     * Get current room layout
     */
    public LayoutType getLayout(Long roomId) {
        SeminarRoom room = roomRepository.findById(roomId)
            .orElseThrow(() -> new BusinessException(ErrorCode.ROOM_NOT_FOUND));
        return room.getLayout();
    }

    /**
     * Get all active rooms
     */
    public List<RoomResponse> getActiveRooms() {
        return roomRepository.findByStatus(RoomStatus.ACTIVE)
            .stream()
            .map(this::mapToResponse)
            .collect(Collectors.toList());
    }

    /**
     * Get rooms by host user
     */
    public List<RoomResponse> getRoomsByHost(Long hostId) {
        return roomRepository.findByHostId(hostId)
            .stream()
            .map(this::mapToResponse)
            .collect(Collectors.toList());
    }

    /**
     * Map entity to response DTO
     */
    private RoomResponse mapToResponse(SeminarRoom room) {
        Long participantCount = participantRepository.countActiveParticipants(room.getId());

        return RoomResponse.builder()
            .id(room.getId())
            .sessionId(room.getSession().getId())
            .hostId(room.getHost().getId())
            .hostName(room.getHost().getName())
            .status(room.getStatus())
            .maxParticipants(room.getMaxParticipants())
            .currentParticipants(participantCount.intValue())
            .startedAt(room.getStartedAt())
            .endedAt(room.getEndedAt())
            .meetingUrl(room.getMeetingUrl())
            .recordingUrl(room.getRecordingUrl())
            .layout(room.getLayout())
            .settings(room.getSettings())
            .createdAt(room.getCreatedAt())
            .updatedAt(room.getUpdatedAt())
            .build();
    }
}
