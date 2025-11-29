package com.eduforum.api.domain.assessment.dto.code;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.Map;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Plagiarism report response")
public class PlagiarismReportResponse {

    @Schema(description = "Report ID", example = "1")
    private Long id;

    @Schema(description = "Assignment ID", example = "1")
    private Long assignmentId;

    @Schema(description = "Submission ID 1", example = "1")
    private Long submissionId1;

    @Schema(description = "Submission ID 2", example = "2")
    private Long submissionId2;

    @Schema(description = "Student ID 1", example = "1")
    private Long studentId1;

    @Schema(description = "Student ID 2", example = "2")
    private Long studentId2;

    @Schema(description = "Similarity score", example = "85.5")
    private BigDecimal similarityScore;

    @Schema(description = "Algorithm used", example = "levenshtein")
    private String algorithm;

    @Schema(description = "Matched segments")
    private Map<String, Object> matchedSegments;

    @Schema(description = "Is flagged", example = "true")
    private Boolean isFlagged;

    @Schema(description = "Reviewed by", example = "3")
    private Long reviewedBy;

    @Schema(description = "Reviewed at")
    private OffsetDateTime reviewedAt;

    @Schema(description = "Review notes")
    private String reviewNotes;

    @Schema(description = "Checked at")
    private OffsetDateTime checkedAt;
}
