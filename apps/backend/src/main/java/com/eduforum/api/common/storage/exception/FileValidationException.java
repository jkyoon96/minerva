package com.eduforum.api.common.storage.exception;

/**
 * 파일 검증 실패 예외
 */
public class FileValidationException extends RuntimeException {

    public FileValidationException(String message) {
        super(message);
    }

    public FileValidationException(String message, Throwable cause) {
        super(message, cause);
    }
}
