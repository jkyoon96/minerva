package com.eduforum.api.domain.assessment.service;

import com.eduforum.api.common.exception.BusinessException;
import com.eduforum.api.common.exception.ErrorCode;
import com.eduforum.api.domain.assessment.dto.participation.*;
import com.eduforum.api.domain.assessment.entity.*;
import com.eduforum.api.domain.assessment.repository.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class ParticipationService {

    private final ParticipationEventRepository participationEventRepository;
    private final ParticipationScoreRepository participationScoreRepository;
    private final ParticipationWeightRepository participationWeightRepository;

    @Transactional
    public void recordEvent(Long studentId, Long courseId, Long sessionId, EventType eventType, Integer points, Map<String, Object> eventData) {
        log.info("Recording participation event: {} for student {} in course {}", eventType, studentId, courseId);

        ParticipationEvent event = ParticipationEvent.builder()
            .studentId(studentId)
            .courseId(courseId)
            .sessionId(sessionId)
            .eventType(eventType)
            .points(points != null ? points : 1)
            .eventData(eventData != null ? eventData : new HashMap<>())
            .build();

        participationEventRepository.save(event);

        // Update participation score
        updateParticipationScore(studentId, courseId);
    }

    @Transactional(readOnly = true)
    public List<ParticipationScoreResponse> getCourseScores(Long courseId) {
        List<ParticipationScore> scores = participationScoreRepository.findByCourseId(courseId);

        return scores.stream()
            .map(this::toScoreResponse)
            .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public ParticipationScoreResponse getStudentScore(Long courseId, Long studentId) {
        ParticipationScore score = participationScoreRepository.findByStudentIdAndCourseId(studentId, courseId)
            .orElse(createInitialScore(studentId, courseId));

        return toScoreResponse(score);
    }

    @Transactional
    public List<ParticipationWeightResponse> setWeights(Long courseId, List<UpdateWeightsRequest> requests) {
        List<ParticipationWeight> weights = new ArrayList<>();

        for (UpdateWeightsRequest request : requests) {
            ParticipationWeight weight = participationWeightRepository
                .findByCourseIdAndEventType(courseId, request.getEventType())
                .orElse(new ParticipationWeight());

            weight.setCourseId(courseId);
            weight.setEventType(request.getEventType());
            weight.setWeight(request.getWeight());
            weight.setIsEnabled(request.getIsEnabled());
            weight.setDescription(request.getDescription());

            weights.add(participationWeightRepository.save(weight));
        }

        return weights.stream()
            .map(this::toWeightResponse)
            .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<ParticipationWeightResponse> getWeights(Long courseId) {
        List<ParticipationWeight> weights = participationWeightRepository.findByCourseId(courseId);

        return weights.stream()
            .map(this::toWeightResponse)
            .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public ParticipationDashboardResponse getStudentDashboard(Long studentId, Long courseId) {
        ParticipationScore score = participationScoreRepository.findByStudentIdAndCourseId(studentId, courseId)
            .orElse(createInitialScore(studentId, courseId));

        List<ParticipationEvent> events = participationEventRepository.findByStudentIdAndCourseId(studentId, courseId);

        // Calculate metrics
        Map<String, BigDecimal> scoreByEventType = calculateScoreByEventType(events);
        Map<String, Integer> eventCounts = calculateEventCounts(events);

        // Calculate ranking
        List<ParticipationScore> allScores = participationScoreRepository.findByCourseId(courseId);
        int rank = calculateRank(score, allScores);

        return ParticipationDashboardResponse.builder()
            .studentId(studentId)
            .courseId(courseId)
            .totalScore(score.getTotalScore())
            .classRank(rank)
            .classSize(allScores.size())
            .percentile(calculatePercentile(rank, allScores.size()))
            .scoreByEventType(scoreByEventType)
            .eventCounts(eventCounts)
            .trendData(new HashMap<>())
            .build();
    }

    private void updateParticipationScore(Long studentId, Long courseId) {
        ParticipationScore score = participationScoreRepository.findByStudentIdAndCourseId(studentId, courseId)
            .orElse(createInitialScore(studentId, courseId));

        List<ParticipationEvent> events = participationEventRepository.findByStudentIdAndCourseIdAndIsCounted(studentId, courseId, true);
        List<ParticipationWeight> weights = participationWeightRepository.findByCourseIdAndIsEnabled(courseId, true);

        Map<EventType, BigDecimal> weightMap = weights.stream()
            .collect(Collectors.toMap(ParticipationWeight::getEventType, ParticipationWeight::getWeight));

        BigDecimal totalScore = BigDecimal.ZERO;
        Map<String, Object> breakdown = new HashMap<>();

        for (ParticipationEvent event : events) {
            BigDecimal weight = weightMap.getOrDefault(event.getEventType(), BigDecimal.ONE);
            BigDecimal eventScore = BigDecimal.valueOf(event.getPoints()).multiply(weight);
            totalScore = totalScore.add(eventScore);
        }

        score.setTotalScore(totalScore);
        score.setScoreBreakdown(breakdown);
        score.recalculate();

        participationScoreRepository.save(score);
    }

    private ParticipationScore createInitialScore(Long studentId, Long courseId) {
        return ParticipationScore.builder()
            .studentId(studentId)
            .courseId(courseId)
            .totalScore(BigDecimal.ZERO)
            .attendanceScore(BigDecimal.ZERO)
            .activityScore(BigDecimal.ZERO)
            .engagementScore(BigDecimal.ZERO)
            .scoreBreakdown(new HashMap<>())
            .build();
    }

    private Map<String, BigDecimal> calculateScoreByEventType(List<ParticipationEvent> events) {
        return events.stream()
            .collect(Collectors.groupingBy(
                e -> e.getEventType().name(),
                Collectors.reducing(BigDecimal.ZERO, e -> BigDecimal.valueOf(e.getPoints()), BigDecimal::add)
            ));
    }

    private Map<String, Integer> calculateEventCounts(List<ParticipationEvent> events) {
        return events.stream()
            .collect(Collectors.groupingBy(
                e -> e.getEventType().name(),
                Collectors.collectingAndThen(Collectors.counting(), Long::intValue)
            ));
    }

    private int calculateRank(ParticipationScore studentScore, List<ParticipationScore> allScores) {
        return (int) allScores.stream()
            .filter(s -> s.getTotalScore().compareTo(studentScore.getTotalScore()) > 0)
            .count() + 1;
    }

    private double calculatePercentile(int rank, int total) {
        if (total == 0) return 0.0;
        return ((double) (total - rank) / total) * 100;
    }

    private ParticipationScoreResponse toScoreResponse(ParticipationScore score) {
        return ParticipationScoreResponse.builder()
            .id(score.getId())
            .studentId(score.getStudentId())
            .courseId(score.getCourseId())
            .totalScore(score.getTotalScore())
            .attendanceScore(score.getAttendanceScore())
            .activityScore(score.getActivityScore())
            .engagementScore(score.getEngagementScore())
            .scoreBreakdown(score.getScoreBreakdown())
            .lastCalculatedAt(score.getLastCalculatedAt())
            .build();
    }

    private ParticipationWeightResponse toWeightResponse(ParticipationWeight weight) {
        return ParticipationWeightResponse.builder()
            .id(weight.getId())
            .courseId(weight.getCourseId())
            .eventType(weight.getEventType())
            .weight(weight.getWeight())
            .isEnabled(weight.getIsEnabled())
            .description(weight.getDescription())
            .build();
    }
}
