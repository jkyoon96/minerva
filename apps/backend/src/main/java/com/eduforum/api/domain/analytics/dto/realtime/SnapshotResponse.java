package com.eduforum.api.domain.analytics.dto.realtime;

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
@Schema(description = "Analytics snapshot response")
public class SnapshotResponse {

    @Schema(description = "Snapshot ID", example = "1")
    private Long id;

    @Schema(description = "Course ID", example = "1")
    private Long courseId;

    @Schema(description = "Session ID", example = "1")
    private Long sessionId;

    @Schema(description = "Snapshot timestamp")
    private OffsetDateTime snapshotTime;

    @Schema(description = "Total participants", example = "25")
    private Integer totalParticipants;

    @Schema(description = "Active participants", example = "20")
    private Integer activeParticipants;

    @Schema(description = "Average engagement score", example = "75.5")
    private Double avgEngagementScore;

    @Schema(description = "Total interactions", example = "150")
    private Integer totalInteractions;

    @Schema(description = "Poll responses", example = "18")
    private Integer pollResponses;

    @Schema(description = "Quiz attempts", example = "22")
    private Integer quizAttempts;

    @Schema(description = "Chat messages", example = "85")
    private Integer chatMessages;

    @Schema(description = "Additional metrics data")
    private Map<String, Object> metricsData;

    @Schema(description = "Created timestamp")
    private OffsetDateTime createdAt;
}
