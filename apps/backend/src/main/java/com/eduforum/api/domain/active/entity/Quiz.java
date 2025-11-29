package com.eduforum.api.domain.active.entity;

import com.eduforum.api.domain.auth.entity.User;
import com.eduforum.api.domain.common.entity.BaseEntity;
import com.eduforum.api.domain.course.entity.Course;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Quiz entity - represents a quiz/assessment
 */
@Entity
@Table(schema = "active", name = "quizzes")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Quiz extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "course_id", nullable = false)
    private Course course;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "creator_id", nullable = false)
    private User creator;

    @Column(nullable = false, length = 200)
    private String title;

    @Column(columnDefinition = "TEXT")
    private String description;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, columnDefinition = "quiz_status")
    @Builder.Default
    private QuizStatus status = QuizStatus.DRAFT;

    @Column(name = "time_limit_minutes")
    private Integer timeLimitMinutes;

    @Column(name = "passing_score")
    private Integer passingScore;

    @Column(name = "shuffle_questions")
    @Builder.Default
    private Boolean shuffleQuestions = false;

    @Column(name = "show_correct_answers")
    @Builder.Default
    private Boolean showCorrectAnswers = true;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "question_ids", columnDefinition = "jsonb", nullable = false)
    @Builder.Default
    private List<Long> questionIds = new ArrayList<>();

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(columnDefinition = "jsonb")
    @Builder.Default
    private Map<String, Object> settings = Map.of();

    // Helper methods
    public void activate() {
        this.status = QuizStatus.ACTIVE;
    }

    public void complete() {
        this.status = QuizStatus.COMPLETED;
    }

    public boolean isActive() {
        return status == QuizStatus.ACTIVE;
    }

    public boolean isCompleted() {
        return status == QuizStatus.COMPLETED;
    }
}
