package com.eduforum.api.domain.assessment.dto.feedback;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Generate feedback request")
public class GenerateFeedbackRequest {

    @NotNull
    @Schema(description = "Submission ID", example = "1")
    private Long submissionId;

    @Schema(description = "Include learning resources", example = "true")
    @Builder.Default
    private Boolean includeResources = true;
}
