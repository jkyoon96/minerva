package com.eduforum.api.domain.assessment.controller;

import com.eduforum.api.common.dto.ApiResponse;
import com.eduforum.api.domain.assessment.dto.participation.*;
import com.eduforum.api.domain.assessment.service.ParticipationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/v1/participation")
@RequiredArgsConstructor
@Tag(name = "Participation", description = "Participation scoring and tracking APIs")
public class ParticipationController {

    private final ParticipationService participationService;

    @PostMapping("/events")
    @Operation(summary = "Record event", description = "Record a participation event")
    public ResponseEntity<ApiResponse<Void>> recordEvent(
            @Valid @RequestBody RecordEventRequest request) {
        participationService.recordEvent(
            request.getStudentId(),
            request.getCourseId(),
            request.getSessionId(),
            request.getEventType(),
            request.getPoints(),
            request.getEventData()
        );
        return ResponseEntity.status(HttpStatus.CREATED)
            .body(ApiResponse.success("Event recorded successfully", null));
    }

    @GetMapping("/scores/{courseId}")
    @Operation(summary = "Get course scores", description = "Get participation scores for all students in course")
    public ResponseEntity<ApiResponse<List<ParticipationScoreResponse>>> getCourseScores(
            @PathVariable Long courseId) {
        List<ParticipationScoreResponse> responses = participationService.getCourseScores(courseId);
        return ResponseEntity.ok(ApiResponse.success(responses));
    }

    @GetMapping("/scores/{courseId}/{studentId}")
    @Operation(summary = "Get student score", description = "Get participation score for a student")
    public ResponseEntity<ApiResponse<ParticipationScoreResponse>> getStudentScore(
            @PathVariable Long courseId,
            @PathVariable Long studentId) {
        ParticipationScoreResponse response = participationService.getStudentScore(courseId, studentId);
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @PutMapping("/weights/{courseId}")
    @Operation(summary = "Set weights", description = "Set participation event weights for course")
    public ResponseEntity<ApiResponse<List<ParticipationWeightResponse>>> setWeights(
            @PathVariable Long courseId,
            @Valid @RequestBody List<UpdateWeightsRequest> requests) {
        List<ParticipationWeightResponse> responses = participationService.setWeights(courseId, requests);
        return ResponseEntity.ok(ApiResponse.success("Weights updated successfully", responses));
    }

    @GetMapping("/weights/{courseId}")
    @Operation(summary = "Get weights", description = "Get participation event weights for course")
    public ResponseEntity<ApiResponse<List<ParticipationWeightResponse>>> getWeights(
            @PathVariable Long courseId) {
        List<ParticipationWeightResponse> responses = participationService.getWeights(courseId);
        return ResponseEntity.ok(ApiResponse.success(responses));
    }

    @GetMapping("/dashboard/{studentId}")
    @Operation(summary = "Get student dashboard", description = "Get participation dashboard for student")
    public ResponseEntity<ApiResponse<ParticipationDashboardResponse>> getStudentDashboard(
            @PathVariable Long studentId,
            @RequestParam Long courseId) {
        ParticipationDashboardResponse response = participationService.getStudentDashboard(studentId, courseId);
        return ResponseEntity.ok(ApiResponse.success(response));
    }
}
