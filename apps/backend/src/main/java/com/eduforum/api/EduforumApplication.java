package com.eduforum.api;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

/**
 * EduForum API Application
 * 미네르바 대학의 Active Learning Forum을 참고한 교육 플랫폼
 */
@SpringBootApplication
@EnableJpaAuditing
public class EduforumApplication {

    public static void main(String[] args) {
        SpringApplication.run(EduforumApplication.class, args);
    }
}
