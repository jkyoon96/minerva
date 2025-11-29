package com.eduforum.api.common.email.impl;

import com.eduforum.api.common.email.EmailService;
import com.eduforum.api.common.email.dto.EmailRequest;
import com.eduforum.api.common.email.dto.EmailResult;
import com.eduforum.api.common.email.queue.EmailQueueService;
import com.eduforum.api.common.email.template.EmailTemplateService;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import java.util.Map;

/**
 * SMTP 이메일 서비스
 * Spring Mail을 사용하여 SMTP를 통해 실제 이메일을 발송합니다.
 */
@Slf4j
@Service
@RequiredArgsConstructor
@ConditionalOnProperty(name = "app.email.provider", havingValue = "smtp")
public class SmtpEmailService implements EmailService {

    private final JavaMailSender mailSender;
    private final EmailTemplateService templateService;
    private final EmailQueueService queueService;

    @Value("${app.email.from:noreply@eduforum.com}")
    private String defaultFrom;

    @Value("${app.email.from-name:EduForum}")
    private String defaultFromName;

    @Override
    public EmailResult sendEmail(String to, String subject, String body) {
        EmailRequest request = EmailRequest.builder()
                .to(to)
                .subject(subject)
                .body(body)
                .html(true)
                .build();
        return send(request);
    }

    @Override
    public EmailResult sendTemplateEmail(String to, String templateName, Map<String, Object> variables) {
        EmailRequest request = EmailRequest.builder()
                .to(to)
                .templateName(templateName)
                .variables(variables)
                .html(true)
                .build();
        return send(request);
    }

    @Override
    public EmailResult send(EmailRequest request) {
        try {
            String body = request.getBody();

            // 템플릿 이메일인 경우 템플릿 렌더링
            if (request.isTemplateEmail()) {
                body = templateService.renderTemplate(request.getTemplateName(), request.getVariables());
                String subject = templateService.getTemplateSubject(request.getTemplateName(), request.getVariables());
                if (subject != null) {
                    request.setSubject(subject);
                }
            }

            // MIME 메시지 생성
            MimeMessage mimeMessage = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, true, "UTF-8");

            // 발신자 설정
            String fromName = request.getFromName() != null ? request.getFromName() : defaultFromName;
            helper.setFrom(defaultFrom, fromName);

            // 수신자 및 제목 설정
            helper.setTo(request.getTo());
            helper.setSubject(request.getSubject());

            // 답장 주소 설정
            if (request.getReplyTo() != null) {
                helper.setReplyTo(request.getReplyTo());
            }

            // 본문 설정 (HTML 또는 Plain Text)
            helper.setText(body, request.isHtml());

            // 이메일 발송
            mailSender.send(mimeMessage);

            log.info("Email sent successfully via SMTP to: {}", request.getTo());

            // 성공 결과 반환 (SMTP는 메시지 ID를 직접 제공하지 않음)
            return EmailResult.success(request.getTo(), request.getSubject(), "smtp-sent");

        } catch (Exception e) {
            log.error("Failed to send SMTP email to: {}", request.getTo(), e);
            return EmailResult.failure(request.getTo(), request.getSubject(), e.getMessage());
        }
    }

    @Override
    public void sendAsync(EmailRequest request) {
        queueService.enqueue(request);
    }
}
