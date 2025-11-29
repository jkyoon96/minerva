package com.eduforum.api.domain.analytics.dto.risk;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Resolve alert request")
public class ResolveAlertRequest {

    @NotBlank
    @Schema(description = "Resolution notes", example = "Met with student, created action plan")
    private String resolutionNotes;
}
