package com.eduforum.api.domain.assessment.controller;

import com.eduforum.api.common.dto.ApiResponse;
import com.eduforum.api.domain.assessment.dto.code.*;
import com.eduforum.api.domain.assessment.service.CodeExecutionService;
import com.eduforum.api.domain.assessment.service.PlagiarismService;
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
@RequestMapping("/v1/code")
@RequiredArgsConstructor
@Tag(name = "Code Evaluation", description = "Code submission and evaluation APIs")
public class CodeEvaluationController {

    private final CodeExecutionService codeExecutionService;
    private final PlagiarismService plagiarismService;

    @PostMapping("/submit")
    @Operation(summary = "Submit code", description = "Submit code for an assignment")
    public ResponseEntity<ApiResponse<CodeSubmissionResponse>> submitCode(
            @Parameter(hidden = true) @RequestHeader("X-User-Id") Long userId,
            @Valid @RequestBody CodeSubmitRequest request) {
        CodeSubmissionResponse response = codeExecutionService.submitCode(
            userId, request.getAssignmentId(), request.getLanguage(),
            request.getCode(), request.getAutoRun()
        );
        return ResponseEntity.status(HttpStatus.CREATED)
            .body(ApiResponse.success("Code submitted successfully", response));
    }

    @GetMapping("/submissions/{submissionId}")
    @Operation(summary = "Get submission", description = "Get code submission details")
    public ResponseEntity<ApiResponse<CodeSubmissionResponse>> getSubmission(
            @PathVariable Long submissionId) {
        CodeSubmissionResponse response = codeExecutionService.getSubmission(submissionId);
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @GetMapping("/submissions/assignment/{assignmentId}")
    @Operation(summary = "Get assignment submissions", description = "Get all submissions for an assignment")
    public ResponseEntity<ApiResponse<List<CodeSubmissionResponse>>> getAssignmentSubmissions(
            @PathVariable Long assignmentId) {
        List<CodeSubmissionResponse> responses = codeExecutionService.getAssignmentSubmissions(assignmentId);
        return ResponseEntity.ok(ApiResponse.success(responses));
    }

    @PostMapping("/run/{submissionId}")
    @Operation(summary = "Run code", description = "Execute code against test cases")
    public ResponseEntity<ApiResponse<CodeSubmissionResponse>> runCode(
            @PathVariable Long submissionId) {
        CodeSubmissionResponse response = codeExecutionService.runCode(submissionId);
        return ResponseEntity.ok(ApiResponse.success("Code executed successfully", response));
    }

    @GetMapping("/results/{submissionId}")
    @Operation(summary = "Get execution results", description = "Get test case execution results")
    public ResponseEntity<ApiResponse<List<ExecutionResultResponse>>> getExecutionResults(
            @PathVariable Long submissionId) {
        List<ExecutionResultResponse> responses = codeExecutionService.getExecutionResults(submissionId);
        return ResponseEntity.ok(ApiResponse.success(responses));
    }

    @PostMapping("/plagiarism/{assignmentId}")
    @Operation(summary = "Run plagiarism check", description = "Check for plagiarism in assignment submissions")
    public ResponseEntity<ApiResponse<List<PlagiarismReportResponse>>> runPlagiarismCheck(
            @PathVariable Long assignmentId,
            @Valid @RequestBody PlagiarismCheckRequest request) {
        List<PlagiarismReportResponse> responses = plagiarismService.checkPlagiarism(
            assignmentId, request.getThreshold(), request.getAlgorithm()
        );
        return ResponseEntity.ok(ApiResponse.success("Plagiarism check completed", responses));
    }

    @GetMapping("/plagiarism/report/{reportId}")
    @Operation(summary = "Get plagiarism report", description = "Get plagiarism report details")
    public ResponseEntity<ApiResponse<PlagiarismReportResponse>> getPlagiarismReport(
            @PathVariable Long reportId) {
        PlagiarismReportResponse response = plagiarismService.getReport(reportId);
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @GetMapping("/plagiarism/assignment/{assignmentId}")
    @Operation(summary = "Get assignment plagiarism reports", description = "Get all plagiarism reports for assignment")
    public ResponseEntity<ApiResponse<List<PlagiarismReportResponse>>> getAssignmentPlagiarismReports(
            @PathVariable Long assignmentId,
            @RequestParam(required = false) Boolean flaggedOnly) {
        List<PlagiarismReportResponse> responses = plagiarismService.getAssignmentReports(assignmentId, flaggedOnly);
        return ResponseEntity.ok(ApiResponse.success(responses));
    }
}
