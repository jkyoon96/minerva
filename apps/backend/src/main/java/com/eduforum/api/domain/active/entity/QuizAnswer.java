package com.eduforum.api.domain.active.entity;

import com.eduforum.api.domain.auth.entity.User;
import com.eduforum.api.domain.common.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.util.Map;

/**
 * Quiz answer entity - represents a student's answers to a quiz
 */
@Entity
@Table(schema = "active", name = "quiz_answers")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class QuizAnswer extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "session_id", nullable = false)
    private QuizSession session;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(nullable = false, columnDefinition = "jsonb")
    private Map<Long, Object> answers; // Map of questionId -> answer

    @Column(name = "score")
    private Integer score;

    @Column(name = "max_score")
    private Integer maxScore;

    @Column(name = "auto_graded")
    @Builder.Default
    private Boolean autoGraded = false;

    @Column(name = "submitted_at")
    private java.time.OffsetDateTime submittedAt;

    // Helper methods
    public void calculateScore(int score, int maxScore) {
        this.score = score;
        this.maxScore = maxScore;
    }

    public Double getPercentage() {
        if (maxScore == null || maxScore == 0) return null;
        return (score * 100.0) / maxScore;
    }
}
