package com.eduforum.api.domain.assessment.entity;

import com.eduforum.api.domain.common.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.util.Map;

/**
 * Answer statistics entity for multiple choice questions
 */
@Entity
@Table(schema = "assessment", name = "answer_statistics")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AnswerStatistics extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "quiz_id", nullable = false)
    private Long quizId;

    @Column(name = "question_id", nullable = false)
    private Long questionId;

    @Column(name = "total_responses", nullable = false)
    @Builder.Default
    private Integer totalResponses = 0;

    @Column(name = "correct_responses", nullable = false)
    @Builder.Default
    private Integer correctResponses = 0;

    @Column(name = "incorrect_responses", nullable = false)
    @Builder.Default
    private Integer incorrectResponses = 0;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "option_distribution", columnDefinition = "jsonb")
    @Builder.Default
    private Map<String, Integer> optionDistribution = Map.of();

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "response_time_avg", columnDefinition = "jsonb")
    private Map<String, Object> responseTimeStats;

    // Helper methods
    public void incrementTotal() {
        this.totalResponses++;
    }

    public void incrementCorrect() {
        this.correctResponses++;
        incrementTotal();
    }

    public void incrementIncorrect() {
        this.incorrectResponses++;
        incrementTotal();
    }

    public double getAccuracyRate() {
        if (totalResponses == 0) return 0.0;
        return (double) correctResponses / totalResponses * 100;
    }
}
