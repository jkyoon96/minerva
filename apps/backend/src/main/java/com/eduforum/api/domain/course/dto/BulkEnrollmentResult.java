package com.eduforum.api.domain.course.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * Response DTO for bulk enrollment result
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "일괄 등록 결과")
public class BulkEnrollmentResult {

    @Schema(description = "총 처리 건수")
    private Integer totalProcessed;

    @Schema(description = "성공 건수")
    private Integer successCount;

    @Schema(description = "실패 건수")
    private Integer failureCount;

    @Schema(description = "새로 생성된 사용자 수")
    private Integer newUsersCreated;

    @Schema(description = "기존 사용자 등록 수")
    private Integer existingUsersEnrolled;

    @Schema(description = "성공한 항목 목록")
    private List<EnrollmentItem> successItems;

    @Schema(description = "실패한 항목 목록")
    private List<EnrollmentError> failureItems;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    @Schema(description = "등록 항목")
    public static class EnrollmentItem {
        @Schema(description = "이메일")
        private String email;

        @Schema(description = "이름")
        private String name;

        @Schema(description = "역할")
        private String role;

        @Schema(description = "사용자 ID")
        private Long userId;

        @Schema(description = "임시 비밀번호 (새 사용자인 경우)")
        private String tempPassword;

        @Schema(description = "신규 생성 여부")
        private Boolean isNewUser;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    @Schema(description = "등록 오류")
    public static class EnrollmentError {
        @Schema(description = "행 번호")
        private Integer lineNumber;

        @Schema(description = "이메일")
        private String email;

        @Schema(description = "오류 메시지")
        private String errorMessage;
    }
}
