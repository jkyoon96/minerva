package com.eduforum.api.domain.active.dto.quiz;

import com.eduforum.api.domain.active.entity.QuizStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.Map;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class QuizResponse {

    private Long id;
    private Long courseId;
    private Long creatorId;
    private String creatorName;
    private String title;
    private String description;
    private QuizStatus status;
    private Integer timeLimitMinutes;
    private Integer passingScore;
    private Boolean shuffleQuestions;
    private Boolean showCorrectAnswers;
    private List<Long> questionIds;
    private Long sessionCount;
    private Map<String, Object> settings;
    private OffsetDateTime createdAt;
    private OffsetDateTime updatedAt;
}
