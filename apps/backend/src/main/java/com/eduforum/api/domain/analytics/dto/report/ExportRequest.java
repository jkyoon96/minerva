package com.eduforum.api.domain.analytics.dto.report;

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
@Schema(description = "Report export request")
public class ExportRequest {

    @NotNull
    @Schema(description = "Course ID", example = "1")
    private Long courseId;

    @Schema(description = "Start date for export")
    private OffsetDateTime startDate;

    @Schema(description = "End date for export")
    private OffsetDateTime endDate;

    @Schema(description = "Export format (PDF/EXCEL)", example = "EXCEL")
    @Builder.Default
    private String format = "EXCEL";

    @Schema(description = "Include student details", example = "true")
    @Builder.Default
    private Boolean includeStudentDetails = true;

    @Schema(description = "Include metrics breakdown", example = "true")
    @Builder.Default
    private Boolean includeMetricsBreakdown = true;
}
