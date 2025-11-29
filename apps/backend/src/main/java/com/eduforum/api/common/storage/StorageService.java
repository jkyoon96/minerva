package com.eduforum.api.common.storage;

import com.eduforum.api.common.storage.dto.FileInfo;
import org.springframework.core.io.Resource;
import org.springframework.web.multipart.MultipartFile;

import java.io.InputStream;

/**
 * 파일 저장소 서비스 인터페이스
 *
 * 다양한 저장소 구현체를 추상화하는 인터페이스
 * - LocalStorageService: 로컬 파일 시스템
 * - S3StorageService: AWS S3
 */
public interface StorageService {

    /**
     * 파일 업로드
     *
     * @param file 업로드할 파일
     * @param path 저장 경로 (디렉토리)
     * @return 파일 정보
     */
    FileInfo upload(MultipartFile file, String path);

    /**
     * 파일 업로드 (InputStream)
     *
     * @param inputStream 입력 스트림
     * @param originalFilename 원본 파일명
     * @param contentType MIME 타입
     * @param size 파일 크기
     * @param path 저장 경로
     * @return 파일 정보
     */
    FileInfo upload(InputStream inputStream, String originalFilename, String contentType, long size, String path);

    /**
     * 파일 다운로드
     *
     * @param fileId 파일 ID (저장된 파일명)
     * @param path 저장 경로
     * @return 파일 리소스
     */
    Resource download(String fileId, String path);

    /**
     * 파일 삭제
     *
     * @param fileId 파일 ID (저장된 파일명)
     * @param path 저장 경로
     */
    void delete(String fileId, String path);

    /**
     * 파일 URL 조회 (공개 URL이 있는 경우)
     *
     * @param fileId 파일 ID
     * @param path 저장 경로
     * @return 파일 접근 URL
     */
    String getUrl(String fileId, String path);

    /**
     * 파일 존재 여부 확인
     *
     * @param fileId 파일 ID
     * @param path 저장 경로
     * @return 존재 여부
     */
    boolean exists(String fileId, String path);

    /**
     * 임시 다운로드 URL 생성 (Pre-signed URL 등)
     *
     * @param fileId 파일 ID
     * @param path 저장 경로
     * @param expirationMinutes 만료 시간 (분)
     * @return 임시 URL
     */
    String generatePresignedUrl(String fileId, String path, int expirationMinutes);
}
