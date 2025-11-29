package com.eduforum.api.domain.assessment.entity;

import com.eduforum.api.domain.common.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.time.OffsetDateTime;
import java.util.Map;

/**
 * Peer review assignment configuration entity
 */
@Entity
@Table(schema = "assessment", name = "peer_review_assignments")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PeerReviewAssignment extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "assignment_id", nullable = false, unique = true)
    private Long assignmentId;

    @Column(name = "reviews_per_submission", nullable = false)
    @Builder.Default
    private Integer reviewsPerSubmission = 3;

    @Column(name = "is_anonymous", nullable = false)
    @Builder.Default
    private Boolean isAnonymous = true;

    @Column(name = "is_auto_assigned", nullable = false)
    @Builder.Default
    private Boolean isAutoAssigned = true;

    @Column(name = "review_deadline")
    private OffsetDateTime reviewDeadline;

    @Column(name = "min_score")
    private Integer minScore;

    @Column(name = "max_score")
    private Integer maxScore;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "rubric", columnDefinition = "jsonb")
    private Map<String, Object> rubric;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "settings", columnDefinition = "jsonb")
    @Builder.Default
    private Map<String, Object> settings = Map.of();

    @Column(name = "remove_outliers", nullable = false)
    @Builder.Default
    private Boolean removeOutliers = true;

    @Column(name = "is_active", nullable = false)
    @Builder.Default
    private Boolean isActive = false;

    // Helper methods
    public void activate() {
        this.isActive = true;
    }

    public void deactivate() {
        this.isActive = false;
    }

    public boolean isDeadlinePassed() {
        return reviewDeadline != null && reviewDeadline.isBefore(OffsetDateTime.now());
    }
}
