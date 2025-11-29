package com.eduforum.api.domain.assessment.dto.feedback;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Learning resource response")
public class LearningResourceResponse {

    @Schema(description = "Resource ID", example = "1")
    private Long id;

    @Schema(description = "Topic", example = "Recursion")
    private String topic;

    @Schema(description = "Resource type", example = "VIDEO")
    private String resourceType;

    @Schema(description = "Title")
    private String title;

    @Schema(description = "URL")
    private String url;

    @Schema(description = "Description")
    private String description;

    @Schema(description = "Difficulty level", example = "INTERMEDIATE")
    private String difficultyLevel;

    @Schema(description = "Estimated duration (minutes)", example = "30")
    private Integer estimatedDurationMinutes;

    @Schema(description = "Relevance score", example = "85")
    private Integer relevanceScore;

    @Schema(description = "Tags")
    private Map<String, Object> tags;

    @Schema(description = "Is completed", example = "false")
    private Boolean isCompleted;

    @Schema(description = "Is bookmarked", example = "true")
    private Boolean isBookmarked;
}
