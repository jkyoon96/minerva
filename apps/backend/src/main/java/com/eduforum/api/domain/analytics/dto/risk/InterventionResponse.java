package com.eduforum.api.domain.analytics.dto.risk;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Intervention suggestions response")
public class InterventionResponse {

    @Schema(description = "Student ID", example = "1")
    private Long studentId;

    @Schema(description = "Course ID", example = "1")
    private Long courseId;

    @Schema(description = "Primary concern", example = "Low engagement and attendance")
    private String primaryConcern;

    @Schema(description = "Suggested interventions")
    private List<Suggestion> suggestions;

    @Schema(description = "Recommended resources")
    private List<String> recommendedResources;

    @Schema(description = "Urgency level", example = "HIGH")
    private String urgencyLevel;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Suggestion {
        @Schema(description = "Intervention type", example = "ONE_ON_ONE_MEETING")
        private String type;

        @Schema(description = "Description")
        private String description;

        @Schema(description = "Priority", example = "HIGH")
        private String priority;

        @Schema(description = "Estimated impact", example = "MEDIUM")
        private String estimatedImpact;
    }
}
