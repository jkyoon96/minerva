package com.eduforum.api.domain.analytics.dto.report;

import com.eduforum.api.domain.analytics.entity.MetricType;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.Map;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Learning metric response")
public class MetricResponse {

    @Schema(description = "Metric ID", example = "1")
    private Long id;

    @Schema(description = "Student ID", example = "1")
    private Long studentId;

    @Schema(description = "Course ID", example = "1")
    private Long courseId;

    @Schema(description = "Metric type")
    private MetricType metricType;

    @Schema(description = "Metric value", example = "85.5")
    private BigDecimal metricValue;

    @Schema(description = "Period start")
    private OffsetDateTime periodStart;

    @Schema(description = "Period end")
    private OffsetDateTime periodEnd;

    @Schema(description = "Sample count", example = "25")
    private Integer sampleCount;

    @Schema(description = "Value breakdown")
    private Map<String, Object> breakdown;

    @Schema(description = "Created timestamp")
    private OffsetDateTime createdAt;
}
