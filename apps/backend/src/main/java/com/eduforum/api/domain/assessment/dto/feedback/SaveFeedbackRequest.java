package com.eduforum.api.domain.assessment.dto.feedback;

import com.eduforum.api.domain.assessment.entity.FeedbackType;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Save custom feedback request")
public class SaveFeedbackRequest {

    @NotNull
    @Schema(description = "Student ID", example = "1")
    private Long studentId;

    @NotNull
    @Schema(description = "Course ID", example = "1")
    private Long courseId;

    @Schema(description = "Submission ID", example = "1")
    private Long submissionId;

    @NotNull
    @Schema(description = "Feedback type")
    private FeedbackType feedbackType;

    @NotBlank
    @Schema(description = "Title", example = "Great progress!")
    private String title;

    @NotBlank
    @Schema(description = "Content")
    private String content;
}
