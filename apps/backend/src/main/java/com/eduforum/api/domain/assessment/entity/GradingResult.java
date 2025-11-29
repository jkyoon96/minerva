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
 * Grading result entity
 */
@Entity
@Table(schema = "assessment", name = "grading_results")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class GradingResult extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "assignment_id", nullable = false)
    private Long assignmentId;

    @Column(name = "submission_id")
    private Long submissionId;

    @Column(name = "student_id", nullable = false)
    private Long studentId;

    @Column(name = "grader_id")
    private Long graderId;

    @Enumerated(EnumType.STRING)
    @Column(name = "grading_type", nullable = false, columnDefinition = "grading_type")
    private GradingType gradingType;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, columnDefinition = "grading_status")
    @Builder.Default
    private GradingStatus status = GradingStatus.PENDING;

    @Column(precision = 5, scale = 2)
    private BigDecimal score;

    @Column(name = "max_score", precision = 5, scale = 2)
    private BigDecimal maxScore;

    @Column(name = "ai_confidence", precision = 5, scale = 2)
    private BigDecimal aiConfidence;

    @Column(columnDefinition = "TEXT")
    private String feedback;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "grading_details", columnDefinition = "jsonb")
    @Builder.Default
    private Map<String, Object> gradingDetails = Map.of();

    @Column(name = "graded_at")
    private OffsetDateTime gradedAt;

    @Column(name = "reviewed_at")
    private OffsetDateTime reviewedAt;

    // Helper methods
    public void markAsGraded() {
        this.status = GradingStatus.GRADED;
        this.gradedAt = OffsetDateTime.now();
    }

    public void markAsReviewed(Long reviewerId) {
        this.status = GradingStatus.REVIEWED;
        this.graderId = reviewerId;
        this.reviewedAt = OffsetDateTime.now();
    }

    public void finalize() {
        this.status = GradingStatus.FINALIZED;
    }

    public boolean isPending() {
        return status == GradingStatus.PENDING;
    }

    public boolean isGraded() {
        return status == GradingStatus.GRADED;
    }
}
