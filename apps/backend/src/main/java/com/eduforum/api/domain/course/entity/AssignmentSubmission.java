package com.eduforum.api.domain.course.entity;

import com.eduforum.api.domain.auth.entity.User;
import com.eduforum.api.domain.common.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * AssignmentSubmission entity (maps to course.assignment_submissions table)
 */
@Entity
@Table(schema = "course", name = "assignment_submissions",
    uniqueConstraints = @UniqueConstraint(columnNames = {"assignment_id", "student_id", "attempt_number"}))
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AssignmentSubmission extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "assignment_id", nullable = false)
    private Assignment assignment;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "student_id", nullable = false)
    private User student;

    @Column(name = "attempt_number", nullable = false)
    @Builder.Default
    private Integer attemptNumber = 1;

    @Column(columnDefinition = "TEXT")
    private String content;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(columnDefinition = "jsonb")
    @Builder.Default
    private List<Object> attachments = new ArrayList<>();

    @Column(name = "submitted_at", nullable = false)
    private OffsetDateTime submittedAt;

    @Column
    private Integer score;

    @Column(columnDefinition = "TEXT")
    private String feedback;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "graded_by")
    private User gradedBy;

    @Column(name = "graded_at")
    private OffsetDateTime gradedAt;

    @Column(name = "is_late", nullable = false)
    @Builder.Default
    private Boolean isLate = false;

    @PrePersist
    protected void onCreate() {
        if (submittedAt == null) {
            submittedAt = OffsetDateTime.now();
        }
    }

    // Helper methods
    public void grade(Integer score, String feedback, User gradedBy) {
        this.score = score;
        this.feedback = feedback;
        this.gradedBy = gradedBy;
        this.gradedAt = OffsetDateTime.now();
    }

    public boolean isGraded() {
        return gradedAt != null && score != null;
    }

    public boolean isLateSubmission(OffsetDateTime dueDate) {
        return submittedAt.isAfter(dueDate);
    }
}
