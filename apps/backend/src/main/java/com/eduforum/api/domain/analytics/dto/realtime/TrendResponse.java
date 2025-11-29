package com.eduforum.api.domain.analytics.dto.realtime;

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
@Schema(description = "Trend analysis response")
public class TrendResponse {

    @Schema(description = "Course ID", example = "1")
    private Long courseId;

    @Schema(description = "Metric name", example = "engagement")
    private String metricName;

    @Schema(description = "Start date")
    private OffsetDateTime startDate;

    @Schema(description = "End date")
    private OffsetDateTime endDate;

    @Schema(description = "Time series data points")
    private List<DataPoint> dataPoints;

    @Schema(description = "Trend direction", example = "INCREASING")
    private String trendDirection;

    @Schema(description = "Change percentage", example = "15.5")
    private Double changePercentage;

    @Schema(description = "Summary statistics")
    private Map<String, Object> statistics;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class DataPoint {
        @Schema(description = "Timestamp")
        private OffsetDateTime timestamp;

        @Schema(description = "Value", example = "75.5")
        private Double value;

        @Schema(description = "Additional metadata")
        private Map<String, Object> metadata;
    }
}
