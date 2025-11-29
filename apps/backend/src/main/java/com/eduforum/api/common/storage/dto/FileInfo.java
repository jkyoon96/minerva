package com.eduforum.api.common.storage.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * 파일 정보 DTO
 */
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FileInfo {

    /**
     * 원본 파일명
     */
    private String originalName;

    /**
     * 저장된 파일명 (UUID 등)
     */
    private String storedName;

    /**
     * 저장 경로
     */
    private String path;

    /**
     * 파일 크기 (bytes)
     */
    private Long size;

    /**
     * MIME 타입
     */
    private String mimeType;

    /**
     * 파일 URL (접근 가능한 경우)
     */
    private String url;

    /**
     * 확장자
     */
    private String extension;
}
