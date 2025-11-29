package com.eduforum.api.domain.assessment.dto.participation;

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
@Schema(description = "Participation score response")
public class ParticipationScoreResponse {

    @Schema(description = "Score ID", example = "1")
    private Long id;

    @Schema(description = "Student ID", example = "1")
    private Long studentId;

    @Schema(description = "Course ID", example = "1")
    private Long courseId;

    @Schema(description = "Total score", example = "85.5")
    private BigDecimal totalScore;

    @Schema(description = "Attendance score", example = "30.0")
    private BigDecimal attendanceScore;

    @Schema(description = "Activity score", example = "40.0")
    private BigDecimal activityScore;

    @Schema(description = "Engagement score", example = "15.5")
    private BigDecimal engagementScore;

    @Schema(description = "Score breakdown")
    private Map<String, Object> scoreBreakdown;

    @Schema(description = "Last calculated at")
    private OffsetDateTime lastCalculatedAt;
}
