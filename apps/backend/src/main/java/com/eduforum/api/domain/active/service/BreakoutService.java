package com.eduforum.api.domain.active.service;

import com.eduforum.api.common.exception.BusinessException;
import com.eduforum.api.common.exception.ErrorCode;
import com.eduforum.api.domain.active.dto.breakout.*;
import com.eduforum.api.domain.active.entity.*;
import com.eduforum.api.domain.active.repository.*;
import com.eduforum.api.domain.auth.entity.User;
import com.eduforum.api.domain.auth.repository.UserRepository;
import com.eduforum.api.domain.seminar.entity.SeminarRoom;
import com.eduforum.api.domain.seminar.repository.SeminarRoomRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.OffsetDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
@Slf4j
public class BreakoutService {

    private final BreakoutRoomRepository breakoutRoomRepository;
    private final BreakoutParticipantRepository breakoutParticipantRepository;
    private final SeminarRoomRepository seminarRoomRepository;
    private final UserRepository userRepository;

    @Transactional
    public BreakoutResponse createBreakoutRoom(Long userId, Long seminarRoomId, CreateBreakoutRequest request) {
        log.info("Creating breakout room for seminar {} by user {}", seminarRoomId, userId);

        SeminarRoom seminarRoom = seminarRoomRepository.findById(seminarRoomId)
            .orElseThrow(() -> new BusinessException(ErrorCode.ENTITY_NOT_FOUND));

        BreakoutRoom breakoutRoom = BreakoutRoom.builder()
            .seminarRoom(seminarRoom)
            .name(request.getName())
            .assignmentMethod(request.getAssignmentMethod())
            .maxParticipants(request.getMaxParticipants())
            .durationMinutes(request.getDurationMinutes())
            .meetingUrl(request.getMeetingUrl())
            .settings(request.getSettings() != null ? request.getSettings() : Map.of())
            .build();

        breakoutRoom = breakoutRoomRepository.save(breakoutRoom);
        log.info("Created breakout room {}", breakoutRoom.getId());
        return mapToResponse(breakoutRoom);
    }

    public List<BreakoutResponse> getBreakoutRooms(Long seminarRoomId) {
        return breakoutRoomRepository.findBySeminarRoomIdAndNotDeleted(seminarRoomId).stream()
            .map(this::mapToResponse)
            .collect(Collectors.toList());
    }

    public BreakoutResponse getBreakoutRoom(Long breakoutRoomId) {
        BreakoutRoom breakoutRoom = breakoutRoomRepository.findByIdAndNotDeleted(breakoutRoomId)
            .orElseThrow(() -> new BusinessException(ErrorCode.ENTITY_NOT_FOUND));
        return mapToResponse(breakoutRoom);
    }

    @Transactional
    public BreakoutResponse updateBreakoutRoom(Long userId, Long breakoutRoomId, CreateBreakoutRequest request) {
        BreakoutRoom breakoutRoom = breakoutRoomRepository.findByIdAndNotDeleted(breakoutRoomId)
            .orElseThrow(() -> new BusinessException(ErrorCode.ENTITY_NOT_FOUND));

        breakoutRoom.setName(request.getName());
        breakoutRoom.setAssignmentMethod(request.getAssignmentMethod());
        breakoutRoom.setMaxParticipants(request.getMaxParticipants());
        breakoutRoom.setDurationMinutes(request.getDurationMinutes());
        breakoutRoom.setMeetingUrl(request.getMeetingUrl());
        breakoutRoom.setSettings(request.getSettings() != null ? request.getSettings() : Map.of());

        breakoutRoom = breakoutRoomRepository.save(breakoutRoom);
        return mapToResponse(breakoutRoom);
    }

    @Transactional
    public void deleteBreakoutRoom(Long userId, Long breakoutRoomId) {
        BreakoutRoom breakoutRoom = breakoutRoomRepository.findByIdAndNotDeleted(breakoutRoomId)
            .orElseThrow(() -> new BusinessException(ErrorCode.ENTITY_NOT_FOUND));

        if (breakoutRoom.isActive()) {
            throw new BusinessException(ErrorCode.INVALID_INPUT_VALUE, "Cannot delete active breakout room");
        }

        breakoutRoom.delete();
        breakoutRoomRepository.save(breakoutRoom);
    }

    @Transactional
    public void assignParticipants(Long userId, Long seminarRoomId, AssignParticipantsRequest request) {
        log.info("Assigning participants to breakout rooms for seminar {}", seminarRoomId);

        List<BreakoutRoom> breakoutRooms = breakoutRoomRepository.findBySeminarRoomIdAndNotDeleted(seminarRoomId);
        List<User> participants = userRepository.findAllById(request.getParticipantIds());

        if (participants.size() != request.getParticipantIds().size()) {
            throw new BusinessException(ErrorCode.INVALID_INPUT_VALUE, "Some participant IDs are invalid");
        }

        // Clear existing assignments if requested
        if (request.getClearExisting() != null && request.getClearExisting()) {
            breakoutParticipantRepository.deleteByBreakoutRoomIn(breakoutRooms);
        }

        AssignmentMethod method = request.getAssignmentMethod() != null
            ? request.getAssignmentMethod()
            : AssignmentMethod.RANDOM;

        switch (method) {
            case RANDOM -> assignRandomly(breakoutRooms, participants);
            case BALANCED -> assignBalanced(breakoutRooms, participants);
            case MANUAL -> assignManually(breakoutRooms, participants, request.getManualAssignments());
            default -> throw new BusinessException(ErrorCode.INVALID_INPUT_VALUE, "Invalid assignment method");
        }

        log.info("Assigned {} participants to {} breakout rooms", participants.size(), breakoutRooms.size());
    }

    private void assignRandomly(List<BreakoutRoom> rooms, List<User> participants) {
        List<User> shuffled = new ArrayList<>(participants);
        Collections.shuffle(shuffled);

        int roomIndex = 0;
        for (User user : shuffled) {
            BreakoutRoom room = rooms.get(roomIndex % rooms.size());

            // Check max participants
            if (room.getMaxParticipants() != null) {
                long currentCount = breakoutParticipantRepository.countByBreakoutRoomId(room.getId());
                if (currentCount >= room.getMaxParticipants()) {
                    roomIndex++;
                    room = rooms.get(roomIndex % rooms.size());
                }
            }

            BreakoutParticipant participant = BreakoutParticipant.builder()
                .breakoutRoom(room)
                .user(user)
                .build();

            breakoutParticipantRepository.save(participant);
            roomIndex++;
        }
    }

    private void assignBalanced(List<BreakoutRoom> rooms, List<User> participants) {
        // For balanced assignment, distribute evenly
        int participantsPerRoom = participants.size() / rooms.size();
        int remainder = participants.size() % rooms.size();

        int participantIndex = 0;
        for (int i = 0; i < rooms.size(); i++) {
            BreakoutRoom room = rooms.get(i);
            int count = participantsPerRoom + (i < remainder ? 1 : 0);

            for (int j = 0; j < count && participantIndex < participants.size(); j++) {
                User user = participants.get(participantIndex++);
                BreakoutParticipant participant = BreakoutParticipant.builder()
                    .breakoutRoom(room)
                    .user(user)
                    .build();

                breakoutParticipantRepository.save(participant);
            }
        }
    }

    private void assignManually(List<BreakoutRoom> rooms, List<User> participants,
                                Map<Long, List<Long>> manualAssignments) {
        if (manualAssignments == null || manualAssignments.isEmpty()) {
            throw new BusinessException(ErrorCode.INVALID_INPUT_VALUE,
                "Manual assignments are required for manual assignment method");
        }

        for (Map.Entry<Long, List<Long>> entry : manualAssignments.entrySet()) {
            Long roomId = entry.getKey();
            List<Long> userIds = entry.getValue();

            BreakoutRoom room = breakoutRoomRepository.findById(roomId)
                .orElseThrow(() -> new BusinessException(ErrorCode.ENTITY_NOT_FOUND));

            for (Long userId : userIds) {
                User user = userRepository.findById(userId)
                    .orElseThrow(() -> new BusinessException(ErrorCode.USER_NOT_FOUND));

                BreakoutParticipant participant = BreakoutParticipant.builder()
                    .breakoutRoom(room)
                    .user(user)
                    .build();

                breakoutParticipantRepository.save(participant);
            }
        }
    }

    @Transactional
    public BreakoutResponse startBreakoutRoom(Long userId, Long breakoutRoomId) {
        BreakoutRoom breakoutRoom = breakoutRoomRepository.findByIdAndNotDeleted(breakoutRoomId)
            .orElseThrow(() -> new BusinessException(ErrorCode.ENTITY_NOT_FOUND));

        breakoutRoom.start();
        breakoutRoom = breakoutRoomRepository.save(breakoutRoom);

        log.info("Started breakout room {}", breakoutRoom.getId());
        return mapToResponse(breakoutRoom);
    }

    @Transactional
    public BreakoutResponse closeBreakoutRoom(Long userId, Long breakoutRoomId) {
        BreakoutRoom breakoutRoom = breakoutRoomRepository.findByIdAndNotDeleted(breakoutRoomId)
            .orElseThrow(() -> new BusinessException(ErrorCode.ENTITY_NOT_FOUND));

        breakoutRoom.close();
        breakoutRoom = breakoutRoomRepository.save(breakoutRoom);

        log.info("Closed breakout room {}", breakoutRoom.getId());
        return mapToResponse(breakoutRoom);
    }

    public BreakoutStatusResponse getBreakoutStatus(Long breakoutRoomId) {
        BreakoutRoom breakoutRoom = breakoutRoomRepository.findByIdAndNotDeleted(breakoutRoomId)
            .orElseThrow(() -> new BusinessException(ErrorCode.ENTITY_NOT_FOUND));

        Long participantCount = breakoutParticipantRepository.countByBreakoutRoomId(breakoutRoomId);

        Integer remainingMinutes = null;
        if (breakoutRoom.getEndsAt() != null) {
            long seconds = java.time.Duration.between(OffsetDateTime.now(), breakoutRoom.getEndsAt()).getSeconds();
            remainingMinutes = (int) (seconds / 60);
        }

        return BreakoutStatusResponse.builder()
            .id(breakoutRoom.getId())
            .name(breakoutRoom.getName())
            .status(breakoutRoom.getStatus())
            .participantCount(participantCount)
            .maxParticipants(breakoutRoom.getMaxParticipants())
            .startedAt(breakoutRoom.getStartedAt())
            .endsAt(breakoutRoom.getEndsAt())
            .remainingMinutes(remainingMinutes)
            .build();
    }

    @Transactional
    public void professorJoinRoom(Long professorId, Long breakoutRoomId) {
        BreakoutRoom breakoutRoom = breakoutRoomRepository.findByIdAndNotDeleted(breakoutRoomId)
            .orElseThrow(() -> new BusinessException(ErrorCode.ENTITY_NOT_FOUND));

        User professor = userRepository.findById(professorId)
            .orElseThrow(() -> new BusinessException(ErrorCode.USER_NOT_FOUND));

        // Check if professor is already in the room
        boolean exists = breakoutParticipantRepository.existsByBreakoutRoomIdAndUserId(breakoutRoomId, professorId);
        if (!exists) {
            BreakoutParticipant participant = BreakoutParticipant.builder()
                .breakoutRoom(breakoutRoom)
                .user(professor)
                .build();

            breakoutParticipantRepository.save(participant);
        }

        log.info("Professor {} joined breakout room {}", professorId, breakoutRoomId);
    }

    // Broadcast message to all rooms (this would typically trigger WebSocket events)
    public void broadcastToAllRooms(Long seminarRoomId, BroadcastMessageRequest request) {
        List<BreakoutRoom> breakoutRooms = breakoutRoomRepository.findBySeminarRoomIdAndNotDeleted(seminarRoomId);

        log.info("Broadcasting message to {} breakout rooms: {}", breakoutRooms.size(), request.getMessage());
        // In a real implementation, this would send WebSocket messages to all rooms
        // For now, just log the broadcast
    }

    private BreakoutResponse mapToResponse(BreakoutRoom breakoutRoom) {
        Long participantCount = breakoutParticipantRepository.countByBreakoutRoomId(breakoutRoom.getId());

        return BreakoutResponse.builder()
            .id(breakoutRoom.getId())
            .seminarRoomId(breakoutRoom.getSeminarRoom().getId())
            .name(breakoutRoom.getName())
            .status(breakoutRoom.getStatus())
            .assignmentMethod(breakoutRoom.getAssignmentMethod())
            .maxParticipants(breakoutRoom.getMaxParticipants())
            .participantCount(participantCount)
            .durationMinutes(breakoutRoom.getDurationMinutes())
            .startedAt(breakoutRoom.getStartedAt())
            .endsAt(breakoutRoom.getEndsAt())
            .meetingUrl(breakoutRoom.getMeetingUrl())
            .settings(breakoutRoom.getSettings())
            .createdAt(breakoutRoom.getCreatedAt())
            .updatedAt(breakoutRoom.getUpdatedAt())
            .build();
    }
}
