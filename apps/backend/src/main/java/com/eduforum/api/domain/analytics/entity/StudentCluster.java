package com.eduforum.api.domain.analytics.entity;

import com.eduforum.api.domain.common.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Student cluster - group of students with similar interaction patterns
 */
@Entity
@Table(schema = "analytics", name = "student_clusters")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class StudentCluster extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "course_id", nullable = false)
    private Long courseId;

    @Column(name = "cluster_name", length = 200)
    private String clusterName;

    @Column(name = "cluster_number")
    private Integer clusterNumber;

    @Column(name = "member_count")
    @Builder.Default
    private Integer memberCount = 0;

    @Column(name = "avg_interaction_score")
    private Double avgInteractionScore;

    @Column(name = "density")
    private Double density;

    @Column(length = 1000)
    private String description;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(columnDefinition = "jsonb")
    @Builder.Default
    private List<Long> memberIds = new ArrayList<>();

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(columnDefinition = "jsonb")
    @Builder.Default
    private Map<String, Object> clusterStats = Map.of();

    // Helper methods
    public void addMember(Long studentId) {
        if (!memberIds.contains(studentId)) {
            memberIds.add(studentId);
            memberCount++;
        }
    }

    public void removeMember(Long studentId) {
        if (memberIds.remove(studentId)) {
            memberCount--;
        }
    }

    public boolean hasMember(Long studentId) {
        return memberIds.contains(studentId);
    }

    public boolean isLarge() {
        return memberCount > 10;
    }

    public boolean isDense() {
        return density != null && density > 0.6;
    }
}
