package com.eduforum.api.domain.seminar.service;

import com.eduforum.api.common.exception.BusinessException;
import com.eduforum.api.common.exception.ErrorCode;
import com.eduforum.api.domain.auth.entity.User;
import com.eduforum.api.domain.auth.repository.UserRepository;
import com.eduforum.api.domain.seminar.dto.ReactionRequest;
import com.eduforum.api.domain.seminar.dto.ReactionResponse;
import com.eduforum.api.domain.seminar.entity.Reaction;
import com.eduforum.api.domain.seminar.entity.SeminarRoom;
import com.eduforum.api.domain.seminar.repository.ReactionRepository;
import com.eduforum.api.domain.seminar.repository.RoomParticipantRepository;
import com.eduforum.api.domain.seminar.repository.SeminarRoomRepository;
import com.eduforum.api.domain.seminar.websocket.WebSocketEventPublisher;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Service for managing reactions
 */
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
@Slf4j
public class ReactionService {

    private final ReactionRepository reactionRepository;
    private final SeminarRoomRepository roomRepository;
    private final UserRepository userRepository;
    private final RoomParticipantRepository participantRepository;
    private final WebSocketEventPublisher eventPublisher;

    /**
     * Send reaction
     */
    @Transactional
    public ReactionResponse sendReaction(Long userId, ReactionRequest request) {
        log.info("User {} sending reaction {} to room {}", userId, request.getReactionType(), request.getRoomId());

        // Get room
        SeminarRoom room = roomRepository.findById(request.getRoomId())
            .orElseThrow(() -> new BusinessException(ErrorCode.ROOM_NOT_FOUND));

        // Verify user is in room
        if (!participantRepository.isUserInRoom(request.getRoomId(), userId)) {
            throw new BusinessException(ErrorCode.NOT_IN_ROOM);
        }

        // Get user
        User user = userRepository.findById(userId)
            .orElseThrow(() -> new BusinessException(ErrorCode.USER_NOT_FOUND));

        // Create reaction
        Reaction reaction = Reaction.builder()
            .room(room)
            .user(user)
            .reactionType(request.getReactionType())
            .build();

        reaction = reactionRepository.save(reaction);

        // Broadcast reaction event
        ReactionResponse response = mapToResponse(reaction);
        eventPublisher.broadcastReaction(request.getRoomId(), userId, response);

        log.info("Reaction {} sent to room {}", reaction.getId(), request.getRoomId());
        return response;
    }

    /**
     * Get recent reactions
     */
    public List<ReactionResponse> getRecentReactions(Long roomId, int minutes) {
        OffsetDateTime since = OffsetDateTime.now().minusMinutes(minutes);
        List<Reaction> reactions = reactionRepository.findRecentReactions(roomId, since);
        return reactions.stream()
            .map(this::mapToResponse)
            .collect(Collectors.toList());
    }

    /**
     * Cleanup old reactions (scheduled task can call this)
     */
    @Transactional
    public void cleanupOldReactions(int daysOld) {
        OffsetDateTime before = OffsetDateTime.now().minusDays(daysOld);
        reactionRepository.deleteReactionsBefore(before);
        log.info("Cleaned up reactions older than {} days", daysOld);
    }

    /**
     * Map entity to response DTO
     */
    private ReactionResponse mapToResponse(Reaction reaction) {
        return ReactionResponse.builder()
            .id(reaction.getId())
            .roomId(reaction.getRoom().getId())
            .userId(reaction.getUser().getId())
            .userName(reaction.getUser().getName())
            .reactionType(reaction.getReactionType())
            .emoji(reaction.getEmoji())
            .createdAt(reaction.getCreatedAt())
            .build();
    }
}
