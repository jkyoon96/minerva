package com.eduforum.api.domain.course.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

import java.time.OffsetDateTime;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "과제 응답")
public class AssignmentResponse {

    @Schema(description = "과제 ID", example = "1")
    private Long id;

    @Schema(description = "코스 ID", example = "1")
    private Long courseId;

    @Schema(description = "과제 제목", example = "Homework 1: Variables and Data Types")
    private String title;

    @Schema(description = "과제 설명")
    private String description;

    @Schema(description = "마감일")
    private OffsetDateTime dueDate;

    @Schema(description = "최대 점수", example = "100")
    private Integer maxScore;

    @Schema(description = "지각 제출 허용 여부", example = "true")
    private Boolean allowLate;

    @Schema(description = "지각 페널티 (%)", example = "10")
    private Integer latePenaltyPercent;

    @Schema(description = "최대 제출 횟수", example = "1")
    private Integer maxAttempts;

    @Schema(description = "첨부 파일 목록")
    private List<Object> attachments;

    @Schema(description = "과제 상태", example = "PUBLISHED")
    private String status;

    @Schema(description = "게시일시")
    private OffsetDateTime publishedAt;

    @Schema(description = "생성일시")
    private OffsetDateTime createdAt;

    @Schema(description = "수정일시")
    private OffsetDateTime updatedAt;

    // For student view
    @Schema(description = "제출 정보 (학생용)")
    private SubmissionInfo submission;

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    @Schema(description = "제출 정보")
    public static class SubmissionInfo {
        @Schema(description = "제출 여부", example = "true")
        private Boolean isSubmitted;

        @Schema(description = "제출일시")
        private OffsetDateTime submittedAt;

        @Schema(description = "점수", example = "95")
        private Integer score;

        @Schema(description = "지각 여부", example = "false")
        private Boolean isLate;
    }
}
