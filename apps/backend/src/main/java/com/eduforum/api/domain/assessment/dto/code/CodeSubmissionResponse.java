package com.eduforum.api.domain.assessment.dto.code;

import com.eduforum.api.domain.assessment.entity.SubmissionStatus;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.OffsetDateTime;
import java.util.Map;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Code submission response")
public class CodeSubmissionResponse {

    @Schema(description = "Submission ID", example = "1")
    private Long id;

    @Schema(description = "Assignment ID", example = "1")
    private Long assignmentId;

    @Schema(description = "Student ID", example = "1")
    private Long studentId;

    @Schema(description = "Programming language", example = "python")
    private String language;

    @Schema(description = "Source code")
    private String code;

    @Schema(description = "Submission status")
    private SubmissionStatus status;

    @Schema(description = "Submitted at")
    private OffsetDateTime submittedAt;

    @Schema(description = "Executed at")
    private OffsetDateTime executedAt;

    @Schema(description = "Passed tests", example = "8")
    private Integer passedTests;

    @Schema(description = "Total tests", example = "10")
    private Integer totalTests;

    @Schema(description = "Pass rate", example = "80.0")
    private Double passRate;

    @Schema(description = "Execution time (ms)", example = "1234")
    private Long executionTimeMs;

    @Schema(description = "Memory used (KB)", example = "2048")
    private Long memoryUsedKb;

    @Schema(description = "Compiler output")
    private Map<String, Object> compilerOutput;
}
