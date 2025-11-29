package com.eduforum.api.domain.active.dto.quiz;

import com.eduforum.api.domain.active.entity.QuestionType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Map;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class QuestionCreateRequest {

    @NotNull(message = "Course ID is required")
    private Long courseId;

    @NotNull(message = "Question type is required")
    private QuestionType type;

    @NotBlank(message = "Question text is required")
    private String questionText;

    private List<String> options;
    private List<String> correctAnswers;
    private String explanation;
    private Integer points;
    private Integer timeLimitSeconds;
    private List<Long> tagIds;
    private Map<String, Object> metadata;
}
