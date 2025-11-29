package com.eduforum.api.domain.assessment.entity;

import com.eduforum.api.domain.common.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.Map;

/**
 * Participation score entity
 */
@Entity
@Table(schema = "assessment", name = "participation_scores")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ParticipationScore extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "student_id", nullable = false)
    private Long studentId;

    @Column(name = "course_id", nullable = false)
    private Long courseId;

    @Column(name = "total_score", nullable = false, precision = 10, scale = 2)
    @Builder.Default
    private BigDecimal totalScore = BigDecimal.ZERO;

    @Column(name = "attendance_score", precision = 10, scale = 2)
    @Builder.Default
    private BigDecimal attendanceScore = BigDecimal.ZERO;

    @Column(name = "activity_score", precision = 10, scale = 2)
    @Builder.Default
    private BigDecimal activityScore = BigDecimal.ZERO;

    @Column(name = "engagement_score", precision = 10, scale = 2)
    @Builder.Default
    private BigDecimal engagementScore = BigDecimal.ZERO;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "score_breakdown", columnDefinition = "jsonb")
    @Builder.Default
    private Map<String, Object> scoreBreakdown = Map.of();

    @Column(name = "last_calculated_at")
    private OffsetDateTime lastCalculatedAt;

    // Helper methods
    public void recalculate() {
        this.lastCalculatedAt = OffsetDateTime.now();
    }

    public void addScore(BigDecimal points) {
        this.totalScore = this.totalScore.add(points);
    }
}
