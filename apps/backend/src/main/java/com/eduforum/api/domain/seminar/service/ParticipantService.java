package com.eduforum.api.domain.seminar.service;

import com.eduforum.api.common.exception.BusinessException;
import com.eduforum.api.common.exception.ErrorCode;
import com.eduforum.api.domain.auth.entity.User;
import com.eduforum.api.domain.auth.repository.UserRepository;
import com.eduforum.api.domain.seminar.dto.ParticipantResponse;
import com.eduforum.api.domain.seminar.dto.RoomJoinRequest;
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
 * Service for managing room participants
 */
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
@Slf4j
public class ParticipantService {

    private final RoomParticipantRepository participantRepository;
    private final SeminarRoomRepository roomRepository;
    private final UserRepository userRepository;
    private final WebSocketEventPublisher eventPublisher;

    /**
     * Join room
     */
    @Transactional
    public ParticipantResponse joinRoom(Long userId, RoomJoinRequest request) {
        log.info("User {} joining room {}", userId, request.getRoomId());

        // Get room
        SeminarRoom room = roomRepository.findById(request.getRoomId())
            .orElseThrow(() -> new BusinessException(ErrorCode.ROOM_NOT_FOUND));

        // Check if room has ended
        if (room.hasEnded()) {
            throw new BusinessException(ErrorCode.ROOM_ALREADY_ENDED);
        }

        // Get user
        User user = userRepository.findById(userId)
            .orElseThrow(() -> new BusinessException(ErrorCode.USER_NOT_FOUND));

        // Check if already in room
        if (participantRepository.isUserInRoom(request.getRoomId(), userId)) {
            throw new BusinessException(ErrorCode.ALREADY_IN_ROOM);
        }

        // Check room capacity
        Long activeCount = participantRepository.countActiveParticipants(request.getRoomId());
        if (activeCount >= room.getMaxParticipants()) {
            throw new BusinessException(ErrorCode.ROOM_FULL);
        }

        // Create participant
        RoomParticipant participant = RoomParticipant.builder()
            .room(room)
            .user(user)
            .role(ParticipantRole.PARTICIPANT)
            .status(room.isWaiting() ? ParticipantStatus.WAITING : ParticipantStatus.JOINED)
            .isMuted(!request.getAudioEnabled())
            .isVideoOn(request.getVideoEnabled())
            .build();

        if (!room.isWaiting()) {
            participant.join();
        }

        participant = participantRepository.save(participant);

        // Broadcast participant joined event
        ParticipantResponse response = mapToResponse(participant);
        eventPublisher.broadcastParticipantJoined(request.getRoomId(), response);

        log.info("User {} joined room {}", userId, request.getRoomId());
        return response;
    }

    /**
     * Leave room
     */
    @Transactional
    public void leaveRoom(Long userId, Long roomId) {
        log.info("User {} leaving room {}", userId, roomId);

        RoomParticipant participant = participantRepository.findByRoomIdAndUserId(roomId, userId)
            .orElseThrow(() -> new BusinessException(ErrorCode.NOT_IN_ROOM));

        // Leave room
        participant.leave();
        participantRepository.save(participant);

        // Broadcast participant left event
        ParticipantResponse response = mapToResponse(participant);
        eventPublisher.broadcastParticipantLeft(roomId, response);

        log.info("User {} left room {}", userId, roomId);
    }

    /**
     * Raise hand
     */
    @Transactional
    public ParticipantResponse raiseHand(Long userId, Long roomId) {
        log.info("User {} raising hand in room {}", userId, roomId);

        RoomParticipant participant = participantRepository.findByRoomIdAndUserId(roomId, userId)
            .orElseThrow(() -> new BusinessException(ErrorCode.NOT_IN_ROOM));

        participant.raiseHand();
        participant = participantRepository.save(participant);

        // Broadcast hand raised event
        ParticipantResponse response = mapToResponse(participant);
        eventPublisher.broadcastHandRaised(roomId, userId, response);

        return response;
    }

    /**
     * Lower hand
     */
    @Transactional
    public ParticipantResponse lowerHand(Long userId, Long roomId) {
        log.info("User {} lowering hand in room {}", userId, roomId);

        RoomParticipant participant = participantRepository.findByRoomIdAndUserId(roomId, userId)
            .orElseThrow(() -> new BusinessException(ErrorCode.NOT_IN_ROOM));

        participant.lowerHand();
        participant = participantRepository.save(participant);

        // Broadcast hand lowered event
        ParticipantResponse response = mapToResponse(participant);
        eventPublisher.broadcastHandLowered(roomId, userId, response);

        return response;
    }

    /**
     * Toggle mute
     */
    @Transactional
    public ParticipantResponse toggleMute(Long userId, Long roomId) {
        RoomParticipant participant = participantRepository.findByRoomIdAndUserId(roomId, userId)
            .orElseThrow(() -> new BusinessException(ErrorCode.NOT_IN_ROOM));

        participant.toggleMute();
        participant = participantRepository.save(participant);

        return mapToResponse(participant);
    }

    /**
     * Toggle video
     */
    @Transactional
    public ParticipantResponse toggleVideo(Long userId, Long roomId) {
        RoomParticipant participant = participantRepository.findByRoomIdAndUserId(roomId, userId)
            .orElseThrow(() -> new BusinessException(ErrorCode.NOT_IN_ROOM));

        participant.toggleVideo();
        participant = participantRepository.save(participant);

        return mapToResponse(participant);
    }

    /**
     * Get all participants in room
     */
    public List<ParticipantResponse> getParticipants(Long roomId) {
        return participantRepository.findByRoomId(roomId)
            .stream()
            .map(this::mapToResponse)
            .collect(Collectors.toList());
    }

    /**
     * Get active participants in room
     */
    public List<ParticipantResponse> getActiveParticipants(Long roomId) {
        return participantRepository.findByRoomIdAndStatus(roomId, ParticipantStatus.JOINED)
            .stream()
            .map(this::mapToResponse)
            .collect(Collectors.toList());
    }

    /**
     * Get participants with raised hands
     */
    public List<ParticipantResponse> getRaisedHands(Long roomId) {
        return participantRepository.findRaisedHandsByRoomId(roomId)
            .stream()
            .map(this::mapToResponse)
            .collect(Collectors.toList());
    }

    /**
     * Map entity to response DTO
     */
    private ParticipantResponse mapToResponse(RoomParticipant participant) {
        return ParticipantResponse.builder()
            .id(participant.getId())
            .userId(participant.getUser().getId())
            .userName(participant.getUser().getName())
            .userEmail(participant.getUser().getEmail())
            .role(participant.getRole())
            .status(participant.getStatus())
            .isHandRaised(participant.getIsHandRaised())
            .isMuted(participant.getIsMuted())
            .isVideoOn(participant.getIsVideoOn())
            .isScreenSharing(participant.getIsScreenSharing())
            .joinedAt(participant.getJoinedAt())
            .leftAt(participant.getLeftAt())
            .build();
    }
}
