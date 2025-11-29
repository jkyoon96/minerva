package com.eduforum.api.common.storage.impl;

import com.eduforum.api.common.storage.StorageService;
import com.eduforum.api.common.storage.config.StorageProperties;
import com.eduforum.api.common.storage.dto.FileInfo;
import com.eduforum.api.common.storage.exception.StorageException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.*;
import software.amazon.awssdk.services.s3.presigner.S3Presigner;
import software.amazon.awssdk.services.s3.presigner.model.GetObjectPresignRequest;
import software.amazon.awssdk.services.s3.presigner.model.PresignedGetObjectRequest;

import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import java.io.IOException;
import java.io.InputStream;
import java.time.Duration;
import java.util.UUID;

/**
 * AWS S3 기반 스토리지 서비스
 */
@Slf4j
@Service("s3StorageService")
@RequiredArgsConstructor
public class S3StorageService implements StorageService {

    private final StorageProperties storageProperties;
    private S3Client s3Client;
    private S3Presigner s3Presigner;

    @PostConstruct
    public void init() {
        StorageProperties.S3 s3Config = storageProperties.getS3();

        // S3 Client 초기화
        s3Client = S3Client.builder()
            .region(Region.of(s3Config.getRegion()))
            .credentialsProvider(StaticCredentialsProvider.create(
                AwsBasicCredentials.create(s3Config.getAccessKey(), s3Config.getSecretKey())
            ))
            .build();

        // S3 Presigner 초기화
        s3Presigner = S3Presigner.builder()
            .region(Region.of(s3Config.getRegion()))
            .credentialsProvider(StaticCredentialsProvider.create(
                AwsBasicCredentials.create(s3Config.getAccessKey(), s3Config.getSecretKey())
            ))
            .build();

        log.info("S3 Storage Service initialized with bucket: {}", s3Config.getBucket());
    }

    @PreDestroy
    public void cleanup() {
        if (s3Client != null) {
            s3Client.close();
        }
        if (s3Presigner != null) {
            s3Presigner.close();
        }
    }

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
            // S3 키 생성 (경로 + UUID + 확장자)
            String extension = getFileExtension(originalFilename);
            String storedName = UUID.randomUUID().toString() + extension;
            String s3Key = buildS3Key(path, storedName);

            // S3에 업로드
            PutObjectRequest putObjectRequest = PutObjectRequest.builder()
                .bucket(storageProperties.getS3().getBucket())
                .key(s3Key)
                .contentType(contentType)
                .contentLength(size)
                .build();

            s3Client.putObject(putObjectRequest, RequestBody.fromInputStream(inputStream, size));

            log.info("File uploaded to S3: {} -> {}", originalFilename, s3Key);

            return FileInfo.builder()
                .originalName(originalFilename)
                .storedName(storedName)
                .path(path)
                .size(size)
                .mimeType(contentType)
                .extension(extension)
                .url(getUrl(storedName, path))
                .build();

        } catch (Exception e) {
            throw new StorageException("Failed to upload file to S3: " + originalFilename, e);
        }
    }

    @Override
    public Resource download(String fileId, String path) {
        // S3에서는 Resource로 직접 반환하기보다 Pre-signed URL 사용 권장
        throw new UnsupportedOperationException("Use generatePresignedUrl() for S3 downloads");
    }

    @Override
    public void delete(String fileId, String path) {
        try {
            String s3Key = buildS3Key(path, fileId);

            DeleteObjectRequest deleteObjectRequest = DeleteObjectRequest.builder()
                .bucket(storageProperties.getS3().getBucket())
                .key(s3Key)
                .build();

            s3Client.deleteObject(deleteObjectRequest);
            log.info("File deleted from S3: {}", s3Key);

        } catch (Exception e) {
            throw new StorageException("Failed to delete file from S3: " + fileId, e);
        }
    }

    @Override
    public String getUrl(String fileId, String path) {
        StorageProperties.S3 s3Config = storageProperties.getS3();

        // CloudFront URL이 설정된 경우
        if (StringUtils.hasText(s3Config.getCloudFrontUrl())) {
            return s3Config.getCloudFrontUrl() + "/" + buildS3Key(path, fileId);
        }

        // S3 직접 URL
        String s3Key = buildS3Key(path, fileId);
        return String.format("https://%s.s3.%s.amazonaws.com/%s",
            s3Config.getBucket(),
            s3Config.getRegion(),
            s3Key
        );
    }

    @Override
    public boolean exists(String fileId, String path) {
        try {
            String s3Key = buildS3Key(path, fileId);

            HeadObjectRequest headObjectRequest = HeadObjectRequest.builder()
                .bucket(storageProperties.getS3().getBucket())
                .key(s3Key)
                .build();

            s3Client.headObject(headObjectRequest);
            return true;

        } catch (NoSuchKeyException e) {
            return false;
        } catch (Exception e) {
            throw new StorageException("Failed to check file existence in S3: " + fileId, e);
        }
    }

    @Override
    public String generatePresignedUrl(String fileId, String path, int expirationMinutes) {
        try {
            String s3Key = buildS3Key(path, fileId);

            GetObjectRequest getObjectRequest = GetObjectRequest.builder()
                .bucket(storageProperties.getS3().getBucket())
                .key(s3Key)
                .build();

            GetObjectPresignRequest presignRequest = GetObjectPresignRequest.builder()
                .signatureDuration(Duration.ofMinutes(expirationMinutes))
                .getObjectRequest(getObjectRequest)
                .build();

            PresignedGetObjectRequest presignedRequest = s3Presigner.presignGetObject(presignRequest);
            return presignedRequest.url().toString();

        } catch (Exception e) {
            throw new StorageException("Failed to generate presigned URL: " + fileId, e);
        }
    }

    /**
     * S3 키 생성
     */
    private String buildS3Key(String path, String filename) {
        if (StringUtils.hasText(path)) {
            return path + "/" + filename;
        }
        return filename;
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
