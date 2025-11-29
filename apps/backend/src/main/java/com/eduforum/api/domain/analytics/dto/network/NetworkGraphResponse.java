package com.eduforum.api.domain.analytics.dto.network;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Map;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Network graph response")
public class NetworkGraphResponse {

    @Schema(description = "Course ID", example = "1")
    private Long courseId;

    @Schema(description = "Total nodes", example = "50")
    private Integer totalNodes;

    @Schema(description = "Total edges", example = "150")
    private Integer totalEdges;

    @Schema(description = "Network density", example = "0.12")
    private Double density;

    @Schema(description = "Average degree", example = "6.0")
    private Double averageDegree;

    @Schema(description = "Node list")
    private List<NodeData> nodes;

    @Schema(description = "Edge list")
    private List<EdgeData> edges;

    @Schema(description = "Network statistics")
    private Map<String, Object> statistics;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class NodeData {
        @Schema(description = "Node ID", example = "1")
        private Long id;

        @Schema(description = "Student ID", example = "1")
        private Long studentId;

        @Schema(description = "Degree centrality", example = "0.25")
        private Double degreeCentrality;

        @Schema(description = "Betweenness centrality", example = "0.15")
        private Double betweennessCentrality;

        @Schema(description = "Closeness centrality", example = "0.35")
        private Double closenessCentrality;

        @Schema(description = "Cluster ID", example = "1")
        private Long clusterId;

        @Schema(description = "Total connections", example = "12")
        private Integer totalConnections;

        @Schema(description = "Node attributes")
        private Map<String, Object> attributes;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class EdgeData {
        @Schema(description = "Edge ID", example = "1")
        private Long id;

        @Schema(description = "From student ID", example = "1")
        private Long fromStudentId;

        @Schema(description = "To student ID", example = "2")
        private Long toStudentId;

        @Schema(description = "Interaction count", example = "15")
        private Integer interactionCount;

        @Schema(description = "Total weight", example = "18")
        private Integer totalWeight;

        @Schema(description = "Edge strength", example = "18.0")
        private Double strength;

        @Schema(description = "Edge attributes")
        private Map<String, Object> attributes;
    }
}
