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
 * Plagiarism report entity
 */
@Entity
@Table(schema = "assessment", name = "plagiarism_reports")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PlagiarismReport extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "assignment_id", nullable = false)
    private Long assignmentId;

    @Column(name = "submission_id_1", nullable = false)
    private Long submissionId1;

    @Column(name = "submission_id_2", nullable = false)
    private Long submissionId2;

    @Column(name = "student_id_1", nullable = false)
    private Long studentId1;

    @Column(name = "student_id_2", nullable = false)
    private Long studentId2;

    @Column(name = "similarity_score", nullable = false, precision = 5, scale = 2)
    private BigDecimal similarityScore;

    @Column(name = "algorithm", length = 50)
    private String algorithm;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "matched_segments", columnDefinition = "jsonb")
    private Map<String, Object> matchedSegments;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "analysis_details", columnDefinition = "jsonb")
    private Map<String, Object> analysisDetails;

    @Column(name = "is_flagged", nullable = false)
    @Builder.Default
    private Boolean isFlagged = false;

    @Column(name = "reviewed_by")
    private Long reviewedBy;

    @Column(name = "reviewed_at")
    private OffsetDateTime reviewedAt;

    @Column(name = "review_notes", columnDefinition = "TEXT")
    private String reviewNotes;

    @Column(name = "checked_at", nullable = false)
    @Builder.Default
    private OffsetDateTime checkedAt = OffsetDateTime.now();

    // Helper methods
    public void flag() {
        this.isFlagged = true;
    }

    public void unflag() {
        this.isFlagged = false;
    }

    public void markAsReviewed(Long reviewerId, String notes) {
        this.reviewedBy = reviewerId;
        this.reviewedAt = OffsetDateTime.now();
        this.reviewNotes = notes;
    }

    public boolean isSuspicious() {
        return similarityScore.compareTo(BigDecimal.valueOf(70)) > 0;
    }
}
