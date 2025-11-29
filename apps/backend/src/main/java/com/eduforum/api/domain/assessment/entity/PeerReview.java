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
 * Peer review entity
 */
@Entity
@Table(schema = "assessment", name = "peer_reviews")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PeerReview extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "assignment_id", nullable = false)
    private Long assignmentId;

    @Column(name = "submission_id", nullable = false)
    private Long submissionId;

    @Column(name = "reviewer_id", nullable = false)
    private Long reviewerId;

    @Column(name = "reviewee_id", nullable = false)
    private Long revieweeId;

    @Column(name = "score", precision = 5, scale = 2)
    private BigDecimal score;

    @Column(name = "max_score", precision = 5, scale = 2)
    private BigDecimal maxScore;

    @Column(name = "comments", columnDefinition = "TEXT")
    private String comments;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "rubric_scores", columnDefinition = "jsonb")
    @Builder.Default
    private Map<String, Object> rubricScores = Map.of();

    @Column(name = "is_submitted", nullable = false)
    @Builder.Default
    private Boolean isSubmitted = false;

    @Column(name = "submitted_at")
    private OffsetDateTime submittedAt;

    @Column(name = "is_outlier", nullable = false)
    @Builder.Default
    private Boolean isOutlier = false;

    @Column(name = "is_anonymous", nullable = false)
    @Builder.Default
    private Boolean isAnonymous = true;

    // Helper methods
    public void submit() {
        this.isSubmitted = true;
        this.submittedAt = OffsetDateTime.now();
    }

    public void markAsOutlier() {
        this.isOutlier = true;
    }

    public boolean isPending() {
        return !isSubmitted;
    }
}
