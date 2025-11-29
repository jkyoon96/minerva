package com.eduforum.api.domain.assessment.dto.grading;

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
@Schema(description = "AI grading request")
public class AiGradeRequest {

    @NotNull
    @Schema(description = "Submission ID", example = "1")
    private Long submissionId;

    @Schema(description = "Generate feedback", example = "true")
    @Builder.Default
    private Boolean generateFeedback = true;

    @Schema(description = "AI model to use", example = "gpt-4")
    private String aiModel;
}
