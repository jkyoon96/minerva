package com.eduforum.api.domain.course.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * Response DTO for enrollment preview
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "등록 미리보기")
public class EnrollmentPreview {

    @Schema(description = "총 항목 수")
    private Integer totalItems;

    @Schema(description = "유효한 항목 수")
    private Integer validItems;

    @Schema(description = "무효한 항목 수")
    private Integer invalidItems;

    @Schema(description = "새로 생성될 사용자 수")
    private Integer newUsers;

    @Schema(description = "기존 사용자 수")
    private Integer existingUsers;

    @Schema(description = "미리보기 항목 목록")
    private List<PreviewItem> items;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    @Schema(description = "미리보기 항목")
    public static class PreviewItem {
        @Schema(description = "행 번호")
        private Integer lineNumber;

        @Schema(description = "이메일")
        private String email;

        @Schema(description = "이름")
        private String firstName;

        @Schema(description = "성")
        private String lastName;

        @Schema(description = "역할")
        private String role;

        @Schema(description = "유효성 여부")
        private Boolean isValid;

        @Schema(description = "신규 사용자 여부")
        private Boolean isNewUser;

        @Schema(description = "오류 메시지")
        private String errorMessage;
    }
}
