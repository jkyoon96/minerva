package com.eduforum.api.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.scheduling.annotation.EnableScheduling;

import java.util.Properties;

/**
 * 이메일 설정
 */
@Slf4j
@Configuration
@EnableScheduling
public class EmailConfig {

    /**
     * SMTP JavaMailSender 빈 생성 (SMTP 프로바이더 사용 시)
     */
    @Bean
    @ConditionalOnProperty(name = "app.email.provider", havingValue = "smtp")
    public JavaMailSender javaMailSender(
            org.springframework.core.env.Environment env
    ) {
        JavaMailSenderImpl mailSender = new JavaMailSenderImpl();

        // SMTP 서버 설정
        mailSender.setHost(env.getProperty("spring.mail.host", "smtp.gmail.com"));
        mailSender.setPort(Integer.parseInt(env.getProperty("spring.mail.port", "587")));

        // 인증 정보
        mailSender.setUsername(env.getProperty("spring.mail.username"));
        mailSender.setPassword(env.getProperty("spring.mail.password"));

        // SMTP 속성 설정
        Properties props = mailSender.getJavaMailProperties();
        props.put("mail.transport.protocol", "smtp");
        props.put("mail.smtp.auth", env.getProperty("spring.mail.properties.mail.smtp.auth", "true"));
        props.put("mail.smtp.starttls.enable", env.getProperty("spring.mail.properties.mail.smtp.starttls.enable", "true"));
        props.put("mail.smtp.starttls.required", env.getProperty("spring.mail.properties.mail.smtp.starttls.required", "true"));
        props.put("mail.smtp.connectiontimeout", env.getProperty("spring.mail.properties.mail.smtp.connectiontimeout", "5000"));
        props.put("mail.smtp.timeout", env.getProperty("spring.mail.properties.mail.smtp.timeout", "5000"));
        props.put("mail.smtp.writetimeout", env.getProperty("spring.mail.properties.mail.smtp.writetimeout", "5000"));

        // SSL 설정 (필요한 경우)
        String sslEnable = env.getProperty("spring.mail.properties.mail.smtp.ssl.enable", "false");
        if ("true".equals(sslEnable)) {
            props.put("mail.smtp.ssl.enable", "true");
            props.put("mail.smtp.ssl.trust", env.getProperty("spring.mail.host", "smtp.gmail.com"));
        }

        // 디버그 모드 (개발 환경에서만)
        String debug = env.getProperty("spring.mail.properties.mail.debug", "false");
        props.put("mail.debug", debug);

        log.info("SMTP Email configuration initialized: host={}, port={}",
                mailSender.getHost(), mailSender.getPort());

        return mailSender;
    }
}
