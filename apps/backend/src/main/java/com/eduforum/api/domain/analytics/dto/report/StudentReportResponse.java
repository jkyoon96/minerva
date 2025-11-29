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
@Schema(description = "Student report response")
public class StudentReportResponse {

    @Schema(description = "Report ID", example = "1")
    private Long id;

    @Schema(description = "Student ID", example = "1")
    private Long studentId;

    @Schema(description = "Course ID", example = "1")
    private Long courseId;

    @Schema(description = "Report period")
    private ReportPeriod period;

    @Schema(description = "Period start date")
    private OffsetDateTime periodStart;

    @Schema(description = "Period end date")
    private OffsetDateTime periodEnd;

    @Schema(description = "Attendance rate", example = "92.5")
    private BigDecimal attendanceRate;

    @Schema(description = "Engagement score", example = "85.0")
    private BigDecimal engagementScore;

    @Schema(description = "Performance score", example = "78.5")
    private BigDecimal performanceScore;

    @Schema(description = "Participation count", example = "45")
    private Integer participationCount;

    @Schema(description = "Quiz average score", example = "88.0")
    private BigDecimal quizAvgScore;

    @Schema(description = "Assignment average score", example = "82.5")
    private BigDecimal assignmentAvgScore;

    @Schema(description = "Overall score", example = "81.75")
    private BigDecimal overallScore;

    @Schema(description = "Summary text")
    private String summary;

    @Schema(description = "Detailed metrics")
    private Map<String, Object> detailedMetrics;

    @Schema(description = "Report URL")
    private String reportUrl;

    @Schema(description = "Generated timestamp")
    private OffsetDateTime generatedAt;

    @Schema(description = "Created timestamp")
    private OffsetDateTime createdAt;
}
