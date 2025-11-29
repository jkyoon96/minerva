package com.eduforum.api.domain.assessment.dto.grading;

import com.eduforum.api.domain.assessment.entity.GradingStatus;
import com.eduforum.api.domain.assessment.entity.GradingType;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.Map;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Grading result response")
public class GradingResultResponse {

    @Schema(description = "Grading result ID", example = "1")
    private Long id;

    @Schema(description = "Assignment ID", example = "1")
    private Long assignmentId;

    @Schema(description = "Submission ID", example = "1")
    private Long submissionId;

    @Schema(description = "Student ID", example = "1")
    private Long studentId;

    @Schema(description = "Grader ID", example = "2")
    private Long graderId;

    @Schema(description = "Grading type")
    private GradingType gradingType;

    @Schema(description = "Grading status")
    private GradingStatus status;

    @Schema(description = "Score", example = "85.5")
    private BigDecimal score;

    @Schema(description = "Max score", example = "100.0")
    private BigDecimal maxScore;

    @Schema(description = "AI confidence (if AI graded)", example = "92.3")
    private BigDecimal aiConfidence;

    @Schema(description = "Feedback")
    private String feedback;

    @Schema(description = "Grading details")
    private Map<String, Object> gradingDetails;

    @Schema(description = "Graded at")
    private OffsetDateTime gradedAt;

    @Schema(description = "Reviewed at")
    private OffsetDateTime reviewedAt;

    @Schema(description = "Created at")
    private OffsetDateTime createdAt;
}
