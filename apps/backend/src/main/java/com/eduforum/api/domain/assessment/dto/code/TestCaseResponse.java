package com.eduforum.api.domain.assessment.dto.code;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Test case response")
public class TestCaseResponse {

    @Schema(description = "Test case ID", example = "1")
    private Long id;

    @Schema(description = "Assignment ID", example = "1")
    private Long assignmentId;

    @Schema(description = "Test name", example = "Test case 1")
    private String testName;

    @Schema(description = "Input data (visible test cases only)")
    private String inputData;

    @Schema(description = "Expected output (visible test cases only)")
    private String expectedOutput;

    @Schema(description = "Is hidden", example = "false")
    private Boolean isHidden;

    @Schema(description = "Points", example = "10")
    private Integer points;

    @Schema(description = "Time limit (ms)", example = "1000")
    private Integer timeLimitMs;

    @Schema(description = "Memory limit (KB)", example = "256000")
    private Integer memoryLimitKb;
}
