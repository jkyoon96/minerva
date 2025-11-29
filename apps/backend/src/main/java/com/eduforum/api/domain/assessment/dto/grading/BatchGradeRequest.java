package com.eduforum.api.domain.assessment.dto.grading;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotEmpty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Batch AI grading request")
public class BatchGradeRequest {

    @NotEmpty
    @Schema(description = "List of submission IDs to grade")
    private List<Long> submissionIds;

    @Schema(description = "Generate feedback for all", example = "true")
    @Builder.Default
    private Boolean generateFeedback = true;
}
