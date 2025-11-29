package com.eduforum.api.domain.analytics.dto.risk;

import com.eduforum.api.domain.analytics.entity.AlertStatus;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.OffsetDateTime;
import java.util.Map;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Risk alert response")
public class RiskAlertResponse {

    @Schema(description = "Alert ID", example = "1")
    private Long id;

    @Schema(description = "Risk indicator ID", example = "1")
    private Long riskIndicatorId;

    @Schema(description = "Student ID", example = "1")
    private Long studentId;

    @Schema(description = "Course ID", example = "1")
    private Long courseId;

    @Schema(description = "Instructor ID", example = "10")
    private Long instructorId;

    @Schema(description = "Alert status")
    private AlertStatus status;

    @Schema(description = "Alert message")
    private String alertMessage;

    @Schema(description = "Recommendations")
    private String recommendations;

    @Schema(description = "Sent timestamp")
    private OffsetDateTime sentAt;

    @Schema(description = "Acknowledged timestamp")
    private OffsetDateTime acknowledgedAt;

    @Schema(description = "Acknowledged by user ID", example = "10")
    private Long acknowledgedBy;

    @Schema(description = "Resolved timestamp")
    private OffsetDateTime resolvedAt;

    @Schema(description = "Resolved by user ID", example = "10")
    private Long resolvedBy;

    @Schema(description = "Resolution notes")
    private String resolutionNotes;

    @Schema(description = "Alert data")
    private Map<String, Object> alertData;

    @Schema(description = "Created timestamp")
    private OffsetDateTime createdAt;
}
