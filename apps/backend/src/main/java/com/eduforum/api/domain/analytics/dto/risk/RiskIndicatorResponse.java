package com.eduforum.api.domain.analytics.dto.risk;

import com.eduforum.api.domain.analytics.entity.RiskLevel;
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
@Schema(description = "Risk indicator response")
public class RiskIndicatorResponse {

    @Schema(description = "Risk indicator ID", example = "1")
    private Long id;

    @Schema(description = "Student ID", example = "1")
    private Long studentId;

    @Schema(description = "Course ID", example = "1")
    private Long courseId;

    @Schema(description = "Risk level")
    private RiskLevel riskLevel;

    @Schema(description = "Overall risk score", example = "65.5")
    private BigDecimal riskScore;

    @Schema(description = "Attendance risk", example = "30.0")
    private BigDecimal attendanceRisk;

    @Schema(description = "Engagement risk", example = "45.0")
    private BigDecimal engagementRisk;

    @Schema(description = "Performance risk", example = "55.0")
    private BigDecimal performanceRisk;

    @Schema(description = "Calculated timestamp")
    private OffsetDateTime calculatedAt;

    @Schema(description = "Last activity timestamp")
    private OffsetDateTime lastActivityAt;

    @Schema(description = "Days inactive", example = "7")
    private Integer daysInactive;

    @Schema(description = "Risk factors")
    private Map<String, Object> riskFactors;

    @Schema(description = "Is at risk", example = "true")
    private Boolean atRisk;
}
