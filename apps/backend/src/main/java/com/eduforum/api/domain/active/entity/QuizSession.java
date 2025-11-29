package com.eduforum.api.domain.active.entity;

import com.eduforum.api.domain.common.entity.BaseEntity;
import com.eduforum.api.domain.seminar.entity.SeminarRoom;
import jakarta.persistence.*;
import lombok.*;

import java.time.OffsetDateTime;

/**
 * Quiz session entity - represents an active quiz session in a seminar
 */
@Entity
@Table(schema = "active", name = "quiz_sessions")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class QuizSession extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "quiz_id", nullable = false)
    private Quiz quiz;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private com.eduforum.api.domain.auth.entity.User user;

    @Column(name = "started_at")
    private OffsetDateTime startedAt;

    @Column(name = "ends_at")
    private OffsetDateTime endsAt;

    @Column(name = "submitted_at")
    private OffsetDateTime submittedAt;

    @Column(name = "total_score")
    @Builder.Default
    private Integer totalScore = 0;

    // Helper methods
    public void start() {
        this.startedAt = OffsetDateTime.now();
        if (quiz.getTimeLimitMinutes() != null) {
            this.endsAt = startedAt.plusMinutes(quiz.getTimeLimitMinutes());
        }
    }

    public boolean isActive() {
        if (startedAt == null) return false;
        if (endsAt == null) return true;
        return OffsetDateTime.now().isBefore(endsAt);
    }

    public void calculateScore() {
        // Score will be calculated from QuizAnswer entities
        // This is just a placeholder
    }
}
