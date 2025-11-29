package com.eduforum.api.domain.analytics.dto.network;

import com.eduforum.api.domain.analytics.entity.InteractionType;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Interaction log request")
public class InteractionLogRequest {

    @NotNull
    @Positive
    @Schema(description = "Course ID", example = "1")
    private Long courseId;

    @Schema(description = "Session ID (optional)", example = "1")
    private Long sessionId;

    @NotNull
    @Positive
    @Schema(description = "From student ID", example = "1")
    private Long fromStudentId;

    @NotNull
    @Positive
    @Schema(description = "To student ID", example = "2")
    private Long toStudentId;

    @NotNull
    @Schema(description = "Interaction type")
    private InteractionType interactionType;

    @Schema(description = "Interaction weight", example = "1")
    @Builder.Default
    private Integer weight = 1;

    @Schema(description = "Context/description", example = "Replied to discussion")
    private String context;

    @Schema(description = "Additional metadata")
    private Map<String, Object> metadata;
}
