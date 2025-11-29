package com.eduforum.api.domain.assessment.dto.participation;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.Map;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Participation dashboard response")
public class ParticipationDashboardResponse {

    @Schema(description = "Student ID", example = "1")
    private Long studentId;

    @Schema(description = "Course ID", example = "1")
    private Long courseId;

    @Schema(description = "Total score", example = "85.5")
    private BigDecimal totalScore;

    @Schema(description = "Class rank", example = "5")
    private Integer classRank;

    @Schema(description = "Class size", example = "30")
    private Integer classSize;

    @Schema(description = "Percentile", example = "83.3")
    private Double percentile;

    @Schema(description = "Score breakdown by event type")
    private Map<String, BigDecimal> scoreByEventType;

    @Schema(description = "Event counts")
    private Map<String, Integer> eventCounts;

    @Schema(description = "Trend data")
    private Map<String, Object> trendData;
}
