package com.eduforum.api.domain.assessment.dto.peer;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Peer review assignment response")
public class PeerReviewAssignmentResponse {

    @Schema(description = "Assignment ID", example = "1")
    private Long assignmentId;

    @Schema(description = "Submission ID to review", example = "2")
    private Long submissionId;

    @Schema(description = "Submission number", example = "1")
    private Integer submissionNumber;

    @Schema(description = "Total assignments", example = "3")
    private Integer totalAssignments;

    @Schema(description = "Is completed", example = "false")
    private Boolean isCompleted;
}
