package com.eduforum.api.domain.analytics.dto.network;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.Map;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Student cluster response")
public class ClusterResponse {

    @Schema(description = "Cluster ID", example = "1")
    private Long id;

    @Schema(description = "Course ID", example = "1")
    private Long courseId;

    @Schema(description = "Cluster name", example = "High Interaction Group")
    private String clusterName;

    @Schema(description = "Cluster number", example = "1")
    private Integer clusterNumber;

    @Schema(description = "Member count", example = "12")
    private Integer memberCount;

    @Schema(description = "Average interaction score", example = "85.5")
    private Double avgInteractionScore;

    @Schema(description = "Cluster density", example = "0.65")
    private Double density;

    @Schema(description = "Description")
    private String description;

    @Schema(description = "Member student IDs")
    private List<Long> memberIds;

    @Schema(description = "Cluster statistics")
    private Map<String, Object> clusterStats;

    @Schema(description = "Created timestamp")
    private OffsetDateTime createdAt;
}
