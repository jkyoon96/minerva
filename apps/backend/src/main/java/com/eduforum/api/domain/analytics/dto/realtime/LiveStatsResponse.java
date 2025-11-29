package com.eduforum.api.domain.analytics.dto.realtime;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.OffsetDateTime;
import java.util.Map;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Live statistics response")
public class LiveStatsResponse {

    @Schema(description = "Session or course ID")
    private Long targetId;

    @Schema(description = "Current participants online", example = "20")
    private Integer currentParticipants;

    @Schema(description = "Peak participants", example = "25")
    private Integer peakParticipants;

    @Schema(description = "Average engagement rate", example = "78.5")
    private Double avgEngagementRate;

    @Schema(description = "Total interactions", example = "150")
    private Integer totalInteractions;

    @Schema(description = "Interactions per minute", example = "12.5")
    private Double interactionsPerMinute;

    @Schema(description = "Active students count", example = "18")
    private Integer activeStudents;

    @Schema(description = "Participation rate", example = "85.0")
    private Double participationRate;

    @Schema(description = "Last update time")
    private OffsetDateTime lastUpdated;

    @Schema(description = "Breakdown by activity type")
    private Map<String, Integer> activityBreakdown;
}
