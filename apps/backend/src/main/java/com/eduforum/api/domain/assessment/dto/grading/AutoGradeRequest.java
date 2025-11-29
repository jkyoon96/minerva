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
@Schema(description = "Auto grading request")
public class AutoGradeRequest {

    @NotNull
    @Schema(description = "Quiz session ID", example = "1")
    private Long quizSessionId;

    @Schema(description = "Include answer statistics", example = "true")
    @Builder.Default
    private Boolean includeStatistics = true;
}
