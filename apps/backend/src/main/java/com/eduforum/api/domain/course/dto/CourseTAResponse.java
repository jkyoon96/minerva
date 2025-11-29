package com.eduforum.api.domain.course.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.OffsetDateTime;

/**
 * Response DTO for course TA
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "코스 TA 정보")
public class CourseTAResponse {

    @Schema(description = "TA 배정 ID")
    private Long id;

    @Schema(description = "코스 ID")
    private Long courseId;

    @Schema(description = "TA 사용자 정보")
    private TAUserInfo taUser;

    @Schema(description = "배정한 교수 정보")
    private AssignedByInfo assignedBy;

    @Schema(description = "배정 시간")
    private OffsetDateTime assignedAt;

    @Schema(description = "TA 권한")
    private TAPermissions permissions;

    @Schema(description = "생성 시간")
    private OffsetDateTime createdAt;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    @Schema(description = "TA 사용자 정보")
    public static class TAUserInfo {
        @Schema(description = "사용자 ID")
        private Long id;

        @Schema(description = "이메일")
        private String email;

        @Schema(description = "이름")
        private String name;

        @Schema(description = "프로필 이미지 URL")
        private String profileImageUrl;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    @Schema(description = "배정한 교수 정보")
    public static class AssignedByInfo {
        @Schema(description = "교수 ID")
        private Long id;

        @Schema(description = "이메일")
        private String email;

        @Schema(description = "이름")
        private String name;
    }
}
