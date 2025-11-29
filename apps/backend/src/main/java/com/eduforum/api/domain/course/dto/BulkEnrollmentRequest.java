package com.eduforum.api.domain.course.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Request DTO for bulk enrollment
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "일괄 등록 요청")
public class BulkEnrollmentRequest {

    @NotBlank(message = "CSV 내용은 필수입니다")
    @Schema(description = "CSV 형식의 사용자 데이터 (email,firstName,lastName,role)",
            example = "john@example.com,John,Doe,STUDENT\njane@example.com,Jane,Smith,STUDENT")
    private String csvContent;

    @Schema(description = "기존 사용자 건너뛰기 (true: 기존 사용자는 등록만, false: 오류 발생)", example = "true")
    @Builder.Default
    private Boolean skipExisting = true;

    @Schema(description = "이메일 발송 여부", example = "true")
    @Builder.Default
    private Boolean sendEmail = true;
}
