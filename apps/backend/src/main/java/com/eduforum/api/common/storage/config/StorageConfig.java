package com.eduforum.api.common.storage.config;

import com.eduforum.api.common.storage.StorageService;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * 스토리지 서비스 설정
 */
@Configuration
@RequiredArgsConstructor
public class StorageConfig {

    private final StorageProperties storageProperties;

    /**
     * 활성 스토리지 서비스 빈 선택
     */
    @Bean
    public StorageService storageService(
        StorageService localStorageService,
        StorageService s3StorageService
    ) {
        String provider = storageProperties.getProvider();

        return switch (provider.toLowerCase()) {
            case "s3" -> s3StorageService;
            case "local" -> localStorageService;
            default -> throw new IllegalArgumentException("Unsupported storage provider: " + provider);
        };
    }
}
