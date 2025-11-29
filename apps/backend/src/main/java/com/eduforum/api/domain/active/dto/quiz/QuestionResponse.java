package com.eduforum.api.domain.active.dto.quiz;

import com.eduforum.api.domain.active.entity.QuestionType;
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
public class QuestionResponse {

    private Long id;
    private Long courseId;
    private Long creatorId;
    private String creatorName;
    private QuestionType type;
    private String questionText;
    private List<String> options;
    private List<String> correctAnswers; // Only shown to instructors
    private String explanation;
    private Integer points;
    private Integer timeLimitSeconds;
    private List<QuestionTagResponse> tags;
    private Map<String, Object> metadata;
    private OffsetDateTime createdAt;
    private OffsetDateTime updatedAt;
}
