package com.eduforum.api.domain.assessment.dto.participation;

import com.eduforum.api.domain.assessment.entity.EventType;
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
@Schema(description = "Update weight request")
public class UpdateWeightsRequest {

    @NotNull
    @Schema(description = "Event type")
    private EventType eventType;

    @NotNull
    @Schema(description = "Weight", example = "2.0")
    private BigDecimal weight;

    @Schema(description = "Is enabled", example = "true")
    @Builder.Default
    private Boolean isEnabled = true;

    @Schema(description = "Description")
    private String description;
}
