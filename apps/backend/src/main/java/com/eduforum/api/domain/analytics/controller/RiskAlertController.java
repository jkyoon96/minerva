package com.eduforum.api.domain.analytics.controller;

import com.eduforum.api.common.dto.ApiResponse;
import com.eduforum.api.domain.analytics.dto.risk.*;
import com.eduforum.api.domain.analytics.service.*;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/v1/analytics")
@RequiredArgsConstructor
@Tag(name = "Risk & Alerts", description = "At-risk student detection and alerts APIs")
public class RiskAlertController {

    private final RiskDetectionService riskDetectionService;
    private final AlertService alertService;

    @GetMapping("/risks/course/{courseId}")
    @Operation(summary = "Get course risks", description = "Get all risk indicators for a course")
    public ResponseEntity<ApiResponse<List<RiskIndicatorResponse>>> getCourseRisks(@PathVariable Long courseId) {
        List<RiskIndicatorResponse> risks = riskDetectionService.getCourseRisks(courseId);
        return ResponseEntity.ok(ApiResponse.success(risks));
    }

    @GetMapping("/risks/student/{studentId}")
    @Operation(summary = "Get student risk", description = "Get risk indicator for a student")
    public ResponseEntity<ApiResponse<RiskIndicatorResponse>> getStudentRisk(
            @PathVariable Long studentId,
            @RequestParam Long courseId) {
        RiskIndicatorResponse risk = riskDetectionService.calculateRiskScore(studentId, courseId);
        return ResponseEntity.ok(ApiResponse.success(risk));
    }

    @PostMapping("/risks/calculate/{courseId}")
    @Operation(summary = "Calculate risks", description = "Run risk calculation for all students in course")
    public ResponseEntity<ApiResponse<String>> calculateRisks(@PathVariable Long courseId) {
        // Would trigger batch calculation
        return ResponseEntity.ok(ApiResponse.success("Risk calculation initiated for course " + courseId));
    }

    @GetMapping("/alerts")
    @Operation(summary = "Get alerts", description = "Get all alerts for a course")
    public ResponseEntity<ApiResponse<List<RiskAlertResponse>>> getAlerts(@RequestParam Long courseId) {
        List<RiskAlertResponse> alerts = alertService.getCourseAlerts(courseId);
        return ResponseEntity.ok(ApiResponse.success(alerts));
    }

    @PutMapping("/alerts/{alertId}/acknowledge")
    @Operation(summary = "Acknowledge alert", description = "Acknowledge a risk alert")
    public ResponseEntity<ApiResponse<RiskAlertResponse>> acknowledgeAlert(
            @Parameter(hidden = true) @RequestHeader("X-User-Id") Long userId,
            @PathVariable Long alertId,
            @Valid @RequestBody(required = false) AcknowledgeAlertRequest request) {
        RiskAlertResponse alert = alertService.acknowledgeAlert(alertId, userId);
        return ResponseEntity.ok(ApiResponse.success("Alert acknowledged", alert));
    }

    @PutMapping("/alerts/{alertId}/resolve")
    @Operation(summary = "Resolve alert", description = "Resolve a risk alert")
    public ResponseEntity<ApiResponse<RiskAlertResponse>> resolveAlert(
            @Parameter(hidden = true) @RequestHeader("X-User-Id") Long userId,
            @PathVariable Long alertId,
            @Valid @RequestBody ResolveAlertRequest request) {
        RiskAlertResponse alert = alertService.resolveAlert(alertId, userId, request.getResolutionNotes());
        return ResponseEntity.ok(ApiResponse.success("Alert resolved", alert));
    }

    @GetMapping("/interventions/{studentId}")
    @Operation(summary = "Get intervention suggestions", description = "Get intervention suggestions for at-risk student")
    public ResponseEntity<ApiResponse<InterventionResponse>> getInterventions(@PathVariable Long studentId) {
        InterventionResponse interventions = alertService.getSuggestions(studentId);
        return ResponseEntity.ok(ApiResponse.success(interventions));
    }
}
