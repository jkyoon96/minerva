package com.eduforum.api.domain.analytics.service;

import com.eduforum.api.domain.analytics.dto.risk.RiskIndicatorResponse;
import com.eduforum.api.domain.analytics.entity.*;
import com.eduforum.api.domain.analytics.repository.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class RiskDetectionService {
    private final RiskIndicatorRepository riskRepository;
    private final LearningMetricRepository metricRepository;
    private final AlertService alertService;
    private final Random random = new Random();

    @Transactional
    public RiskIndicatorResponse calculateRiskScore(Long studentId, Long courseId) {
        log.info("Calculating risk score for student: {}, course: {}", studentId, courseId);
        
        List<LearningMetric> metrics = metricRepository.findByStudentIdAndCourseId(studentId, courseId);
        
        BigDecimal attendanceRisk = calculateAttendanceRisk(metrics);
        BigDecimal engagementRisk = calculateEngagementRisk(metrics);
        BigDecimal performanceRisk = calculatePerformanceRisk(metrics);
        BigDecimal overallRisk = attendanceRisk.add(engagementRisk).add(performanceRisk)
            .divide(BigDecimal.valueOf(3), 2, java.math.RoundingMode.HALF_UP);
        
        RiskIndicator indicator = riskRepository.findByStudentIdAndCourseId(studentId, courseId)
            .orElse(RiskIndicator.builder()
                .studentId(studentId)
                .courseId(courseId)
                .daysInactive(0)
                .riskFactors(new HashMap<>())
                .build());
        
        indicator.setRiskScore(overallRisk);
        indicator.setAttendanceRisk(attendanceRisk);
        indicator.setEngagementRisk(engagementRisk);
        indicator.setPerformanceRisk(performanceRisk);
        indicator.setLastActivityAt(OffsetDateTime.now().minusDays(random.nextInt(10)));
        indicator.updateCalculationTime();
        indicator.calculateRiskLevel();
        
        indicator = riskRepository.save(indicator);
        
        if (indicator.isAtRisk()) {
            alertService.createRiskAlert(indicator);
        }
        
        return toResponse(indicator);
    }

    @Transactional(readOnly = true)
    public List<RiskIndicatorResponse> getCourseRisks(Long courseId) {
        return riskRepository.findByCourseId(courseId).stream()
            .map(this::toResponse)
            .collect(Collectors.toList());
    }

    @Scheduled(cron = "0 0 3 * * *")
    @Transactional
    public void detectAtRiskStudents() {
        log.info("Running scheduled at-risk student detection");
        // Would normally iterate through all courses
    }

    private BigDecimal calculateAttendanceRisk(List<LearningMetric> metrics) {
        return BigDecimal.valueOf(20 + random.nextInt(61)); // Simulated
    }

    private BigDecimal calculateEngagementRisk(List<LearningMetric> metrics) {
        return BigDecimal.valueOf(15 + random.nextInt(66)); // Simulated
    }

    private BigDecimal calculatePerformanceRisk(List<LearningMetric> metrics) {
        return BigDecimal.valueOf(25 + random.nextInt(56)); // Simulated
    }

    private RiskIndicatorResponse toResponse(RiskIndicator indicator) {
        return RiskIndicatorResponse.builder()
            .id(indicator.getId())
            .studentId(indicator.getStudentId())
            .courseId(indicator.getCourseId())
            .riskLevel(indicator.getRiskLevel())
            .riskScore(indicator.getRiskScore())
            .attendanceRisk(indicator.getAttendanceRisk())
            .engagementRisk(indicator.getEngagementRisk())
            .performanceRisk(indicator.getPerformanceRisk())
            .calculatedAt(indicator.getCalculatedAt())
            .lastActivityAt(indicator.getLastActivityAt())
            .daysInactive(indicator.getDaysInactive())
            .riskFactors(indicator.getRiskFactors())
            .atRisk(indicator.isAtRisk())
            .build();
    }
}
