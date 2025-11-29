package com.eduforum.api.domain.analytics.dto.network;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.Map;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Network timeline response")
public class TimelineResponse {

    @Schema(description = "Course ID", example = "1")
    private Long courseId;

    @Schema(description = "Start date")
    private OffsetDateTime startDate;

    @Schema(description = "End date")
    private OffsetDateTime endDate;

    @Schema(description = "Timeline data points")
    private List<TimePoint> timePoints;

    @Schema(description = "Summary statistics")
    private Map<String, Object> summary;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class TimePoint {
        @Schema(description = "Timestamp")
        private OffsetDateTime timestamp;

        @Schema(description = "Total interactions", example = "45")
        private Integer totalInteractions;

        @Schema(description = "Active students", example = "25")
        private Integer activeStudents;

        @Schema(description = "Network density", example = "0.15")
        private Double networkDensity;

        @Schema(description = "Breakdown by interaction type")
        private Map<String, Integer> interactionBreakdown;
    }
}
