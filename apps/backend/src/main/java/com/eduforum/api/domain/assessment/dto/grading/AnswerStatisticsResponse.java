package com.eduforum.api.domain.assessment.dto.grading;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Answer statistics response")
public class AnswerStatisticsResponse {

    @Schema(description = "Statistics ID", example = "1")
    private Long id;

    @Schema(description = "Quiz ID", example = "1")
    private Long quizId;

    @Schema(description = "Question ID", example = "1")
    private Long questionId;

    @Schema(description = "Total responses", example = "50")
    private Integer totalResponses;

    @Schema(description = "Correct responses", example = "35")
    private Integer correctResponses;

    @Schema(description = "Incorrect responses", example = "15")
    private Integer incorrectResponses;

    @Schema(description = "Accuracy rate", example = "70.0")
    private Double accuracyRate;

    @Schema(description = "Option distribution")
    private Map<String, Integer> optionDistribution;

    @Schema(description = "Response time statistics")
    private Map<String, Object> responseTimeStats;
}
