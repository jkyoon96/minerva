package com.eduforum.api.domain.analytics.service;

import com.eduforum.api.common.exception.BusinessException;
import com.eduforum.api.common.exception.ErrorCode;
import com.eduforum.api.domain.analytics.dto.report.StudentReportRequest;
import com.eduforum.api.domain.analytics.dto.report.StudentReportResponse;
import com.eduforum.api.domain.analytics.entity.*;
import com.eduforum.api.domain.analytics.repository.LearningMetricRepository;
import com.eduforum.api.domain.analytics.repository.StudentReportRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.OffsetDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class StudentReportService {

    private final StudentReportRepository reportRepository;
    private final LearningMetricRepository metricRepository;
    private final Random random = new Random();

    @Transactional(readOnly = true)
    public StudentReportResponse getStudentReport(Long studentId, Long courseId) {
        StudentReport report = reportRepository.findByStudentIdAndCourseIdOrderByPeriodStartDesc(studentId, courseId)
            .stream()
            .findFirst()
            .orElseThrow(() -> new BusinessException(ErrorCode.ENTITY_NOT_FOUND, "Student report not found"));

        return toResponse(report);
    }

    @Transactional
    public StudentReportResponse generateReport(StudentReportRequest request) {
        log.info("Generating report for student: {}, course: {}", request.getStudentId(), request.getCourseId());

        OffsetDateTime[] period = calculatePeriod(request.getPeriod(), request.getPeriodStart(), request.getPeriodEnd());

        // Aggregate metrics
        List<LearningMetric> metrics = metricRepository.findByStudentAndPeriod(
            request.getStudentId(),
            request.getCourseId(),
            period[0],
            period[1]
        );

        StudentReport report = StudentReport.builder()
            .studentId(request.getStudentId())
            .courseId(request.getCourseId())
            .period(request.getPeriod())
            .periodStart(period[0])
            .periodEnd(period[1])
            .attendanceRate(calculateAttendanceRate(metrics))
            .engagementScore(calculateEngagementScore(metrics))
            .performanceScore(calculatePerformanceScore(metrics))
            .participationCount(calculateParticipationCount(metrics))
            .quizAvgScore(calculateQuizAvg(metrics))
            .assignmentAvgScore(calculateAssignmentAvg(metrics))
            .summary(generateSummary(request.getStudentId(), request.getCourseId()))
            .detailedMetrics(request.getIncludeDetails() ? createDetailedMetrics(metrics) : Map.of())
            .build();

        report.markAsGenerated();
        report = reportRepository.save(report);

        return toResponse(report);
    }

    @Transactional(readOnly = true)
    public List<StudentReportResponse> getCourseReports(Long courseId) {
        return reportRepository.findByCourseIdOrderByGeneratedAtDesc(courseId).stream()
            .map(this::toResponse)
            .collect(Collectors.toList());
    }

    private OffsetDateTime[] calculatePeriod(ReportPeriod period, OffsetDateTime start, OffsetDateTime end) {
        OffsetDateTime now = OffsetDateTime.now();
        return switch (period) {
            case DAILY -> new OffsetDateTime[]{now.minusDays(1), now};
            case WEEKLY -> new OffsetDateTime[]{now.minusWeeks(1), now};
            case MONTHLY -> new OffsetDateTime[]{now.minusMonths(1), now};
            case SEMESTER -> new OffsetDateTime[]{now.minusMonths(4), now};
            case CUSTOM -> new OffsetDateTime[]{start, end};
        };
    }

    private BigDecimal calculateAttendanceRate(List<LearningMetric> metrics) {
        return metrics.stream()
            .filter(m -> m.getMetricType() == MetricType.ATTENDANCE)
            .map(LearningMetric::getMetricValue)
            .reduce(BigDecimal.ZERO, BigDecimal::add)
            .divide(BigDecimal.valueOf(Math.max(1, metrics.size())), 2, RoundingMode.HALF_UP);
    }

    private BigDecimal calculateEngagementScore(List<LearningMetric> metrics) {
        return BigDecimal.valueOf(70 + random.nextInt(31)); // Simulated 70-100
    }

    private BigDecimal calculatePerformanceScore(List<LearningMetric> metrics) {
        return BigDecimal.valueOf(65 + random.nextInt(36)); // Simulated 65-100
    }

    private Integer calculateParticipationCount(List<LearningMetric> metrics) {
        return metrics.stream()
            .filter(m -> m.getMetricType() == MetricType.PARTICIPATION)
            .mapToInt(LearningMetric::getSampleCount)
            .sum();
    }

    private BigDecimal calculateQuizAvg(List<LearningMetric> metrics) {
        return BigDecimal.valueOf(75 + random.nextInt(26)); // Simulated
    }

    private BigDecimal calculateAssignmentAvg(List<LearningMetric> metrics) {
        return BigDecimal.valueOf(70 + random.nextInt(31)); // Simulated
    }

    private String generateSummary(Long studentId, Long courseId) {
        return String.format("Performance summary for student %d in course %d. Shows consistent engagement with room for improvement in assignment completion.", studentId, courseId);
    }

    private Map<String, Object> createDetailedMetrics(List<LearningMetric> metrics) {
        Map<String, Object> detailed = new HashMap<>();
        detailed.put("total_metrics", metrics.size());
        detailed.put("metrics_by_type", groupByType(metrics));
        detailed.put("trend", "IMPROVING");
        return detailed;
    }

    private Map<String, Long> groupByType(List<LearningMetric> metrics) {
        return metrics.stream()
            .collect(Collectors.groupingBy(
                m -> m.getMetricType().toString(),
                Collectors.counting()
            ));
    }

    private StudentReportResponse toResponse(StudentReport report) {
        return StudentReportResponse.builder()
            .id(report.getId())
            .studentId(report.getStudentId())
            .courseId(report.getCourseId())
            .period(report.getPeriod())
            .periodStart(report.getPeriodStart())
            .periodEnd(report.getPeriodEnd())
            .attendanceRate(report.getAttendanceRate())
            .engagementScore(report.getEngagementScore())
            .performanceScore(report.getPerformanceScore())
            .participationCount(report.getParticipationCount())
            .quizAvgScore(report.getQuizAvgScore())
            .assignmentAvgScore(report.getAssignmentAvgScore())
            .overallScore(report.getOverallScore())
            .summary(report.getSummary())
            .detailedMetrics(report.getDetailedMetrics())
            .reportUrl(report.getReportUrl())
            .generatedAt(report.getGeneratedAt())
            .createdAt(report.getCreatedAt())
            .build();
    }
}
