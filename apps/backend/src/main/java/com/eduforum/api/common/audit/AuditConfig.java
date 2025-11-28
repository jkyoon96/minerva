package com.eduforum.api.common.audit;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.domain.AuditorAware;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

/**
 * JPA Auditing 설정
 *
 * 기능:
 * - @CreatedDate, @LastModifiedDate 자동 설정
 * - @CreatedBy, @LastModifiedBy 자동 설정
 * - 엔티티 생성/수정 이력 자동 추적
 */
@Configuration
@EnableJpaAuditing(auditorAwareRef = "auditorProvider")
public class AuditConfig {

    /**
     * 현재 사용자 정보 제공자 빈 등록
     */
    @Bean
    public AuditorAware<String> auditorProvider() {
        return new AuditorAwareImpl();
    }
}
