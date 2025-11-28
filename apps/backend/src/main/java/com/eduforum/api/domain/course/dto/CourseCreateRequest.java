package com.eduforum.api.domain.course.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.*;
import lombok.*;

import java.util.Map;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "코스 생성 요청")
public class CourseCreateRequest {

    @NotBlank(message = "코스 코드는 필수입니다")
    @Size(max = 50, message = "코스 코드는 50자 이하여야 합니다")
    @Schema(description = "코스 코드", example = "CS101")
    private String code;

    @NotBlank(message = "코스 제목은 필수입니다")
    @Size(max = 255, message = "코스 제목은 255자 이하여야 합니다")
    @Schema(description = "코스 제목", example = "Introduction to Computer Science")
    private String title;

    @Schema(description = "코스 설명", example = "기초 컴퓨터 과학 강의입니다")
    private String description;

    @NotBlank(message = "학기는 필수입니다")
    @Pattern(regexp = "^(Spring|Summer|Fall|Winter)$", message = "학기는 Spring, Summer, Fall, Winter 중 하나여야 합니다")
    @Schema(description = "학기", example = "Fall")
    private String semester;

    @NotNull(message = "연도는 필수입니다")
    @Min(value = 2020, message = "연도는 2020 이상이어야 합니다")
    @Max(value = 2100, message = "연도는 2100 이하여야 합니다")
    @Schema(description = "연도", example = "2024")
    private Integer year;

    @Schema(description = "썸네일 URL")
    private String thumbnailUrl;

    @Min(value = 1, message = "최대 학생 수는 1명 이상이어야 합니다")
    @Max(value = 500, message = "최대 학생 수는 500명 이하여야 합니다")
    @Schema(description = "최대 학생 수", example = "50")
    @Builder.Default
    private Integer maxStudents = 50;

    @Schema(description = "코스 설정 (성적 비율 등)")
    private Map<String, Object> settings;
}
