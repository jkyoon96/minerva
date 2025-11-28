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
@Schema(description = "코스 수정 요청")
public class CourseUpdateRequest {

    @Size(max = 255, message = "코스 제목은 255자 이하여야 합니다")
    @Schema(description = "코스 제목", example = "Introduction to Computer Science")
    private String title;

    @Schema(description = "코스 설명", example = "기초 컴퓨터 과학 강의입니다")
    private String description;

    @Schema(description = "썸네일 URL")
    private String thumbnailUrl;

    @Min(value = 1, message = "최대 학생 수는 1명 이상이어야 합니다")
    @Max(value = 500, message = "최대 학생 수는 500명 이하여야 합니다")
    @Schema(description = "최대 학생 수", example = "50")
    private Integer maxStudents;

    @Schema(description = "코스 설정 (성적 비율 등)")
    private Map<String, Object> settings;

    @Schema(description = "코스 공개 여부", example = "true")
    private Boolean isPublished;
}
