package com.eduforum.api.domain.analytics.dto.realtime;

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
@Schema(description = "Analytics snapshot creation request")
public class SnapshotRequest {

    @NotNull
    @Positive
    @Schema(description = "Course ID", example = "1")
    private Long courseId;

    @Schema(description = "Session ID (optional)", example = "1")
    private Long sessionId;

    @Schema(description = "Total participants", example = "25")
    private Integer totalParticipants;

    @Schema(description = "Active participants", example = "20")
    private Integer activeParticipants;

    @Schema(description = "Average engagement score", example = "75.5")
    private Double avgEngagementScore;

    @Schema(description = "Additional metrics data")
    private Map<String, Object> metricsData;
}
