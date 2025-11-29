package com.eduforum.api.domain.assessment.controller;

import com.eduforum.api.common.dto.ApiResponse;
import com.eduforum.api.domain.assessment.dto.feedback.*;
import com.eduforum.api.domain.assessment.service.FeedbackService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/v1/feedback")
@RequiredArgsConstructor
@Tag(name = "Feedback", description = "Feedback and learning resource recommendation APIs")
public class FeedbackController {

    private final FeedbackService feedbackService;

    @PostMapping("/generate/{submissionId}")
    @Operation(summary = "Generate AI feedback", description = "Generate AI feedback for submission")
    public ResponseEntity<ApiResponse<FeedbackResponse>> generateFeedback(
            @PathVariable Long submissionId,
            @Valid @RequestBody GenerateFeedbackRequest request) {
        FeedbackResponse response = feedbackService.generateFeedbackForSubmission(submissionId);
        return ResponseEntity.status(HttpStatus.CREATED)
            .body(ApiResponse.success("Feedback generated successfully", response));
    }

    @GetMapping("/{feedbackId}")
    @Operation(summary = "Get feedback", description = "Get feedback by ID")
    public ResponseEntity<ApiResponse<FeedbackResponse>> getFeedback(
            @PathVariable Long feedbackId) {
        FeedbackResponse response = feedbackService.getFeedback(feedbackId);
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @GetMapping("/student/{studentId}")
    @Operation(summary = "Get student feedbacks", description = "Get all feedbacks for a student")
    public ResponseEntity<ApiResponse<List<FeedbackResponse>>> getStudentFeedbacks(
            @PathVariable Long studentId,
            @RequestParam(required = false) Long courseId) {
        List<FeedbackResponse> responses = feedbackService.getStudentFeedbacks(studentId, courseId);
        return ResponseEntity.ok(ApiResponse.success(responses));
    }

    @GetMapping("/recommendations/{studentId}")
    @Operation(summary = "Get learning resources", description = "Get recommended learning resources for student")
    public ResponseEntity<ApiResponse<List<LearningResourceResponse>>> getRecommendations(
            @PathVariable Long studentId,
            @RequestParam(required = false) Long courseId) {
        List<LearningResourceResponse> responses = feedbackService.getRecommendations(studentId, courseId);
        return ResponseEntity.ok(ApiResponse.success(responses));
    }

    @PostMapping("/save")
    @Operation(summary = "Save custom feedback", description = "Save custom feedback from professor")
    public ResponseEntity<ApiResponse<FeedbackResponse>> saveFeedback(
            @Parameter(hidden = true) @RequestHeader("X-User-Id") Long userId,
            @Valid @RequestBody SaveFeedbackRequest request) {
        FeedbackResponse response = feedbackService.saveFeedback(
            request.getStudentId(),
            request.getCourseId(),
            request.getSubmissionId(),
            request.getFeedbackType(),
            request.getTitle(),
            request.getContent(),
            userId
        );
        return ResponseEntity.status(HttpStatus.CREATED)
            .body(ApiResponse.success("Feedback saved successfully", response));
    }
}
