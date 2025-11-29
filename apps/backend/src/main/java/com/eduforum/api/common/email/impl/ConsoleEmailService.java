package com.eduforum.api.common.email.impl;

import com.eduforum.api.common.email.EmailService;
import com.eduforum.api.common.email.dto.EmailRequest;
import com.eduforum.api.common.email.dto.EmailResult;
import com.eduforum.api.common.email.queue.EmailQueueService;
import com.eduforum.api.common.email.template.EmailTemplateService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.UUID;

/**
 * 콘솔 출력 이메일 서비스 (개발용)
 * 실제 이메일을 발송하지 않고 콘솔에 내용을 출력합니다.
 */
@Slf4j
@Service
@RequiredArgsConstructor
@ConditionalOnProperty(name = "app.email.provider", havingValue = "console", matchIfMissing = true)
public class ConsoleEmailService implements EmailService {

    private final EmailTemplateService templateService;
    private final EmailQueueService queueService;

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

            // 콘솔에 이메일 내용 출력
            printEmailToConsole(request, body);

            // 성공 결과 반환 (가상의 메시지 ID 생성)
            String messageId = "console-" + UUID.randomUUID().toString();
            return EmailResult.success(request.getTo(), request.getSubject(), messageId);

        } catch (Exception e) {
            log.error("Failed to send console email to: {}", request.getTo(), e);
            return EmailResult.failure(request.getTo(), request.getSubject(), e.getMessage());
        }
    }

    @Override
    public void sendAsync(EmailRequest request) {
        queueService.enqueue(request);
    }

    /**
     * 이메일 내용을 콘솔에 출력
     */
    private void printEmailToConsole(EmailRequest request, String body) {
        log.info("\n" +
                "================================================================================\n" +
                "📧 EMAIL (Console Mode)\n" +
                "================================================================================\n" +
                "To:       {}\n" +
                "Subject:  {}\n" +
                "From:     {}\n" +
                "ReplyTo:  {}\n" +
                "Type:     {}\n" +
                "--------------------------------------------------------------------------------\n" +
                "{}\n" +
                "================================================================================\n",
                request.getTo(),
                request.getSubject(),
                request.getFromName() != null ? request.getFromName() : "EduForum",
                request.getReplyTo() != null ? request.getReplyTo() : "N/A",
                request.isHtml() ? "HTML" : "Plain Text",
                body
        );
    }
}
