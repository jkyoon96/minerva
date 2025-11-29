package com.eduforum.api.domain.course.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * TA permissions DTO
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "TA 권한 설정")
public class TAPermissions {

    @Schema(description = "채점 권한", example = "true")
    @Builder.Default
    private Boolean canGrade = true;

    @Schema(description = "학생 관리 권한", example = "true")
    @Builder.Default
    private Boolean canManageStudents = true;

    @Schema(description = "세션 관리 권한", example = "false")
    @Builder.Default
    private Boolean canManageSessions = false;

    @Schema(description = "과제 관리 권한", example = "true")
    @Builder.Default
    private Boolean canManageAssignments = true;

    @Schema(description = "분석 조회 권한", example = "true")
    @Builder.Default
    private Boolean canViewAnalytics = true;

    @Schema(description = "토론 중재 권한", example = "true")
    @Builder.Default
    private Boolean canModerateDiscussions = true;
}
