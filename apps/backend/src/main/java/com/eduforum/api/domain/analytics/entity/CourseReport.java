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
 * Course report - course-level analytics report
 */
@Entity
@Table(schema = "analytics", name = "course_reports")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CourseReport extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "course_id", nullable = false)
    private Long courseId;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ReportPeriod period;

    @Column(name = "period_start", nullable = false)
    private OffsetDateTime periodStart;

    @Column(name = "period_end", nullable = false)
    private OffsetDateTime periodEnd;

    @Column(name = "total_students")
    @Builder.Default
    private Integer totalStudents = 0;

    @Column(name = "active_students")
    @Builder.Default
    private Integer activeStudents = 0;

    @Column(name = "avg_attendance_rate", precision = 5, scale = 2)
    private BigDecimal avgAttendanceRate;

    @Column(name = "avg_engagement_score", precision = 5, scale = 2)
    private BigDecimal avgEngagementScore;

    @Column(name = "avg_performance_score", precision = 5, scale = 2)
    private BigDecimal avgPerformanceScore;

    @Column(name = "total_sessions")
    @Builder.Default
    private Integer totalSessions = 0;

    @Column(name = "total_assignments")
    @Builder.Default
    private Integer totalAssignments = 0;

    @Column(name = "completion_rate", precision = 5, scale = 2)
    private BigDecimal completionRate;

    @Column(name = "at_risk_students")
    @Builder.Default
    private Integer atRiskStudents = 0;

    @Column(length = 2000)
    private String summary;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(columnDefinition = "jsonb")
    @Builder.Default
    private Map<String, Object> detailedStats = Map.of();

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

    public BigDecimal getOverallHealth() {
        if (avgPerformanceScore == null || avgEngagementScore == null) {
            return BigDecimal.ZERO;
        }
        return avgPerformanceScore.add(avgEngagementScore).divide(BigDecimal.valueOf(2));
    }
}
