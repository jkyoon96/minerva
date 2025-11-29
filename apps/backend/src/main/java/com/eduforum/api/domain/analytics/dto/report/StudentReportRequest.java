package com.eduforum.api.domain.analytics.dto.report;

import com.eduforum.api.domain.analytics.entity.ReportPeriod;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.OffsetDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Student report generation request")
public class StudentReportRequest {

    @NotNull
    @Schema(description = "Student ID", example = "1")
    private Long studentId;

    @NotNull
    @Schema(description = "Course ID", example = "1")
    private Long courseId;

    @NotNull
    @Schema(description = "Report period")
    private ReportPeriod period;

    @Schema(description = "Period start (for CUSTOM period)")
    private OffsetDateTime periodStart;

    @Schema(description = "Period end (for CUSTOM period)")
    private OffsetDateTime periodEnd;

    @Schema(description = "Include detailed metrics", example = "true")
    @Builder.Default
    private Boolean includeDetails = true;
}
