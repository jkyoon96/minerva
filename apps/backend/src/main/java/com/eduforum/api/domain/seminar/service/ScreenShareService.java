package com.eduforum.api.domain.seminar.service;

import com.eduforum.api.common.exception.BusinessException;
import com.eduforum.api.common.exception.ErrorCode;
import com.eduforum.api.domain.seminar.dto.ParticipantResponse;
import com.eduforum.api.domain.seminar.entity.ParticipantRole;
import com.eduforum.api.domain.seminar.entity.RoomParticipant;
import com.eduforum.api.domain.seminar.entity.SeminarRoom;
import com.eduforum.api.domain.seminar.repository.RoomParticipantRepository;
import com.eduforum.api.domain.seminar.repository.SeminarRoomRepository;
import com.eduforum.api.domain.seminar.websocket.WebSocketEventPublisher;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Map;

/**
 * Service for managing screen sharing
 */
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
@Slf4j
public class ScreenShareService {

    private final RoomParticipantRepository participantRepository;
    private final SeminarRoomRepository roomRepository;
    private final WebSocketEventPublisher eventPublisher;

    /**
     * Start screen sharing
     */
    @Transactional
    public ParticipantResponse startScreenShare(Long userId, Long roomId) {
        log.info("User {} starting screen share in room {}", userId, roomId);

        // Get room
        SeminarRoom room = roomRepository.findById(roomId)
            .orElseThrow(() -> new BusinessException(ErrorCode.ROOM_NOT_FOUND));

        // Get participant
        RoomParticipant participant = participantRepository.findByRoomIdAndUserId(roomId, userId)
            .orElseThrow(() -> new BusinessException(ErrorCode.NOT_IN_ROOM));

        // Check permissions
        Boolean allowScreenShare = (Boolean) room.getSettings().getOrDefault("allowScreenShare", true);
        if (!allowScreenShare && participant.getRole() == ParticipantRole.PARTICIPANT) {
            throw new BusinessException(ErrorCode.SCREEN_SHARE_NOT_ALLOWED);
        }

        // Check if someone else is sharing
        boolean someoneSharing = participantRepository.findByRoomId(roomId)
            .stream()
            .anyMatch(p -> !p.getId().equals(participant.getId()) && p.getIsScreenSharing());

        if (someoneSharing) {
            throw new BusinessException(ErrorCode.SCREEN_SHARE_IN_PROGRESS);
        }

        // Start screen sharing
        participant.startScreenShare();
        participant = participantRepository.save(participant);

        // Broadcast event
        ParticipantResponse response = mapToResponse(participant);
        eventPublisher.broadcastScreenShareStarted(roomId, userId, response);

        log.info("User {} started screen sharing in room {}", userId, roomId);
        return response;
    }

    /**
     * Stop screen sharing
     */
    @Transactional
    public ParticipantResponse stopScreenShare(Long userId, Long roomId) {
        log.info("User {} stopping screen share in room {}", userId, roomId);

        RoomParticipant participant = participantRepository.findByRoomIdAndUserId(roomId, userId)
            .orElseThrow(() -> new BusinessException(ErrorCode.NOT_IN_ROOM));

        // Stop screen sharing
        participant.stopScreenShare();
        participant = participantRepository.save(participant);

        // Broadcast event
        ParticipantResponse response = mapToResponse(participant);
        eventPublisher.broadcastScreenShareStopped(roomId, userId, response);

        log.info("User {} stopped screen sharing in room {}", userId, roomId);
        return response;
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
