package com.eduforum.api.domain.analytics.entity;

import com.eduforum.api.domain.common.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Risk alert - notification for at-risk students
 */
@Entity
@Table(schema = "analytics", name = "risk_alerts")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RiskAlert extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "risk_indicator_id", nullable = false)
    private RiskIndicator riskIndicator;

    @Column(name = "student_id", nullable = false)
    private Long studentId;

    @Column(name = "course_id", nullable = false)
    private Long courseId;

    @Column(name = "instructor_id")
    private Long instructorId;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    @Builder.Default
    private AlertStatus status = AlertStatus.PENDING;

    @Column(nullable = false, length = 500)
    private String alertMessage;

    @Column(length = 2000)
    private String recommendations;

    @Column(name = "sent_at")
    private OffsetDateTime sentAt;

    @Column(name = "acknowledged_at")
    private OffsetDateTime acknowledgedAt;

    @Column(name = "acknowledged_by")
    private Long acknowledgedBy;

    @Column(name = "resolved_at")
    private OffsetDateTime resolvedAt;

    @Column(name = "resolved_by")
    private Long resolvedBy;

    @Column(name = "resolution_notes", length = 2000)
    private String resolutionNotes;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(columnDefinition = "jsonb")
    @Builder.Default
    private Map<String, Object> alertData = Map.of();

    // Helper methods
    public void markAsSent() {
        this.status = AlertStatus.SENT;
        this.sentAt = OffsetDateTime.now();
    }

    public void acknowledge(Long userId) {
        this.status = AlertStatus.ACKNOWLEDGED;
        this.acknowledgedAt = OffsetDateTime.now();
        this.acknowledgedBy = userId;
    }

    public void resolve(Long userId, String notes) {
        this.status = AlertStatus.RESOLVED;
        this.resolvedAt = OffsetDateTime.now();
        this.resolvedBy = userId;
        this.resolutionNotes = notes;
    }

    public boolean isPending() {
        return status == AlertStatus.PENDING;
    }

    public boolean isResolved() {
        return status == AlertStatus.RESOLVED;
    }
}
