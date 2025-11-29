package com.eduforum.api.domain.analytics.service;

import com.eduforum.api.domain.analytics.entity.*;
import com.eduforum.api.domain.analytics.repository.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

@Slf4j
@Service
@RequiredArgsConstructor
public class DataAggregationService {

    private final LearningMetricRepository metricRepository;
    private final AnalyticsSnapshotRepository snapshotRepository;
    private final Random random = new Random();

    @Async
    @Transactional
    public void aggregateStudentMetrics(Long studentId, Long courseId, OffsetDateTime start, OffsetDateTime end) {
        log.info("Aggregating metrics for student: {}, course: {}", studentId, courseId);

        // Simulate metric calculation
        for (MetricType type : MetricType.values()) {
            LearningMetric metric = metricRepository.findByStudentIdAndCourseIdAndMetricType(studentId, courseId, type)
                .orElse(LearningMetric.builder()
                    .studentId(studentId)
                    .courseId(courseId)
                    .metricType(type)
                    .periodStart(start)
                    .periodEnd(end)
                    .sampleCount(0)
                    .breakdown(new HashMap<>())
                    .build());

            BigDecimal value = simulateMetricValue(type);
            metric.updateValue(value);
            metricRepository.save(metric);
        }
    }

    @Scheduled(cron = "0 0 2 * * *") // Run at 2 AM daily
    @Transactional
    public void dailyAggregation() {
        log.info("Running daily aggregation batch job");
        // Simulated batch processing
        OffsetDateTime yesterday = OffsetDateTime.now().minusDays(1);
        OffsetDateTime today = OffsetDateTime.now();

        // Would normally iterate through all active courses and students
        log.info("Daily aggregation completed");
    }

    private BigDecimal simulateMetricValue(MetricType type) {
        return BigDecimal.valueOf(60 + random.nextInt(41)); // 60-100
    }

    private Map<String, Object> createBreakdown(MetricType type) {
        Map<String, Object> breakdown = new HashMap<>();
        breakdown.put("high", random.nextInt(10));
        breakdown.put("medium", random.nextInt(10));
        breakdown.put("low", random.nextInt(5));
        return breakdown;
    }
}
