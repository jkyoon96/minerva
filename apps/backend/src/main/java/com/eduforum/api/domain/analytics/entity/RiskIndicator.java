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
 * Risk indicator - student risk assessment
 */
@Entity
@Table(schema = "analytics", name = "risk_indicators")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RiskIndicator extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "student_id", nullable = false)
    private Long studentId;

    @Column(name = "course_id", nullable = false)
    private Long courseId;

    @Enumerated(EnumType.STRING)
    @Column(name = "risk_level", nullable = false)
    @Builder.Default
    private RiskLevel riskLevel = RiskLevel.LOW;

    @Column(name = "risk_score", precision = 5, scale = 2, nullable = false)
    private BigDecimal riskScore;

    @Column(name = "attendance_risk", precision = 5, scale = 2)
    private BigDecimal attendanceRisk;

    @Column(name = "engagement_risk", precision = 5, scale = 2)
    private BigDecimal engagementRisk;

    @Column(name = "performance_risk", precision = 5, scale = 2)
    private BigDecimal performanceRisk;

    @Column(name = "calculated_at", nullable = false)
    private OffsetDateTime calculatedAt;

    @Column(name = "last_activity_at")
    private OffsetDateTime lastActivityAt;

    @Column(name = "days_inactive")
    @Builder.Default
    private Integer daysInactive = 0;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(columnDefinition = "jsonb")
    @Builder.Default
    private Map<String, Object> riskFactors = Map.of();

    // Helper methods
    public void calculateRiskLevel() {
        int score = riskScore.intValue();
        if (score >= 76) {
            this.riskLevel = RiskLevel.CRITICAL;
        } else if (score >= 51) {
            this.riskLevel = RiskLevel.HIGH;
        } else if (score >= 26) {
            this.riskLevel = RiskLevel.MEDIUM;
        } else {
            this.riskLevel = RiskLevel.LOW;
        }
    }

    public boolean isAtRisk() {
        return riskLevel == RiskLevel.HIGH || riskLevel == RiskLevel.CRITICAL;
    }

    public void updateCalculationTime() {
        this.calculatedAt = OffsetDateTime.now();
    }
}
