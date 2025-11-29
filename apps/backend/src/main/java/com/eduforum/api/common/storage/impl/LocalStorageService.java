package com.eduforum.api.common.storage.impl;

import com.eduforum.api.common.storage.StorageService;
import com.eduforum.api.common.storage.config.StorageProperties;
import com.eduforum.api.common.storage.dto.FileInfo;
import com.eduforum.api.common.storage.exception.StorageException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.UUID;

/**
 * 로컬 파일 시스템 기반 스토리지 서비스
 */
@Slf4j
@Service("localStorageService")
@RequiredArgsConstructor
public class LocalStorageService implements StorageService {

    private final StorageProperties storageProperties;

    @Override
    public FileInfo upload(MultipartFile file, String path) {
        try {
            return upload(
                file.getInputStream(),
                file.getOriginalFilename(),
                file.getContentType(),
                file.getSize(),
                path
            );
        } catch (IOException e) {
            throw new StorageException("Failed to read file", e);
        }
    }

    @Override
    public FileInfo upload(InputStream inputStream, String originalFilename, String contentType, long size, String path) {
        try {
            // 저장 디렉토리 생성
            Path uploadPath = getUploadPath(path);
            Files.createDirectories(uploadPath);

            // 파일명 생성 (UUID + 확장자)
            String extension = getFileExtension(originalFilename);
            String storedName = UUID.randomUUID().toString() + extension;

            // 파일 저장
            Path targetPath = uploadPath.resolve(storedName);
            Files.copy(inputStream, targetPath, StandardCopyOption.REPLACE_EXISTING);

            log.info("File uploaded successfully: {} -> {}", originalFilename, targetPath);

            return FileInfo.builder()
                .originalName(originalFilename)
                .storedName(storedName)
                .path(path)
                .size(size)
                .mimeType(contentType)
                .extension(extension)
                .url(null) // 로컬 스토리지는 직접 URL 없음
                .build();

        } catch (IOException e) {
            throw new StorageException("Failed to store file: " + originalFilename, e);
        }
    }

    @Override
    public Resource download(String fileId, String path) {
        try {
            Path filePath = getUploadPath(path).resolve(fileId).normalize();
            Resource resource = new UrlResource(filePath.toUri());

            if (resource.exists() && resource.isReadable()) {
                return resource;
            } else {
                throw new StorageException("File not found or not readable: " + fileId);
            }
        } catch (MalformedURLException e) {
            throw new StorageException("Failed to read file: " + fileId, e);
        }
    }

    @Override
    public void delete(String fileId, String path) {
        try {
            Path filePath = getUploadPath(path).resolve(fileId).normalize();
            Files.deleteIfExists(filePath);
            log.info("File deleted: {}", filePath);
        } catch (IOException e) {
            throw new StorageException("Failed to delete file: " + fileId, e);
        }
    }

    @Override
    public String getUrl(String fileId, String path) {
        // 로컬 스토리지는 직접 접근 URL이 없음
        // 별도의 엔드포인트를 통해 제공해야 함
        return "/api/v1/files/" + fileId + "/download";
    }

    @Override
    public boolean exists(String fileId, String path) {
        Path filePath = getUploadPath(path).resolve(fileId).normalize();
        return Files.exists(filePath);
    }

    @Override
    public String generatePresignedUrl(String fileId, String path, int expirationMinutes) {
        // 로컬 스토리지에서는 Pre-signed URL을 지원하지 않음
        return getUrl(fileId, path);
    }

    /**
     * 업로드 경로 조회
     */
    private Path getUploadPath(String subPath) {
        String basePath = storageProperties.getLocal().getBasePath();
        if (StringUtils.hasText(subPath)) {
            return Paths.get(basePath, subPath);
        }
        return Paths.get(basePath);
    }

    /**
     * 파일 확장자 추출
     */
    private String getFileExtension(String filename) {
        if (filename == null) {
            return "";
        }
        int dotIndex = filename.lastIndexOf('.');
        if (dotIndex > 0 && dotIndex < filename.length() - 1) {
            return filename.substring(dotIndex);
        }
        return "";
    }
}
