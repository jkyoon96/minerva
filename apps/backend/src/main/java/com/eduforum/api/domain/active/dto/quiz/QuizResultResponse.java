package com.eduforum.api.domain.active.dto.quiz;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.OffsetDateTime;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class QuizResultResponse {

    private Long quizId;
    private String title;
    private Integer totalScore;
    private Integer maxScore;
    private Double percentage;
    private Boolean passed;
    private OffsetDateTime startedAt;
    private OffsetDateTime submittedAt;
    private List<QuestionResult> questionResults;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class QuestionResult {
        private Long questionId;
        private String questionText;
        private List<String> userAnswers;
        private List<String> correctAnswers;
        private Boolean isCorrect;
        private Integer pointsEarned;
        private Integer pointsPossible;
        private String explanation;
    }
}
