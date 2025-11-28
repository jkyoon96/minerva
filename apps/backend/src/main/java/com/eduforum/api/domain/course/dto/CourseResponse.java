package com.eduforum.api.domain.course.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

import java.time.OffsetDateTime;
import java.util.Map;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "코스 응답")
public class CourseResponse {

    @Schema(description = "코스 ID", example = "1")
    private Long id;

    @Schema(description = "코스 코드", example = "CS101")
    private String code;

    @Schema(description = "코스 제목", example = "Introduction to Computer Science")
    private String title;

    @Schema(description = "코스 설명")
    private String description;

    @Schema(description = "학기", example = "Fall")
    private String semester;

    @Schema(description = "연도", example = "2024")
    private Integer year;

    @Schema(description = "썸네일 URL")
    private String thumbnailUrl;

    @Schema(description = "초대 코드")
    private String inviteCode;

    @Schema(description = "초대 코드 만료일")
    private OffsetDateTime inviteExpiresAt;

    @Schema(description = "최대 학생 수", example = "50")
    private Integer maxStudents;

    @Schema(description = "현재 학생 수", example = "30")
    private Integer currentStudents;

    @Schema(description = "공개 여부", example = "true")
    private Boolean isPublished;

    @Schema(description = "코스 설정")
    private Map<String, Object> settings;

    @Schema(description = "교수 정보")
    private ProfessorInfo professor;

    @Schema(description = "생성일시")
    private OffsetDateTime createdAt;

    @Schema(description = "수정일시")
    private OffsetDateTime updatedAt;

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    @Schema(description = "교수 정보")
    public static class ProfessorInfo {
        @Schema(description = "사용자 ID", example = "1")
        private Long id;

        @Schema(description = "이메일", example = "professor@minerva.edu")
        private String email;

        @Schema(description = "이름", example = "김교수")
        private String name;

        @Schema(description = "프로필 이미지 URL")
        private String profileImageUrl;
    }
}
