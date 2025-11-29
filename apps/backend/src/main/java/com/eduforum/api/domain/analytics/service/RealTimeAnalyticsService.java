package com.eduforum.api.domain.analytics.service;

import com.eduforum.api.common.exception.BusinessException;
import com.eduforum.api.common.exception.ErrorCode;
import com.eduforum.api.domain.analytics.dto.realtime.LiveStatsResponse;
import com.eduforum.api.domain.analytics.dto.realtime.SnapshotRequest;
import com.eduforum.api.domain.analytics.dto.realtime.SnapshotResponse;
import com.eduforum.api.domain.analytics.dto.realtime.TrendResponse;
import com.eduforum.api.domain.analytics.entity.AnalyticsSnapshot;
import com.eduforum.api.domain.analytics.repository.AnalyticsSnapshotRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class RealTimeAnalyticsService {

    private final AnalyticsSnapshotRepository snapshotRepository;

    @Transactional(readOnly = true)
    public LiveStatsResponse getLiveSessionStats(Long sessionId) {
        log.info("Getting live stats for session: {}", sessionId);

        List<AnalyticsSnapshot> snapshots = snapshotRepository.findBySessionIdOrderBySnapshotTimeDesc(sessionId);

        if (snapshots.isEmpty()) {
            return LiveStatsResponse.builder()
                .targetId(sessionId)
                .currentParticipants(0)
                .peakParticipants(0)
                .avgEngagementRate(0.0)
                .totalInteractions(0)
                .interactionsPerMinute(0.0)
                .activeStudents(0)
                .participationRate(0.0)
                .lastUpdated(OffsetDateTime.now())
                .activityBreakdown(Map.of())
                .build();
        }

        AnalyticsSnapshot latest = snapshots.get(0);
        int peakParticipants = snapshots.stream()
            .mapToInt(AnalyticsSnapshot::getTotalParticipants)
            .max()
            .orElse(0);

        Map<String, Integer> breakdown = new HashMap<>();
        breakdown.put("polls", latest.getPollResponses());
        breakdown.put("quizzes", latest.getQuizAttempts());
        breakdown.put("chat", latest.getChatMessages());

        return LiveStatsResponse.builder()
            .targetId(sessionId)
            .currentParticipants(latest.getActiveParticipants())
            .peakParticipants(peakParticipants)
            .avgEngagementRate(latest.getAvgEngagementScore() != null ? latest.getAvgEngagementScore() : 0.0)
            .totalInteractions(latest.getTotalInteractions())
            .interactionsPerMinute(calculateInteractionsPerMinute(snapshots))
            .activeStudents(latest.getActiveParticipants())
            .participationRate(calculateParticipationRate(latest))
            .lastUpdated(latest.getSnapshotTime())
            .activityBreakdown(breakdown)
            .build();
    }

    @Transactional(readOnly = true)
    public LiveStatsResponse getLiveCourseStats(Long courseId) {
        log.info("Getting live stats for course: {}", courseId);

        List<AnalyticsSnapshot> recentSnapshots = snapshotRepository.findByCourseIdAndTimeRange(
            courseId,
            OffsetDateTime.now().minusHours(24),
            OffsetDateTime.now()
        );

        if (recentSnapshots.isEmpty()) {
            return createEmptyStats(courseId);
        }

        AnalyticsSnapshot latest = recentSnapshots.get(0);
        return aggregateCourseStats(courseId, recentSnapshots, latest);
    }

    @Transactional
    public SnapshotResponse saveSnapshot(SnapshotRequest request) {
        log.info("Saving analytics snapshot for course: {}, session: {}",
            request.getCourseId(), request.getSessionId());

        AnalyticsSnapshot snapshot = AnalyticsSnapshot.builder()
            .courseId(request.getCourseId())
            .sessionId(request.getSessionId())
            .snapshotTime(OffsetDateTime.now())
            .totalParticipants(request.getTotalParticipants() != null ? request.getTotalParticipants() : 0)
            .activeParticipants(request.getActiveParticipants() != null ? request.getActiveParticipants() : 0)
            .avgEngagementScore(request.getAvgEngagementScore())
            .totalInteractions(0)
            .pollResponses(0)
            .quizAttempts(0)
            .chatMessages(0)
            .metricsData(request.getMetricsData() != null ? request.getMetricsData() : Map.of())
            .build();

        snapshot = snapshotRepository.save(snapshot);
        return toSnapshotResponse(snapshot);
    }

    @Transactional(readOnly = true)
    public TrendResponse getTrends(Long courseId, String metricName, OffsetDateTime start, OffsetDateTime end) {
        log.info("Getting trends for course: {}, metric: {}", courseId, metricName);

        List<AnalyticsSnapshot> snapshots = snapshotRepository.findByCourseIdAndTimeRange(courseId, start, end);

        List<TrendResponse.DataPoint> dataPoints = snapshots.stream()
            .map(s -> TrendResponse.DataPoint.builder()
                .timestamp(s.getSnapshotTime())
                .value(extractMetricValue(s, metricName))
                .metadata(Map.of("participants", s.getTotalParticipants()))
                .build())
            .collect(Collectors.toList());

        String trendDirection = calculateTrendDirection(dataPoints);
        Double changePercentage = calculateChangePercentage(dataPoints);

        return TrendResponse.builder()
            .courseId(courseId)
            .metricName(metricName)
            .startDate(start)
            .endDate(end)
            .dataPoints(dataPoints)
            .trendDirection(trendDirection)
            .changePercentage(changePercentage)
            .statistics(calculateStatistics(dataPoints))
            .build();
    }

    // Helper methods
    private LiveStatsResponse createEmptyStats(Long courseId) {
        return LiveStatsResponse.builder()
            .targetId(courseId)
            .currentParticipants(0)
            .peakParticipants(0)
            .avgEngagementRate(0.0)
            .totalInteractions(0)
            .interactionsPerMinute(0.0)
            .activeStudents(0)
            .participationRate(0.0)
            .lastUpdated(OffsetDateTime.now())
            .activityBreakdown(Map.of())
            .build();
    }

    private LiveStatsResponse aggregateCourseStats(Long courseId, List<AnalyticsSnapshot> snapshots, AnalyticsSnapshot latest) {
        int totalInteractions = snapshots.stream().mapToInt(AnalyticsSnapshot::getTotalInteractions).sum();
        double avgEngagement = snapshots.stream()
            .filter(s -> s.getAvgEngagementScore() != null)
            .mapToDouble(AnalyticsSnapshot::getAvgEngagementScore)
            .average()
            .orElse(0.0);

        Map<String, Integer> breakdown = new HashMap<>();
        breakdown.put("polls", snapshots.stream().mapToInt(AnalyticsSnapshot::getPollResponses).sum());
        breakdown.put("quizzes", snapshots.stream().mapToInt(AnalyticsSnapshot::getQuizAttempts).sum());
        breakdown.put("chat", snapshots.stream().mapToInt(AnalyticsSnapshot::getChatMessages).sum());

        return LiveStatsResponse.builder()
            .targetId(courseId)
            .currentParticipants(latest.getActiveParticipants())
            .peakParticipants(snapshots.stream().mapToInt(AnalyticsSnapshot::getTotalParticipants).max().orElse(0))
            .avgEngagementRate(avgEngagement)
            .totalInteractions(totalInteractions)
            .interactionsPerMinute(calculateInteractionsPerMinute(snapshots))
            .activeStudents(latest.getActiveParticipants())
            .participationRate(calculateParticipationRate(latest))
            .lastUpdated(latest.getSnapshotTime())
            .activityBreakdown(breakdown)
            .build();
    }

    private Double calculateInteractionsPerMinute(List<AnalyticsSnapshot> snapshots) {
        if (snapshots.size() < 2) return 0.0;

        AnalyticsSnapshot first = snapshots.get(snapshots.size() - 1);
        AnalyticsSnapshot last = snapshots.get(0);

        long minutes = java.time.Duration.between(first.getSnapshotTime(), last.getSnapshotTime()).toMinutes();
        if (minutes == 0) return 0.0;

        int totalInteractions = last.getTotalInteractions() - first.getTotalInteractions();
        return (double) totalInteractions / minutes;
    }

    private Double calculateParticipationRate(AnalyticsSnapshot snapshot) {
        if (snapshot.getTotalParticipants() == 0) return 0.0;
        return (double) snapshot.getActiveParticipants() / snapshot.getTotalParticipants() * 100;
    }

    private Double extractMetricValue(AnalyticsSnapshot snapshot, String metricName) {
        return switch (metricName.toLowerCase()) {
            case "engagement" -> snapshot.getAvgEngagementScore();
            case "participation" -> (double) snapshot.getActiveParticipants();
            case "interactions" -> (double) snapshot.getTotalInteractions();
            default -> 0.0;
        };
    }

    private String calculateTrendDirection(List<TrendResponse.DataPoint> dataPoints) {
        if (dataPoints.size() < 2) return "STABLE";

        double firstHalf = dataPoints.subList(0, dataPoints.size() / 2).stream()
            .mapToDouble(TrendResponse.DataPoint::getValue)
            .average()
            .orElse(0.0);

        double secondHalf = dataPoints.subList(dataPoints.size() / 2, dataPoints.size()).stream()
            .mapToDouble(TrendResponse.DataPoint::getValue)
            .average()
            .orElse(0.0);

        if (secondHalf > firstHalf * 1.05) return "INCREASING";
        if (secondHalf < firstHalf * 0.95) return "DECREASING";
        return "STABLE";
    }

    private Double calculateChangePercentage(List<TrendResponse.DataPoint> dataPoints) {
        if (dataPoints.size() < 2) return 0.0;

        double first = dataPoints.get(0).getValue();
        double last = dataPoints.get(dataPoints.size() - 1).getValue();

        if (first == 0) return 0.0;
        return ((last - first) / first) * 100;
    }

    private Map<String, Object> calculateStatistics(List<TrendResponse.DataPoint> dataPoints) {
        Map<String, Object> stats = new HashMap<>();

        if (dataPoints.isEmpty()) {
            stats.put("min", 0.0);
            stats.put("max", 0.0);
            stats.put("avg", 0.0);
            stats.put("count", 0);
            return stats;
        }

        double[] values = dataPoints.stream().mapToDouble(TrendResponse.DataPoint::getValue).toArray();

        stats.put("min", java.util.Arrays.stream(values).min().orElse(0.0));
        stats.put("max", java.util.Arrays.stream(values).max().orElse(0.0));
        stats.put("avg", java.util.Arrays.stream(values).average().orElse(0.0));
        stats.put("count", values.length);

        return stats;
    }

    private SnapshotResponse toSnapshotResponse(AnalyticsSnapshot snapshot) {
        return SnapshotResponse.builder()
            .id(snapshot.getId())
            .courseId(snapshot.getCourseId())
            .sessionId(snapshot.getSessionId())
            .snapshotTime(snapshot.getSnapshotTime())
            .totalParticipants(snapshot.getTotalParticipants())
            .activeParticipants(snapshot.getActiveParticipants())
            .avgEngagementScore(snapshot.getAvgEngagementScore())
            .totalInteractions(snapshot.getTotalInteractions())
            .pollResponses(snapshot.getPollResponses())
            .quizAttempts(snapshot.getQuizAttempts())
            .chatMessages(snapshot.getChatMessages())
            .metricsData(snapshot.getMetricsData())
            .createdAt(snapshot.getCreatedAt())
            .build();
    }
}
