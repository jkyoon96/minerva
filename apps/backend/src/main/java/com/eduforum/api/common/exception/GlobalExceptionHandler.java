package com.eduforum.api.common.exception;

import com.eduforum.api.common.dto.ApiResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.validation.BindException;
import org.springframework.validation.FieldError;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import java.util.HashMap;
import java.util.Map;

/**
 * 전역 예외 처리
 */
@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    /**
     * 비즈니스 예외 처리
     */
    @ExceptionHandler(BusinessException.class)
    protected ResponseEntity<ApiResponse<Object>> handleBusinessException(BusinessException e) {
        log.error("BusinessException: {}", e.getMessage(), e);
        ErrorCode errorCode = e.errorCode;
        ApiResponse<Object> response = ApiResponse.error(
            errorCode.getStatus(),
            e.getMessage()
        );
        return ResponseEntity
            .status(errorCode.getHttpStatus())
            .body(response);
    }

    /**
     * Validation 예외 처리
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    protected ResponseEntity<ApiResponse<Object>> handleMethodArgumentNotValidException(
        MethodArgumentNotValidException e) {
        log.error("MethodArgumentNotValidException: {}", e.getMessage());

        Map<String, String> errors = new HashMap<>();
        e.getBindingResult().getAllErrors().forEach(error -> {
            String fieldName = ((FieldError) error).getField();
            String errorMessage = error.getDefaultMessage();
            errors.put(fieldName, errorMessage);
        });

        ApiResponse<Object> response = ApiResponse.error(
            HttpStatus.BAD_REQUEST.value(),
            "입력값이 올바르지 않습니다",
            errors
        );
        return ResponseEntity
            .status(HttpStatus.BAD_REQUEST)
            .body(response);
    }

    /**
     * Bind 예외 처리
     */
    @ExceptionHandler(BindException.class)
    protected ResponseEntity<ApiResponse<Object>> handleBindException(BindException e) {
        log.error("BindException: {}", e.getMessage());

        Map<String, String> errors = new HashMap<>();
        e.getBindingResult().getAllErrors().forEach(error -> {
            String fieldName = ((FieldError) error).getField();
            String errorMessage = error.getDefaultMessage();
            errors.put(fieldName, errorMessage);
        });

        ApiResponse<Object> response = ApiResponse.error(
            HttpStatus.BAD_REQUEST.value(),
            "입력값이 올바르지 않습니다",
            errors
        );
        return ResponseEntity
            .status(HttpStatus.BAD_REQUEST)
            .body(response);
    }

    /**
     * Type Mismatch 예외 처리
     */
    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    protected ResponseEntity<ApiResponse<Object>> handleMethodArgumentTypeMismatchException(
        MethodArgumentTypeMismatchException e) {
        log.error("MethodArgumentTypeMismatchException: {}", e.getMessage());

        ApiResponse<Object> response = ApiResponse.error(
            HttpStatus.BAD_REQUEST.value(),
            String.format("'%s' 파라미터의 타입이 올바르지 않습니다", e.getName())
        );
        return ResponseEntity
            .status(HttpStatus.BAD_REQUEST)
            .body(response);
    }

    /**
     * HTTP Method 예외 처리
     */
    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    protected ResponseEntity<ApiResponse<Object>> handleHttpRequestMethodNotSupportedException(
        HttpRequestMethodNotSupportedException e) {
        log.error("HttpRequestMethodNotSupportedException: {}", e.getMessage());

        ApiResponse<Object> response = ApiResponse.error(
            HttpStatus.METHOD_NOT_ALLOWED.value(),
            ErrorCode.METHOD_NOT_ALLOWED.getMessage()
        );
        return ResponseEntity
            .status(HttpStatus.METHOD_NOT_ALLOWED)
            .body(response);
    }

    /**
     * 인증 예외 처리
     */
    @ExceptionHandler(AuthenticationException.class)
    protected ResponseEntity<ApiResponse<Object>> handleAuthenticationException(
        AuthenticationException e) {
        log.error("AuthenticationException: {}", e.getMessage(), e);

        ApiResponse<Object> response = ApiResponse.error(
            HttpStatus.UNAUTHORIZED.value(),
            ErrorCode.AUTHENTICATION_FAILED.getMessage()
        );
        return ResponseEntity
            .status(HttpStatus.UNAUTHORIZED)
            .body(response);
    }

    /**
     * 접근 거부 예외 처리
     */
    @ExceptionHandler(AccessDeniedException.class)
    protected ResponseEntity<ApiResponse<Object>> handleAccessDeniedException(
        AccessDeniedException e) {
        log.error("AccessDeniedException: {}", e.getMessage(), e);

        ApiResponse<Object> response = ApiResponse.error(
            HttpStatus.FORBIDDEN.value(),
            ErrorCode.ACCESS_DENIED.getMessage()
        );
        return ResponseEntity
            .status(HttpStatus.FORBIDDEN)
            .body(response);
    }

    /**
     * 기타 모든 예외 처리
     */
    @ExceptionHandler(Exception.class)
    protected ResponseEntity<ApiResponse<Object>> handleException(Exception e) {
        log.error("Exception: {}", e.getMessage(), e);

        ApiResponse<Object> response = ApiResponse.error(
            HttpStatus.INTERNAL_SERVER_ERROR.value(),
            ErrorCode.INTERNAL_SERVER_ERROR.getMessage()
        );
        return ResponseEntity
            .status(HttpStatus.INTERNAL_SERVER_ERROR)
            .body(response);
    }
}
