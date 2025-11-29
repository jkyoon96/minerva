package com.eduforum.api.domain.analytics.service;

import com.eduforum.api.common.exception.BusinessException;
import com.eduforum.api.common.exception.ErrorCode;
import com.eduforum.api.domain.analytics.dto.risk.*;
import com.eduforum.api.domain.analytics.entity.*;
import com.eduforum.api.domain.analytics.repository.RiskAlertRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class AlertService {
    private final RiskAlertRepository alertRepository;

    @Transactional
    public void createRiskAlert(RiskIndicator indicator) {
        log.info("Creating risk alert for student: {}", indicator.getStudentId());
        
        String message = generateAlertMessage(indicator);
        String recommendations = generateRecommendations(indicator);
        
        RiskAlert alert = RiskAlert.builder()
            .riskIndicator(indicator)
            .studentId(indicator.getStudentId())
            .courseId(indicator.getCourseId())
            .alertMessage(message)
            .recommendations(recommendations)
            .alertData(Map.of("riskLevel", indicator.getRiskLevel().toString()))
            .build();
        
        alert.markAsSent();
        alertRepository.save(alert);
    }

    @Transactional(readOnly = true)
    public List<RiskAlertResponse> getCourseAlerts(Long courseId) {
        return alertRepository.findByCourseIdOrderByCreatedAtDesc(courseId).stream()
            .map(this::toResponse)
            .collect(Collectors.toList());
    }

    @Transactional
    public RiskAlertResponse acknowledgeAlert(Long alertId, Long userId) {
        RiskAlert alert = alertRepository.findById(alertId)
            .orElseThrow(() -> new BusinessException(ErrorCode.ENTITY_NOT_FOUND, "Alert not found"));
        
        alert.acknowledge(userId);
        alert = alertRepository.save(alert);
        return toResponse(alert);
    }

    @Transactional
    public RiskAlertResponse resolveAlert(Long alertId, Long userId, String notes) {
        RiskAlert alert = alertRepository.findById(alertId)
            .orElseThrow(() -> new BusinessException(ErrorCode.ENTITY_NOT_FOUND, "Alert not found"));
        
        alert.resolve(userId, notes);
        alert = alertRepository.save(alert);
        return toResponse(alert);
    }

    public InterventionResponse getSuggestions(Long studentId) {
        return InterventionResponse.builder()
            .studentId(studentId)
            .primaryConcern("Low engagement and attendance")
            .suggestions(List.of(
                InterventionResponse.Suggestion.builder()
                    .type("ONE_ON_ONE_MEETING")
                    .description("Schedule individual meeting with student")
                    .priority("HIGH")
                    .estimatedImpact("HIGH")
                    .build(),
                InterventionResponse.Suggestion.builder()
                    .type("PEER_MENTORING")
                    .description("Assign peer mentor from same course")
                    .priority("MEDIUM")
                    .estimatedImpact("MEDIUM")
                    .build()
            ))
            .recommendedResources(List.of("Study Group", "Office Hours", "Tutoring"))
            .urgencyLevel("HIGH")
            .build();
    }

    private String generateAlertMessage(RiskIndicator indicator) {
        return String.format("Student %d is at %s risk (score: %.2f)",
            indicator.getStudentId(), indicator.getRiskLevel(), indicator.getRiskScore());
    }

    private String generateRecommendations(RiskIndicator indicator) {
        return "Immediate intervention recommended. Schedule meeting and provide additional support.";
    }

    private RiskAlertResponse toResponse(RiskAlert alert) {
        return RiskAlertResponse.builder()
            .id(alert.getId())
            .riskIndicatorId(alert.getRiskIndicator().getId())
            .studentId(alert.getStudentId())
            .courseId(alert.getCourseId())
            .instructorId(alert.getInstructorId())
            .status(alert.getStatus())
            .alertMessage(alert.getAlertMessage())
            .recommendations(alert.getRecommendations())
            .sentAt(alert.getSentAt())
            .acknowledgedAt(alert.getAcknowledgedAt())
            .acknowledgedBy(alert.getAcknowledgedBy())
            .resolvedAt(alert.getResolvedAt())
            .resolvedBy(alert.getResolvedBy())
            .resolutionNotes(alert.getResolutionNotes())
            .alertData(alert.getAlertData())
            .createdAt(alert.getCreatedAt())
            .build();
    }
}
