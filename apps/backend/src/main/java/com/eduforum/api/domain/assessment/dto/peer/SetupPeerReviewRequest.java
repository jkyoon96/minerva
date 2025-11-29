package com.eduforum.api.domain.assessment.dto.peer;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
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
@Schema(description = "Setup peer review request")
public class SetupPeerReviewRequest {

    @NotNull
    @Schema(description = "Number of reviews per submission", example = "3")
    @Builder.Default
    private Integer reviewsPerSubmission = 3;

    @Schema(description = "Is anonymous", example = "true")
    @Builder.Default
    private Boolean isAnonymous = true;

    @Schema(description = "Is auto assigned", example = "true")
    @Builder.Default
    private Boolean isAutoAssigned = true;

    @Schema(description = "Review deadline")
    private OffsetDateTime reviewDeadline;

    @Schema(description = "Min score", example = "0")
    private Integer minScore;

    @Schema(description = "Max score", example = "100")
    private Integer maxScore;

    @Schema(description = "Rubric")
    private Map<String, Object> rubric;

    @Schema(description = "Remove outliers", example = "true")
    @Builder.Default
    private Boolean removeOutliers = true;
}
