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
@Schema(description = "Student connection analysis response")
public class StudentConnectionResponse {

    @Schema(description = "Student ID", example = "1")
    private Long studentId;

    @Schema(description = "Course ID", example = "1")
    private Long courseId;

    @Schema(description = "Total connections", example = "15")
    private Integer totalConnections;

    @Schema(description = "Degree centrality", example = "0.30")
    private Double degreeCentrality;

    @Schema(description = "Betweenness centrality", example = "0.18")
    private Double betweennessCentrality;

    @Schema(description = "Closeness centrality", example = "0.42")
    private Double closenessCentrality;

    @Schema(description = "Clustering coefficient", example = "0.65")
    private Double clusteringCoefficient;

    @Schema(description = "Cluster ID", example = "1")
    private Long clusterId;

    @Schema(description = "Connection list")
    private List<Connection> connections;

    @Schema(description = "Interaction summary")
    private Map<String, Integer> interactionSummary;

    @Schema(description = "Is isolated", example = "false")
    private Boolean isolated;

    @Schema(description = "Is hub", example = "false")
    private Boolean hub;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Connection {
        @Schema(description = "Connected student ID", example = "2")
        private Long connectedStudentId;

        @Schema(description = "Interaction count", example = "12")
        private Integer interactionCount;

        @Schema(description = "Total weight", example = "15")
        private Integer totalWeight;

        @Schema(description = "Connection strength", example = "STRONG")
        private String strength;
    }
}
