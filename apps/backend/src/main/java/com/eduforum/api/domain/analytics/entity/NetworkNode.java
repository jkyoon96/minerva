package com.eduforum.api.domain.analytics.entity;

import com.eduforum.api.domain.common.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.util.Map;

/**
 * Network node - student node in interaction network
 */
@Entity
@Table(schema = "analytics", name = "network_nodes")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class NetworkNode extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "course_id", nullable = false)
    private Long courseId;

    @Column(name = "student_id", nullable = false)
    private Long studentId;

    @Column(name = "degree_centrality")
    private Double degreeCentrality;

    @Column(name = "betweenness_centrality")
    private Double betweennessCentrality;

    @Column(name = "closeness_centrality")
    private Double closenessCentrality;

    @Column(name = "clustering_coefficient")
    private Double clusteringCoefficient;

    @Column(name = "total_connections")
    @Builder.Default
    private Integer totalConnections = 0;

    @Column(name = "cluster_id")
    private Long clusterId;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(columnDefinition = "jsonb")
    @Builder.Default
    private Map<String, Object> nodeAttributes = Map.of();

    // Helper methods
    public void updateCentralities(Double degree, Double betweenness, Double closeness) {
        this.degreeCentrality = degree;
        this.betweennessCentrality = betweenness;
        this.closenessCentrality = closeness;
    }

    public boolean isIsolated() {
        return totalConnections == 0;
    }

    public boolean isHub() {
        return degreeCentrality != null && degreeCentrality > 0.7;
    }
}
