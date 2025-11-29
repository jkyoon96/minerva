package com.eduforum.api.domain.assessment.entity;

import com.eduforum.api.domain.common.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.time.OffsetDateTime;
import java.util.Map;

/**
 * Feedback entity
 */
@Entity
@Table(schema = "assessment", name = "feedbacks")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Feedback extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "student_id", nullable = false)
    private Long studentId;

    @Column(name = "course_id", nullable = false)
    private Long courseId;

    @Column(name = "submission_id")
    private Long submissionId;

    @Column(name = "grading_result_id")
    private Long gradingResultId;

    @Enumerated(EnumType.STRING)
    @Column(name = "feedback_type", nullable = false, columnDefinition = "feedback_type")
    private FeedbackType feedbackType;

    @Column(name = "title", nullable = false, length = 200)
    private String title;

    @Column(name = "content", nullable = false, columnDefinition = "TEXT")
    private String content;

    @Column(name = "is_ai_generated", nullable = false)
    @Builder.Default
    private Boolean isAiGenerated = false;

    @Column(name = "generated_by")
    private Long generatedBy;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "metadata", columnDefinition = "jsonb")
    @Builder.Default
    private Map<String, Object> metadata = Map.of();

    @Column(name = "is_read", nullable = false)
    @Builder.Default
    private Boolean isRead = false;

    @Column(name = "read_at")
    private OffsetDateTime readAt;

    @Column(name = "sent_at", nullable = false)
    @Builder.Default
    private OffsetDateTime sentAt = OffsetDateTime.now();

    // Helper methods
    public void markAsRead() {
        this.isRead = true;
        this.readAt = OffsetDateTime.now();
    }

    public boolean isUnread() {
        return !isRead;
    }
}
