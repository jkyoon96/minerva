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
@Schema(description = "과제 제출 응답")
public class AssignmentSubmissionResponse {

    @Schema(description = "제출 ID", example = "1")
    private Long id;

    @Schema(description = "과제 ID", example = "1")
    private Long assignmentId;

    @Schema(description = "학생 ID", example = "2")
    private Long studentId;

    @Schema(description = "학생 이름", example = "홍길동")
    private String studentName;

    @Schema(description = "시도 번호", example = "1")
    private Integer attemptNumber;

    @Schema(description = "제출 내용")
    private String content;

    @Schema(description = "첨부 파일 목록")
    private List<Object> attachments;

    @Schema(description = "제출일시")
    private OffsetDateTime submittedAt;

    @Schema(description = "점수", example = "95")
    private Integer score;

    @Schema(description = "피드백")
    private String feedback;

    @Schema(description = "채점자 이름", example = "김교수")
    private String gradedByName;

    @Schema(description = "채점일시")
    private OffsetDateTime gradedAt;

    @Schema(description = "지각 여부", example = "false")
    private Boolean isLate;
}
