package com.eduforum.api.domain.assessment.dto.peer;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Aggregated peer review result response")
public class PeerReviewResultResponse {

    @Schema(description = "Submission ID", example = "1")
    private Long submissionId;

    @Schema(description = "Student ID", example = "1")
    private Long studentId;

    @Schema(description = "Average score", example = "85.5")
    private BigDecimal averageScore;

    @Schema(description = "Median score", example = "87.0")
    private BigDecimal medianScore;

    @Schema(description = "Min score", example = "75.0")
    private BigDecimal minScore;

    @Schema(description = "Max score", example = "95.0")
    private BigDecimal maxScore;

    @Schema(description = "Standard deviation", example = "8.5")
    private BigDecimal standardDeviation;

    @Schema(description = "Total reviews", example = "3")
    private Integer totalReviews;

    @Schema(description = "Outliers removed", example = "0")
    private Integer outliersRemoved;

    @Schema(description = "Individual reviews")
    private List<PeerReviewResponse> reviews;
}
