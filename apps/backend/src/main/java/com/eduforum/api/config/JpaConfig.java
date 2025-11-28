package com.eduforum.api.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

/**
 * JPA 설정
 */
@Configuration
@EnableJpaRepositories(basePackages = "com.eduforum.api.domain")
public class JpaConfig {
}
