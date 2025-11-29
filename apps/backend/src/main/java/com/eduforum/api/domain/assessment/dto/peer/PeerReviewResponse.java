package com.eduforum.api.domain.assessment.dto.peer;

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
@Schema(description = "Peer review response")
public class PeerReviewResponse {

    @Schema(description = "Review ID", example = "1")
    private Long id;

    @Schema(description = "Assignment ID", example = "1")
    private Long assignmentId;

    @Schema(description = "Submission ID", example = "1")
    private Long submissionId;

    @Schema(description = "Reviewer ID (null if anonymous)", example = "2")
    private Long reviewerId;

    @Schema(description = "Reviewee ID", example = "1")
    private Long revieweeId;

    @Schema(description = "Score", example = "85.5")
    private BigDecimal score;

    @Schema(description = "Max score", example = "100.0")
    private BigDecimal maxScore;

    @Schema(description = "Comments")
    private String comments;

    @Schema(description = "Rubric scores")
    private Map<String, Object> rubricScores;

    @Schema(description = "Is submitted", example = "true")
    private Boolean isSubmitted;

    @Schema(description = "Submitted at")
    private OffsetDateTime submittedAt;

    @Schema(description = "Is outlier", example = "false")
    private Boolean isOutlier;

    @Schema(description = "Is anonymous", example = "true")
    private Boolean isAnonymous;
}
