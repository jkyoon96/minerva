package com.eduforum.api.common.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * 유효성 검증 에러 응답
 */
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "유효성 검증 에러 응답")
public class ValidationErrorResponse {

    @Schema(description = "성공 여부", example = "false")
    @Builder.Default
    private Boolean success = false;

    @Schema(description = "에러 메시지", example = "입력 값이 올바르지 않습니다")
    private String message;

    @Schema(description = "에러 코드", example = "VALIDATION_ERROR")
    @Builder.Default
    private String errorCode = "VALIDATION_ERROR";

    @Schema(description = "필드별 에러 목록")
    @Builder.Default
    private List<FieldError> errors = new ArrayList<>();

    @Schema(description = "응답 시간", example = "2025-11-29T10:30:00")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss")
    @Builder.Default
    private LocalDateTime timestamp = LocalDateTime.now();

    /**
     * 필드 에러 정보
     */
    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    @Schema(description = "필드 에러 정보")
    public static class FieldError {

        @Schema(description = "필드명", example = "email")
        private String field;

        @Schema(description = "에러 메시지", example = "이메일 형식이 올바르지 않습니다")
        private String message;

        @Schema(description = "거부된 값", example = "invalid-email")
        private Object rejectedValue;
    }

    /**
     * 유효성 검증 에러 응답 생성
     */
    public static ValidationErrorResponse of(String message, List<FieldError> errors) {
        return ValidationErrorResponse.builder()
            .success(false)
            .message(message)
            .errorCode("VALIDATION_ERROR")
            .errors(errors)
            .build();
    }

    /**
     * 필드 에러 추가
     */
    public void addFieldError(String field, String message, Object rejectedValue) {
        this.errors.add(FieldError.builder()
            .field(field)
            .message(message)
            .rejectedValue(rejectedValue)
            .build());
    }
}
