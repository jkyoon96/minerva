package com.eduforum.api.common.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * 표준 API 에러 응답
 */
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "표준 API 에러 응답")
public class ApiErrorResponse {

    @Schema(description = "성공 여부", example = "false")
    @Builder.Default
    private Boolean success = false;

    @Schema(description = "에러 메시지", example = "요청 처리 중 오류가 발생했습니다")
    private String message;

    @Schema(description = "에러 코드", example = "COMMON_001")
    private String errorCode;

    @Schema(description = "응답 시간", example = "2025-11-29T10:30:00")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss")
    @Builder.Default
    private LocalDateTime timestamp = LocalDateTime.now();

    /**
     * 에러 응답 생성
     */
    public static ApiErrorResponse of(String message, String errorCode) {
        return ApiErrorResponse.builder()
            .success(false)
            .message(message)
            .errorCode(errorCode)
            .build();
    }
}
