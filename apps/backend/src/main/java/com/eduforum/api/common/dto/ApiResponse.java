package com.eduforum.api.common.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.Map;

/**
 * 공통 API 응답 래퍼
 *
 * @param <T> 응답 데이터 타입
 */
@Getter
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
@Schema(description = "표준 API 응답")
public class ApiResponse<T> {

    /**
     * HTTP 상태 코드
     */
    @Schema(description = "HTTP 상태 코드", example = "200")
    private final int status;

    /**
     * 응답 메시지
     */
    @Schema(description = "응답 메시지", example = "Success")
    private final String message;

    /**
     * 응답 데이터
     */
    @Schema(description = "응답 데이터")
    private final T data;

    /**
     * 메타데이터 (페이징 정보 등)
     */
    @Schema(description = "메타데이터 (페이징 정보 등)")
    private final Map<String, Object> meta;

    /**
     * 응답 시간
     */
    @Schema(description = "응답 시간", example = "2025-11-29T10:30:00")
    @Builder.Default
    private final LocalDateTime timestamp = LocalDateTime.now();

    /**
     * 성공 응답 생성 (데이터 포함)
     */
    public static <T> ApiResponse<T> success(T data) {
        return ApiResponse.<T>builder()
            .status(200)
            .message("Success")
            .data(data)
            .build();
    }

    /**
     * 성공 응답 생성 (메시지와 데이터 포함)
     */
    public static <T> ApiResponse<T> success(String message, T data) {
        return ApiResponse.<T>builder()
            .status(200)
            .message(message)
            .data(data)
            .build();
    }

    /**
     * 성공 응답 생성 (메시지만)
     */
    public static <T> ApiResponse<T> success(String message) {
        return ApiResponse.<T>builder()
            .status(200)
            .message(message)
            .build();
    }

    /**
     * 성공 응답 생성 (데이터와 메타데이터 포함)
     */
    public static <T> ApiResponse<T> success(T data, Map<String, Object> meta) {
        return ApiResponse.<T>builder()
            .status(200)
            .message("Success")
            .data(data)
            .meta(meta)
            .build();
    }

    /**
     * 에러 응답 생성
     */
    public static <T> ApiResponse<T> error(int status, String message) {
        return ApiResponse.<T>builder()
            .status(status)
            .message(message)
            .build();
    }

    /**
     * 에러 응답 생성 (데이터 포함)
     */
    public static <T> ApiResponse<T> error(int status, String message, T data) {
        return ApiResponse.<T>builder()
            .status(status)
            .message(message)
            .data(data)
            .build();
    }
}
