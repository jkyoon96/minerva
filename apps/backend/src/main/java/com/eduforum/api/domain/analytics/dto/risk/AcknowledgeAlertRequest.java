package com.eduforum.api.domain.analytics.dto.risk;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Acknowledge alert request")
public class AcknowledgeAlertRequest {

    @Schema(description = "Notes (optional)", example = "Contacted student via email")
    private String notes;
}
