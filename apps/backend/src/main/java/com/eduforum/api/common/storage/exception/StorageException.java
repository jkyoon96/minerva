package com.eduforum.api.common.storage.exception;

/**
 * 스토리지 관련 예외
 */
public class StorageException extends RuntimeException {

    public StorageException(String message) {
        super(message);
    }

    public StorageException(String message, Throwable cause) {
        super(message, cause);
    }
}
