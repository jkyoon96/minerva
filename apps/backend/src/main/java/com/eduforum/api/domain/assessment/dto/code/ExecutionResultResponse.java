package com.eduforum.api.domain.assessment.dto.code;

import com.eduforum.api.domain.assessment.entity.ExecutionStatus;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Execution result response")
public class ExecutionResultResponse {

    @Schema(description = "Result ID", example = "1")
    private Long id;

    @Schema(description = "Test case name", example = "Test case 1")
    private String testCaseName;

    @Schema(description = "Execution status")
    private ExecutionStatus status;

    @Schema(description = "Actual output")
    private String actualOutput;

    @Schema(description = "Expected output")
    private String expectedOutput;

    @Schema(description = "Error message")
    private String errorMessage;

    @Schema(description = "Execution time (ms)", example = "123")
    private Long executionTimeMs;

    @Schema(description = "Memory used (KB)", example = "512")
    private Long memoryUsedKb;

    @Schema(description = "Passed", example = "true")
    private Boolean passed;
}
