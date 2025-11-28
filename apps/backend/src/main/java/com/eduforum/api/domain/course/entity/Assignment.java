package com.eduforum.api.domain.course.entity;

import com.eduforum.api.domain.common.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Assignment entity (maps to course.assignments table)
 */
@Entity
@Table(schema = "course", name = "assignments")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Assignment extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "course_id", nullable = false)
    private Course course;

    @Column(nullable = false, length = 255)
    private String title;

    @Column(columnDefinition = "TEXT")
    private String description;

    @Column(name = "due_date", nullable = false)
    private OffsetDateTime dueDate;

    @Column(name = "max_score", nullable = false)
    @Builder.Default
    private Integer maxScore = 100;

    @Column(name = "allow_late", nullable = false)
    @Builder.Default
    private Boolean allowLate = true;

    @Column(name = "late_penalty_percent")
    @Builder.Default
    private Integer latePenaltyPercent = 10;

    @Column(name = "max_attempts")
    @Builder.Default
    private Integer maxAttempts = 1;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(columnDefinition = "jsonb")
    @Builder.Default
    private List<Object> attachments = new ArrayList<>();

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, columnDefinition = "assignment_status")
    @Builder.Default
    private AssignmentStatus status = AssignmentStatus.DRAFT;

    @Column(name = "published_at")
    private OffsetDateTime publishedAt;

    // Helper methods
    public void publish() {
        this.status = AssignmentStatus.PUBLISHED;
        this.publishedAt = OffsetDateTime.now();
    }

    public void close() {
        this.status = AssignmentStatus.CLOSED;
    }

    public boolean isPublished() {
        return status == AssignmentStatus.PUBLISHED;
    }

    public boolean isClosed() {
        return status == AssignmentStatus.CLOSED;
    }

    public boolean isPastDue() {
        return OffsetDateTime.now().isAfter(dueDate);
    }
}
