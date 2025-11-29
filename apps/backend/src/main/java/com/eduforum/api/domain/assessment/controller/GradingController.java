package com.eduforum.api.domain.assessment.controller;

import com.eduforum.api.common.dto.ApiResponse;
import com.eduforum.api.domain.assessment.dto.grading.*;
import com.eduforum.api.domain.assessment.service.AiGradingService;
import com.eduforum.api.domain.assessment.service.AutoGradingService;
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
@RequestMapping("/v1/grading")
@RequiredArgsConstructor
@Tag(name = "Grading", description = "Auto & AI Grading APIs")
public class GradingController {

    private final AutoGradingService autoGradingService;
    private final AiGradingService aiGradingService;

    @PostMapping("/auto/{quizSessionId}")
    @Operation(summary = "Auto grade quiz", description = "Automatically grade multiple choice quiz")
    public ResponseEntity<ApiResponse<List<GradingResultResponse>>> autoGradeQuiz(
            @PathVariable Long quizSessionId,
            @Valid @RequestBody AutoGradeRequest request) {
        List<GradingResultResponse> responses = autoGradingService.gradeQuizSession(
            quizSessionId, request.getIncludeStatistics()
        );
        return ResponseEntity.ok(ApiResponse.success("Quiz graded successfully", responses));
    }

    @GetMapping("/{resultId}")
    @Operation(summary = "Get grading result", description = "Get grading result by ID")
    public ResponseEntity<ApiResponse<GradingResultResponse>> getGradingResult(
            @PathVariable Long resultId) {
        GradingResultResponse response = autoGradingService.getGradingResult(resultId);
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @GetMapping("/assignment/{assignmentId}")
    @Operation(summary = "Get assignment grades", description = "Get all grades for an assignment")
    public ResponseEntity<ApiResponse<List<GradingResultResponse>>> getAssignmentGrades(
            @PathVariable Long assignmentId) {
        List<GradingResultResponse> responses = autoGradingService.getAssignmentGrades(assignmentId);
        return ResponseEntity.ok(ApiResponse.success(responses));
    }

    @GetMapping("/statistics/{quizId}")
    @Operation(summary = "Get quiz statistics", description = "Get answer statistics for a quiz")
    public ResponseEntity<ApiResponse<List<AnswerStatisticsResponse>>> getQuizStatistics(
            @PathVariable Long quizId) {
        List<AnswerStatisticsResponse> responses = autoGradingService.getQuizStatistics(quizId);
        return ResponseEntity.ok(ApiResponse.success(responses));
    }

    @PostMapping("/ai/{submissionId}")
    @Operation(summary = "AI grade submission", description = "Grade submission using AI")
    public ResponseEntity<ApiResponse<GradingResultResponse>> aiGradeSubmission(
            @PathVariable Long submissionId,
            @Valid @RequestBody AiGradeRequest request) {
        GradingResultResponse response = aiGradingService.gradeSubmission(
            submissionId, request.getGenerateFeedback(), request.getAiModel()
        );
        return ResponseEntity.ok(ApiResponse.success("Submission graded successfully", response));
    }

    @PutMapping("/{resultId}")
    @Operation(summary = "Update grade", description = "Update grade and feedback (professor review)")
    public ResponseEntity<ApiResponse<GradingResultResponse>> updateGrade(
            @Parameter(hidden = true) @RequestHeader("X-User-Id") Long userId,
            @PathVariable Long resultId,
            @Valid @RequestBody UpdateGradeRequest request) {
        GradingResultResponse response = aiGradingService.updateGrade(
            resultId, userId, request.getScore(), request.getFeedback()
        );
        return ResponseEntity.ok(ApiResponse.success("Grade updated successfully", response));
    }

    @GetMapping("/ai/pending")
    @Operation(summary = "Get pending AI grades", description = "Get all pending AI grade submissions")
    public ResponseEntity<ApiResponse<List<GradingResultResponse>>> getPendingAiGrades(
            @RequestParam Long assignmentId) {
        List<GradingResultResponse> responses = aiGradingService.getPendingAiGrades(assignmentId);
        return ResponseEntity.ok(ApiResponse.success(responses));
    }

    @PostMapping("/ai/batch")
    @Operation(summary = "Batch AI grade", description = "Grade multiple submissions with AI")
    public ResponseEntity<ApiResponse<List<GradingResultResponse>>> batchAiGrade(
            @Valid @RequestBody BatchGradeRequest request) {
        List<GradingResultResponse> responses = aiGradingService.batchGrade(
            request.getSubmissionIds(), request.getGenerateFeedback()
        );
        return ResponseEntity.ok(ApiResponse.success("Batch grading completed", responses));
    }
}
