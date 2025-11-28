package com.eduforum.api.domain.course.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.*;
import lombok.*;

import java.time.OffsetDateTime;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "과제 생성 요청")
public class AssignmentCreateRequest {

    @NotBlank(message = "과제 제목은 필수입니다")
    @Size(max = 255, message = "과제 제목은 255자 이하여야 합니다")
    @Schema(description = "과제 제목", example = "Homework 1: Variables and Data Types")
    private String title;

    @Schema(description = "과제 설명")
    private String description;

    @NotNull(message = "마감일은 필수입니다")
    @Future(message = "마감일은 미래여야 합니다")
    @Schema(description = "마감일", example = "2024-12-15T23:59:59Z")
    private OffsetDateTime dueDate;

    @Min(value = 1, message = "최대 점수는 1점 이상이어야 합니다")
    @Max(value = 1000, message = "최대 점수는 1000점 이하여야 합니다")
    @Schema(description = "최대 점수", example = "100")
    @Builder.Default
    private Integer maxScore = 100;

    @Schema(description = "지각 제출 허용 여부", example = "true")
    @Builder.Default
    private Boolean allowLate = true;

    @Min(value = 0, message = "지각 페널티는 0 이상이어야 합니다")
    @Max(value = 100, message = "지각 페널티는 100 이하여야 합니다")
    @Schema(description = "지각 페널티 (%)", example = "10")
    @Builder.Default
    private Integer latePenaltyPercent = 10;

    @Min(value = 1, message = "최대 제출 횟수는 1회 이상이어야 합니다")
    @Schema(description = "최대 제출 횟수", example = "1")
    @Builder.Default
    private Integer maxAttempts = 1;

    @Schema(description = "첨부 파일 목록")
    private List<Object> attachments;
}
