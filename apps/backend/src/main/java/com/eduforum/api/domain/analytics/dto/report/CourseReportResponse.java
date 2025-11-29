package com.eduforum.api.domain.analytics.dto.report;

import com.eduforum.api.domain.analytics.entity.ReportPeriod;
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
@Schema(description = "Course report response")
public class CourseReportResponse {

    @Schema(description = "Report ID", example = "1")
    private Long id;

    @Schema(description = "Course ID", example = "1")
    private Long courseId;

    @Schema(description = "Report period")
    private ReportPeriod period;

    @Schema(description = "Period start date")
    private OffsetDateTime periodStart;

    @Schema(description = "Period end date")
    private OffsetDateTime periodEnd;

    @Schema(description = "Total students", example = "50")
    private Integer totalStudents;

    @Schema(description = "Active students", example = "45")
    private Integer activeStudents;

    @Schema(description = "Average attendance rate", example = "89.5")
    private BigDecimal avgAttendanceRate;

    @Schema(description = "Average engagement score", example = "82.0")
    private BigDecimal avgEngagementScore;

    @Schema(description = "Average performance score", example = "75.5")
    private BigDecimal avgPerformanceScore;

    @Schema(description = "Total sessions", example = "24")
    private Integer totalSessions;

    @Schema(description = "Total assignments", example = "12")
    private Integer totalAssignments;

    @Schema(description = "Completion rate", example = "92.0")
    private BigDecimal completionRate;

    @Schema(description = "At-risk students count", example = "5")
    private Integer atRiskStudents;

    @Schema(description = "Overall health score", example = "78.75")
    private BigDecimal overallHealth;

    @Schema(description = "Summary text")
    private String summary;

    @Schema(description = "Detailed statistics")
    private Map<String, Object> detailedStats;

    @Schema(description = "Report URL")
    private String reportUrl;

    @Schema(description = "Generated timestamp")
    private OffsetDateTime generatedAt;
}
