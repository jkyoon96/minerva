package com.eduforum.api.domain.assessment.dto.code;

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
@Schema(description = "Code submission request")
public class CodeSubmitRequest {

    @NotNull
    @Schema(description = "Assignment ID", example = "1")
    private Long assignmentId;

    @NotBlank
    @Schema(description = "Programming language", example = "python")
    private String language;

    @NotBlank
    @Schema(description = "Source code")
    private String code;

    @Schema(description = "Auto-run after submission", example = "true")
    @Builder.Default
    private Boolean autoRun = false;
}
