package com.eduforum.api.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.transaction.annotation.EnableTransactionManagement;

/**
 * JPA 설정
 *
 * - JPA Repository 스캔
 * - 트랜잭션 관리 활성화
 * - JPA Auditing은 AuditConfig에서 별도 관리
 */
@Configuration
@EnableJpaRepositories(basePackages = "com.eduforum.api.domain")
@EnableTransactionManagement
public class JpaConfig {
}
