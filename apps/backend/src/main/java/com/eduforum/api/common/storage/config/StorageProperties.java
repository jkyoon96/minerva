package com.eduforum.api.common.storage.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * 스토리지 설정
 */
@Getter
@Setter
@Component
@ConfigurationProperties(prefix = "app.storage")
public class StorageProperties {

    /**
     * 스토리지 제공자 (local, s3)
     */
    private String provider = "local";

    /**
     * 로컬 스토리지 설정
     */
    private Local local = new Local();

    /**
     * S3 스토리지 설정
     */
    private S3 s3 = new S3();

    /**
     * 파일 업로드 설정
     */
    private Upload upload = new Upload();

    @Getter
    @Setter
    public static class Local {
        /**
         * 로컬 저장 기본 경로
         */
        private String basePath = "./uploads";
    }

    @Getter
    @Setter
    public static class S3 {
        /**
         * S3 버킷 이름
         */
        private String bucket;

        /**
         * AWS 리전
         */
        private String region = "ap-northeast-2";

        /**
         * AWS Access Key
         */
        private String accessKey;

        /**
         * AWS Secret Key
         */
        private String secretKey;

        /**
         * CloudFront 배포 URL (옵션)
         */
        private String cloudFrontUrl;
    }

    @Getter
    @Setter
    public static class Upload {
        /**
         * 최대 파일 크기 (bytes)
         */
        private long maxFileSize = 52428800L; // 50MB

        /**
         * 허용 MIME 타입 (쉼표 구분)
         */
        private String allowedMimeTypes = "image/*,application/pdf,application/msword,application/vnd.openxmlformats-officedocument.wordprocessingml.document,application/vnd.ms-excel,application/vnd.openxmlformats-officedocument.spreadsheetml.sheet,application/vnd.ms-powerpoint,application/vnd.openxmlformats-officedocument.presentationml.presentation,text/*,video/*";

        /**
         * 파일명 길이 제한
         */
        private int maxFilenameLength = 255;
    }
}
