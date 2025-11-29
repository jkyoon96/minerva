package com.eduforum.api.common.storage.validator;

import com.eduforum.api.common.storage.config.StorageProperties;
import com.eduforum.api.common.storage.exception.FileValidationException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.util.Arrays;
import java.util.List;

/**
 * 파일 검증 컴포넌트
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class FileValidator {

    private final StorageProperties storageProperties;

    /**
     * 파일 검증
     */
    public void validate(MultipartFile file) {
        if (file == null || file.isEmpty()) {
            throw new FileValidationException("파일이 비어있습니다");
        }

        validateSize(file);
        validateMimeType(file);
        validateFilename(file);
    }

    /**
     * 파일 크기 검증
     */
    private void validateSize(MultipartFile file) {
        long maxSize = storageProperties.getUpload().getMaxFileSize();

        if (file.getSize() > maxSize) {
            throw new FileValidationException(
                String.format("파일 크기가 너무 큽니다. 최대 크기: %d MB", maxSize / (1024 * 1024))
            );
        }
    }

    /**
     * MIME 타입 검증
     */
    private void validateMimeType(MultipartFile file) {
        String allowedTypes = storageProperties.getUpload().getAllowedMimeTypes();
        String contentType = file.getContentType();

        if (contentType == null || contentType.isEmpty()) {
            throw new FileValidationException("파일 타입을 확인할 수 없습니다");
        }

        // 허용 타입 파싱
        List<String> allowedTypeList = Arrays.asList(allowedTypes.split(","));

        // MIME 타입 검증
        boolean isAllowed = allowedTypeList.stream()
            .map(String::trim)
            .anyMatch(allowed -> {
                if (allowed.endsWith("/*")) {
                    // 와일드카드 검증 (예: image/*)
                    String prefix = allowed.substring(0, allowed.length() - 2);
                    return contentType.startsWith(prefix + "/");
                } else {
                    // 정확한 MIME 타입 검증
                    return contentType.equals(allowed);
                }
            });

        if (!isAllowed) {
            throw new FileValidationException(
                String.format("허용되지 않는 파일 타입입니다: %s", contentType)
            );
        }
    }

    /**
     * 파일명 검증
     */
    private void validateFilename(MultipartFile file) {
        String filename = file.getOriginalFilename();

        if (filename == null || filename.isEmpty()) {
            throw new FileValidationException("파일명이 없습니다");
        }

        int maxLength = storageProperties.getUpload().getMaxFilenameLength();
        if (filename.length() > maxLength) {
            throw new FileValidationException(
                String.format("파일명이 너무 깁니다. 최대 길이: %d자", maxLength)
            );
        }

        // 위험한 문자 검증 (선택적)
        if (filename.contains("..") || filename.contains("/") || filename.contains("\\")) {
            throw new FileValidationException("파일명에 허용되지 않는 문자가 포함되어 있습니다");
        }
    }
}
