package com.eduforum.api.common.email.impl;

import com.eduforum.api.common.email.EmailService;
import com.eduforum.api.common.email.dto.EmailRequest;
import com.eduforum.api.common.email.dto.EmailResult;
import com.eduforum.api.common.email.queue.EmailQueueService;
import com.eduforum.api.common.email.template.EmailTemplateService;
import com.sendgrid.Method;
import com.sendgrid.Request;
import com.sendgrid.Response;
import com.sendgrid.SendGrid;
import com.sendgrid.helpers.mail.Mail;
import com.sendgrid.helpers.mail.objects.Content;
import com.sendgrid.helpers.mail.objects.Email;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;

import java.util.Map;

/**
 * SendGrid 이메일 서비스
 * SendGrid API를 사용하여 이메일을 발송합니다.
 */
@Slf4j
@Service
@RequiredArgsConstructor
@ConditionalOnProperty(name = "app.email.provider", havingValue = "sendgrid")
public class SendGridEmailService implements EmailService {

    private final EmailTemplateService templateService;
    private final EmailQueueService queueService;

    @Value("${sendgrid.api-key}")
    private String sendGridApiKey;

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

            // SendGrid 메일 객체 생성
            String fromName = request.getFromName() != null ? request.getFromName() : defaultFromName;
            Email from = new Email(defaultFrom, fromName);
            Email toEmail = new Email(request.getTo());

            Content content = new Content(
                    request.isHtml() ? "text/html" : "text/plain",
                    body
            );

            Mail mail = new Mail(from, request.getSubject(), toEmail, content);

            // 답장 주소 설정
            if (request.getReplyTo() != null) {
                mail.setReplyTo(new Email(request.getReplyTo()));
            }

            // SendGrid API 호출
            SendGrid sg = new SendGrid(sendGridApiKey);
            Request sgRequest = new Request();
            sgRequest.setMethod(Method.POST);
            sgRequest.setEndpoint("mail/send");
            sgRequest.setBody(mail.build());

            Response response = sg.api(sgRequest);

            // 응답 확인
            if (response.getStatusCode() >= 200 && response.getStatusCode() < 300) {
                log.info("Email sent successfully via SendGrid to: {}, Status: {}",
                        request.getTo(), response.getStatusCode());

                // SendGrid 메시지 ID 추출 (헤더에서)
                String messageId = response.getHeaders().getOrDefault("X-Message-Id", "sendgrid-sent");
                return EmailResult.success(request.getTo(), request.getSubject(), messageId);
            } else {
                log.error("SendGrid API error. Status: {}, Body: {}",
                        response.getStatusCode(), response.getBody());
                return EmailResult.failure(request.getTo(), request.getSubject(),
                        "SendGrid API error: " + response.getStatusCode());
            }

        } catch (Exception e) {
            log.error("Failed to send SendGrid email to: {}", request.getTo(), e);
            return EmailResult.failure(request.getTo(), request.getSubject(), e.getMessage());
        }
    }

    @Override
    public void sendAsync(EmailRequest request) {
        queueService.enqueue(request);
    }
}
