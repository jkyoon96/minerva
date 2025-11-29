package com.eduforum.api.domain.active.service;

import com.eduforum.api.common.exception.BusinessException;
import com.eduforum.api.common.exception.ErrorCode;
import com.eduforum.api.domain.active.dto.discussion.*;
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
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
@Slf4j
public class DiscussionService {

    private final SpeakingQueueRepository speakingQueueRepository;
    private final DiscussionThreadRepository discussionThreadRepository;
    private final SeminarRoomRepository seminarRoomRepository;
    private final UserRepository userRepository;

    @Transactional
    public SpeakingQueueResponse joinSpeakingQueue(Long userId, Long seminarRoomId, JoinQueueRequest request) {
        log.info("User {} joining speaking queue for seminar {}", userId, seminarRoomId);

        SeminarRoom seminarRoom = seminarRoomRepository.findById(seminarRoomId)
            .orElseThrow(() -> new BusinessException(ErrorCode.ENTITY_NOT_FOUND));

        User user = userRepository.findById(userId)
            .orElseThrow(() -> new BusinessException(ErrorCode.USER_NOT_FOUND));

        // Check if user is already in queue
        boolean exists = speakingQueueRepository.existsByRoomIdAndUserIdAndStatus(
            seminarRoomId, userId, SpeakingStatus.WAITING);

        if (exists) {
            throw new BusinessException(ErrorCode.INVALID_INPUT_VALUE, "Already in speaking queue");
        }

        // Get next queue position
        Integer maxPosition = speakingQueueRepository.findMaxQueuePositionByRoomId(seminarRoomId);
        int nextPosition = (maxPosition != null ? maxPosition : 0) + 1;

        SpeakingQueue queueEntry = SpeakingQueue.builder()
            .room(seminarRoom)
            .user(user)
            .status(SpeakingStatus.WAITING)
            .queuePosition(nextPosition)
            .build();

        queueEntry = speakingQueueRepository.save(queueEntry);
        log.info("User {} joined queue at position {}", userId, nextPosition);
        return mapToQueueResponse(queueEntry);
    }

    @Transactional
    public void leaveSpeakingQueue(Long userId, Long queueId) {
        SpeakingQueue queueEntry = speakingQueueRepository.findById(queueId)
            .orElseThrow(() -> new BusinessException(ErrorCode.ENTITY_NOT_FOUND));

        if (!queueEntry.getUser().getId().equals(userId)) {
            throw new BusinessException(ErrorCode.ACCESS_DENIED);
        }

        if (queueEntry.isSpeaking()) {
            queueEntry.finish();
            speakingQueueRepository.save(queueEntry);
        } else {
            speakingQueueRepository.delete(queueEntry);
        }

        log.info("User {} left speaking queue", userId);
    }

    public List<SpeakingQueueResponse> getSpeakingQueue(Long seminarRoomId) {
        return speakingQueueRepository.findByRoomIdOrderByQueuePositionAsc(seminarRoomId).stream()
            .map(this::mapToQueueResponse)
            .collect(Collectors.toList());
    }

    @Transactional
    public SpeakingQueueResponse grantSpeakingTurn(Long professorId, Long queueId) {
        SpeakingQueue queueEntry = speakingQueueRepository.findById(queueId)
            .orElseThrow(() -> new BusinessException(ErrorCode.ENTITY_NOT_FOUND));

        if (!queueEntry.isWaiting()) {
            throw new BusinessException(ErrorCode.INVALID_INPUT_VALUE, "Queue entry is not in waiting state");
        }

        // Finish any currently speaking user
        List<SpeakingQueue> currentlySpeaking = speakingQueueRepository
            .findByRoomIdAndStatus(queueEntry.getRoom().getId(), SpeakingStatus.SPEAKING);

        for (SpeakingQueue speaking : currentlySpeaking) {
            speaking.finish();
            speakingQueueRepository.save(speaking);
        }

        // Grant turn to requested user
        queueEntry.grant();
        queueEntry = speakingQueueRepository.save(queueEntry);

        log.info("Granted speaking turn to user {}", queueEntry.getUser().getId());
        return mapToQueueResponse(queueEntry);
    }

    public ParticipationStatsResponse getParticipationStats(Long seminarRoomId) {
        List<SpeakingQueue> allEntries = speakingQueueRepository.findByRoomId(seminarRoomId);

        // Calculate stats per user
        Map<Long, ParticipationStatsResponse.UserStats> userStatsMap = new HashMap<>();

        for (SpeakingQueue entry : allEntries) {
            Long userId = entry.getUser().getId();
            String userName = entry.getUser().getName();

            userStatsMap.putIfAbsent(userId, ParticipationStatsResponse.UserStats.builder()
                .userId(userId)
                .userName(userName)
                .speakingCount(0)
                .totalSpeakingSeconds(0)
                .averageSpeakingSeconds(0.0)
                .build());

            ParticipationStatsResponse.UserStats stats = userStatsMap.get(userId);

            if (entry.getSpeakingDurationSeconds() != null) {
                stats.setSpeakingCount(stats.getSpeakingCount() + 1);
                stats.setTotalSpeakingSeconds(stats.getTotalSpeakingSeconds() + entry.getSpeakingDurationSeconds());
            }
        }

        // Calculate averages
        for (ParticipationStatsResponse.UserStats stats : userStatsMap.values()) {
            if (stats.getSpeakingCount() > 0) {
                stats.setAverageSpeakingSeconds(
                    (double) stats.getTotalSpeakingSeconds() / stats.getSpeakingCount()
                );
            }
        }

        List<ParticipationStatsResponse.UserStats> userStatsList = userStatsMap.values().stream()
            .sorted((a, b) -> Integer.compare(b.getTotalSpeakingSeconds(), a.getTotalSpeakingSeconds()))
            .collect(Collectors.toList());

        int totalSpeakingSeconds = userStatsList.stream()
            .mapToInt(ParticipationStatsResponse.UserStats::getTotalSpeakingSeconds)
            .sum();

        return ParticipationStatsResponse.builder()
            .seminarRoomId(seminarRoomId)
            .totalParticipants(userStatsMap.size())
            .totalSpeakingSeconds(totalSpeakingSeconds)
            .userStats(userStatsList)
            .build();
    }

    @Transactional
    public ThreadResponse createThread(Long userId, Long seminarRoomId, CreateThreadRequest request) {
        log.info("Creating discussion thread for seminar {} by user {}", seminarRoomId, userId);

        SeminarRoom seminarRoom = seminarRoomRepository.findById(seminarRoomId)
            .orElseThrow(() -> new BusinessException(ErrorCode.ENTITY_NOT_FOUND));

        User creator = userRepository.findById(userId)
            .orElseThrow(() -> new BusinessException(ErrorCode.USER_NOT_FOUND));

        DiscussionThread thread = DiscussionThread.builder()
            .room(seminarRoom)
            .creator(creator)
            .title(request.getTitle())
            .content(request.getContent())
            .isPinned(request.getIsPinned() != null ? request.getIsPinned() : false)
            .build();

        thread = discussionThreadRepository.save(thread);
        log.info("Created discussion thread {}", thread.getId());
        return mapToThreadResponse(thread);
    }

    public List<ThreadResponse> getThreads(Long seminarRoomId) {
        return discussionThreadRepository.findByRoomIdAndNotDeletedOrderByCreatedAtDesc(seminarRoomId).stream()
            .map(this::mapToThreadResponse)
            .collect(Collectors.toList());
    }

    public ThreadResponse getThread(Long threadId) {
        DiscussionThread thread = discussionThreadRepository.findByIdAndNotDeleted(threadId)
            .orElseThrow(() -> new BusinessException(ErrorCode.ENTITY_NOT_FOUND));
        return mapToThreadResponse(thread);
    }

    @Transactional
    public ThreadResponse replyToThread(Long userId, Long threadId, CreateThreadRequest request) {
        DiscussionThread parentThread = discussionThreadRepository.findByIdAndNotDeleted(threadId)
            .orElseThrow(() -> new BusinessException(ErrorCode.ENTITY_NOT_FOUND));

        User creator = userRepository.findById(userId)
            .orElseThrow(() -> new BusinessException(ErrorCode.USER_NOT_FOUND));

        DiscussionThread reply = DiscussionThread.builder()
            .room(parentThread.getRoom())
            .creator(creator)
            .parentThread(parentThread)
            .title(request.getTitle())
            .content(request.getContent())
            .isPinned(false)
            .build();

        reply = discussionThreadRepository.save(reply);
        log.info("Created reply to thread {}", threadId);
        return mapToThreadResponse(reply);
    }

    @Transactional
    public void deleteThread(Long userId, Long threadId) {
        DiscussionThread thread = discussionThreadRepository.findByIdAndNotDeleted(threadId)
            .orElseThrow(() -> new BusinessException(ErrorCode.ENTITY_NOT_FOUND));

        if (!thread.getCreator().getId().equals(userId)) {
            throw new BusinessException(ErrorCode.ACCESS_DENIED);
        }

        thread.delete();
        discussionThreadRepository.save(thread);
        log.info("Deleted thread {}", threadId);
    }

    private SpeakingQueueResponse mapToQueueResponse(SpeakingQueue queueEntry) {
        return SpeakingQueueResponse.builder()
            .id(queueEntry.getId())
            .roomId(queueEntry.getRoom().getId())
            .userId(queueEntry.getUser().getId())
            .userName(queueEntry.getUser().getName())
            .status(queueEntry.getStatus())
            .queuePosition(queueEntry.getQueuePosition())
            .grantedAt(queueEntry.getGrantedAt())
            .finishedAt(queueEntry.getFinishedAt())
            .speakingDurationSeconds(queueEntry.getSpeakingDurationSeconds())
            .createdAt(queueEntry.getCreatedAt())
            .build();
    }

    private ThreadResponse mapToThreadResponse(DiscussionThread thread) {
        Long replyCount = discussionThreadRepository.countByParentThreadId(thread.getId());

        return ThreadResponse.builder()
            .id(thread.getId())
            .roomId(thread.getRoom().getId())
            .creatorId(thread.getCreator().getId())
            .creatorName(thread.getCreator().getName())
            .parentThreadId(thread.getParentThread() != null ? thread.getParentThread().getId() : null)
            .title(thread.getTitle())
            .content(thread.getContent())
            .isPinned(thread.getIsPinned())
            .replyCount(replyCount)
            .createdAt(thread.getCreatedAt())
            .updatedAt(thread.getUpdatedAt())
            .build();
    }
}
