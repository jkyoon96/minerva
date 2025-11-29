package com.eduforum.api.domain.assessment.dto.grading;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Update grade request")
public class UpdateGradeRequest {

    @NotNull
    @Schema(description = "Score", example = "85.5")
    private BigDecimal score;

    @Schema(description = "Feedback", example = "Good work, but needs improvement in...")
    private String feedback;
}
