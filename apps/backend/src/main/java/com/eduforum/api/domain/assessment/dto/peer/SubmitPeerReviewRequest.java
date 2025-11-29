package com.eduforum.api.domain.assessment.dto.peer;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.Map;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Submit peer review request")
public class SubmitPeerReviewRequest {

    @NotNull
    @Schema(description = "Submission ID being reviewed", example = "1")
    private Long submissionId;

    @NotNull
    @Schema(description = "Score", example = "85.5")
    private BigDecimal score;

    @Schema(description = "Comments")
    private String comments;

    @Schema(description = "Rubric scores")
    private Map<String, Object> rubricScores;
}
