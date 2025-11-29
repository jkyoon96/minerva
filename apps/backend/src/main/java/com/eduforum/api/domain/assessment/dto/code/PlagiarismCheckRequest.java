package com.eduforum.api.domain.assessment.dto.code;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Plagiarism check request")
public class PlagiarismCheckRequest {

    @NotNull
    @Schema(description = "Assignment ID", example = "1")
    private Long assignmentId;

    @Schema(description = "Similarity threshold", example = "70.0")
    @Builder.Default
    private BigDecimal threshold = BigDecimal.valueOf(70);

    @Schema(description = "Algorithm to use", example = "levenshtein")
    @Builder.Default
    private String algorithm = "levenshtein";
}
