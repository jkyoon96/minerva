package com.eduforum.api.domain.analytics.entity;

import com.eduforum.api.domain.common.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.util.Map;

/**
 * Network edge - connection between students
 */
@Entity
@Table(schema = "analytics", name = "network_edges")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class NetworkEdge extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "course_id", nullable = false)
    private Long courseId;

    @Column(name = "from_student_id", nullable = false)
    private Long fromStudentId;

    @Column(name = "to_student_id", nullable = false)
    private Long toStudentId;

    @Column(name = "interaction_count")
    @Builder.Default
    private Integer interactionCount = 0;

    @Column(name = "total_weight")
    @Builder.Default
    private Integer totalWeight = 0;

    @Column(name = "last_interaction_at")
    private java.time.OffsetDateTime lastInteractionAt;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(columnDefinition = "jsonb")
    @Builder.Default
    private Map<String, Object> edgeAttributes = Map.of();

    // Helper methods
    public void incrementInteraction(Integer weight) {
        this.interactionCount++;
        this.totalWeight += weight;
        this.lastInteractionAt = java.time.OffsetDateTime.now();
    }

    public boolean isStrong() {
        return totalWeight > 10;
    }

    public boolean isWeak() {
        return totalWeight <= 3;
    }

    public Double getStrength() {
        return totalWeight.doubleValue();
    }
}
