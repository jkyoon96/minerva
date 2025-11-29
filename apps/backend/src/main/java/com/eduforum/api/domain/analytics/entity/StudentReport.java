package com.eduforum.api.domain.analytics.entity;

import com.eduforum.api.domain.common.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.Map;

/**
 * Student report - individual student performance report
 */
@Entity
@Table(schema = "analytics", name = "student_reports")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class StudentReport extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "student_id", nullable = false)
    private Long studentId;

    @Column(name = "course_id", nullable = false)
    private Long courseId;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ReportPeriod period;

    @Column(name = "period_start", nullable = false)
    private OffsetDateTime periodStart;

    @Column(name = "period_end", nullable = false)
    private OffsetDateTime periodEnd;

    @Column(name = "attendance_rate", precision = 5, scale = 2)
    private BigDecimal attendanceRate;

    @Column(name = "engagement_score", precision = 5, scale = 2)
    private BigDecimal engagementScore;

    @Column(name = "performance_score", precision = 5, scale = 2)
    private BigDecimal performanceScore;

    @Column(name = "participation_count")
    @Builder.Default
    private Integer participationCount = 0;

    @Column(name = "quiz_avg_score", precision = 5, scale = 2)
    private BigDecimal quizAvgScore;

    @Column(name = "assignment_avg_score", precision = 5, scale = 2)
    private BigDecimal assignmentAvgScore;

    @Column(length = 2000)
    private String summary;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(columnDefinition = "jsonb")
    @Builder.Default
    private Map<String, Object> detailedMetrics = Map.of();

    @Column(name = "generated_at")
    private OffsetDateTime generatedAt;

    @Column(name = "report_url")
    private String reportUrl;

    // Helper methods
    public void markAsGenerated() {
        this.generatedAt = OffsetDateTime.now();
    }

    public boolean isGenerated() {
        return this.generatedAt != null;
    }

    public BigDecimal getOverallScore() {
        if (performanceScore == null) return BigDecimal.ZERO;
        return performanceScore;
    }
}
