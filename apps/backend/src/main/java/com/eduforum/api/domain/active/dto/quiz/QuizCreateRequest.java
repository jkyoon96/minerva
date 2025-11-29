package com.eduforum.api.domain.active.dto.quiz;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
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
public class QuizCreateRequest {

    @NotNull(message = "Course ID is required")
    private Long courseId;

    @NotBlank(message = "Title is required")
    private String title;

    private String description;

    @NotEmpty(message = "At least one question is required")
    private List<Long> questionIds;

    private Integer timeLimitMinutes;
    private Integer passingScore;
    private Boolean shuffleQuestions;
    private Boolean showCorrectAnswers;
    private Map<String, Object> settings;
}
