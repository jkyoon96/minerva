package com.eduforum.api.domain.course.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.*;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "과제 제출 요청")
public class AssignmentSubmissionRequest {

    @NotBlank(message = "제출 내용은 필수입니다")
    @Schema(description = "제출 내용")
    private String content;

    @Schema(description = "첨부 파일 목록")
    private List<Object> attachments;
}
