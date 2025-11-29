package com.eduforum.api.domain.assessment.controller;

import com.eduforum.api.common.dto.ApiResponse;
import com.eduforum.api.domain.assessment.dto.peer.*;
import com.eduforum.api.domain.assessment.service.PeerReviewService;
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
@RequestMapping("/v1/peer-review")
@RequiredArgsConstructor
@Tag(name = "Peer Review", description = "Peer review and evaluation APIs")
public class PeerReviewController {

    private final PeerReviewService peerReviewService;

    @PostMapping("/assignments/{assignmentId}/setup")
    @Operation(summary = "Setup peer review", description = "Setup peer review for an assignment")
    public ResponseEntity<ApiResponse<Void>> setupPeerReview(
            @PathVariable Long assignmentId,
            @Valid @RequestBody SetupPeerReviewRequest request) {
        peerReviewService.setupPeerReview(assignmentId, request);
        return ResponseEntity.status(HttpStatus.CREATED)
            .body(ApiResponse.success("Peer review setup successfully", null));
    }

    @GetMapping("/assignments/{assignmentId}")
    @Operation(summary = "Get review assignments", description = "Get peer review assignments for a reviewer")
    public ResponseEntity<ApiResponse<List<PeerReviewAssignmentResponse>>> getReviewAssignments(
            @Parameter(hidden = true) @RequestHeader("X-User-Id") Long userId,
            @PathVariable Long assignmentId) {
        List<PeerReviewAssignmentResponse> responses = peerReviewService.getReviewAssignments(assignmentId, userId);
        return ResponseEntity.ok(ApiResponse.success(responses));
    }

    @PostMapping("/submit")
    @Operation(summary = "Submit peer review", description = "Submit a peer review")
    public ResponseEntity<ApiResponse<PeerReviewResponse>> submitReview(
            @Parameter(hidden = true) @RequestHeader("X-User-Id") Long userId,
            @Valid @RequestBody SubmitPeerReviewRequest request) {
        PeerReviewResponse response = peerReviewService.submitReview(
            userId,
            request.getSubmissionId(),
            request.getScore(),
            request.getComments(),
            request.getRubricScores()
        );
        return ResponseEntity.ok(ApiResponse.success("Review submitted successfully", response));
    }

    @GetMapping("/received/{submissionId}")
    @Operation(summary = "Get received reviews", description = "Get reviews received for a submission")
    public ResponseEntity<ApiResponse<List<PeerReviewResponse>>> getReceivedReviews(
            @PathVariable Long submissionId) {
        List<PeerReviewResponse> responses = peerReviewService.getReceivedReviews(submissionId);
        return ResponseEntity.ok(ApiResponse.success(responses));
    }

    @GetMapping("/given/{studentId}")
    @Operation(summary = "Get given reviews", description = "Get reviews given by a student")
    public ResponseEntity<ApiResponse<List<PeerReviewResponse>>> getGivenReviews(
            @PathVariable Long studentId) {
        List<PeerReviewResponse> responses = peerReviewService.getGivenReviews(studentId);
        return ResponseEntity.ok(ApiResponse.success(responses));
    }

    @GetMapping("/results/{submissionId}")
    @Operation(summary = "Get aggregated results", description = "Get aggregated peer review results")
    public ResponseEntity<ApiResponse<PeerReviewResultResponse>> getAggregatedResults(
            @PathVariable Long submissionId) {
        PeerReviewResultResponse response = peerReviewService.getAggregatedResults(submissionId);
        return ResponseEntity.ok(ApiResponse.success(response));
    }
}
