package com.eduforum.api.domain.assessment.dto.participation;

import com.eduforum.api.domain.assessment.entity.EventType;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Participation weight response")
public class ParticipationWeightResponse {

    @Schema(description = "Weight ID", example = "1")
    private Long id;

    @Schema(description = "Course ID", example = "1")
    private Long courseId;

    @Schema(description = "Event type")
    private EventType eventType;

    @Schema(description = "Weight", example = "2.0")
    private BigDecimal weight;

    @Schema(description = "Is enabled", example = "true")
    private Boolean isEnabled;

    @Schema(description = "Description")
    private String description;
}
