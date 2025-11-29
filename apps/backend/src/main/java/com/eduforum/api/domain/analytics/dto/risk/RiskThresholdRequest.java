package com.eduforum.api.domain.analytics.dto.risk;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Risk threshold configuration request")
public class RiskThresholdRequest {

    @NotNull
    @Min(0)
    @Max(100)
    @Schema(description = "Attendance risk threshold", example = "70.0")
    private BigDecimal attendanceThreshold;

    @NotNull
    @Min(0)
    @Max(100)
    @Schema(description = "Engagement risk threshold", example = "60.0")
    private BigDecimal engagementThreshold;

    @NotNull
    @Min(0)
    @Max(100)
    @Schema(description = "Performance risk threshold", example = "65.0")
    private BigDecimal performanceThreshold;

    @NotNull
    @Min(1)
    @Max(30)
    @Schema(description = "Inactivity days threshold", example = "7")
    private Integer inactivityDays;
}
