package com.eduforum.api.domain.course.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

import java.time.OffsetDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "수강 등록 응답")
public class EnrollmentResponse {

    @Schema(description = "수강 등록 ID", example = "1")
    private Long id;

    @Schema(description = "코스 정보")
    private CourseInfo course;

    @Schema(description = "학생 정보")
    private StudentInfo student;

    @Schema(description = "역할", example = "STUDENT")
    private String role;

    @Schema(description = "상태", example = "ACTIVE")
    private String status;

    @Schema(description = "가입일시")
    private OffsetDateTime joinedAt;

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    @Schema(description = "코스 정보")
    public static class CourseInfo {
        @Schema(description = "코스 ID", example = "1")
        private Long id;

        @Schema(description = "코스 코드", example = "CS101")
        private String code;

        @Schema(description = "코스 제목", example = "Introduction to Computer Science")
        private String title;

        @Schema(description = "학기", example = "Fall")
        private String semester;

        @Schema(description = "연도", example = "2024")
        private Integer year;

        @Schema(description = "썸네일 URL")
        private String thumbnailUrl;
    }

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    @Schema(description = "학생 정보")
    public static class StudentInfo {
        @Schema(description = "사용자 ID", example = "2")
        private Long id;

        @Schema(description = "이메일", example = "student@minerva.edu")
        private String email;

        @Schema(description = "이름", example = "홍길동")
        private String name;

        @Schema(description = "프로필 이미지 URL")
        private String profileImageUrl;
    }
}
