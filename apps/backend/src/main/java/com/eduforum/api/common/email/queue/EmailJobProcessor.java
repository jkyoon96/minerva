package com.eduforum.api.common.email.queue;

import com.eduforum.api.common.email.EmailService;
import com.eduforum.api.common.email.dto.EmailRequest;
import com.eduforum.api.common.email.dto.EmailResult;
import com.eduforum.api.common.util.JsonUtil;
import com.fasterxml.jackson.core.type.TypeReference;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

/**
 * 이메일 작업 처리기
 * 스케줄링을 통해 큐에 있는 이메일을 처리합니다.
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class EmailJobProcessor {

    private final EmailJobRepository emailJobRepository;
    private final EmailQueueService queueService;
    private final EmailService emailService;

    private static final int BATCH_SIZE = 10;

    /**
     * 대기 중인 이메일 작업 처리 (1분마다 실행)
     */
    @Scheduled(fixedDelay = 60000) // 1분
    public void processPendingJobs() {
        try {
            List<EmailJob> pendingJobs = emailJobRepository.findPendingJobs(
                    LocalDateTime.now(),
                    PageRequest.of(0, BATCH_SIZE)
            );

            if (pendingJobs.isEmpty()) {
                return;
            }

            log.info("Processing {} pending email jobs", pendingJobs.size());

            for (EmailJob job : pendingJobs) {
                processJob(job);
            }

        } catch (Exception e) {
            log.error("Error processing pending email jobs", e);
        }
    }

    /**
     * 개별 작업 처리
     */
    private void processJob(EmailJob job) {
        try {
            // 상태를 처리 중으로 변경
            queueService.markAsProcessing(job.getId());

            // 이메일 요청 객체 생성
            EmailRequest.EmailRequestBuilder requestBuilder = EmailRequest.builder()
                    .to(job.getRecipientEmail())
                    .subject(job.getSubject())
                    .body(job.getBody())
                    .html(job.getIsHtml())
                    .fromName(job.getFromName())
                    .replyTo(job.getReplyTo());

            // 템플릿 정보가 있으면 추가
            if (job.getTemplateName() != null) {
                requestBuilder.templateName(job.getTemplateName());

                if (job.getTemplateVariables() != null) {
                    Map<String, Object> variables = JsonUtil.fromJson(
                            job.getTemplateVariables(),
                            new TypeReference<Map<String, Object>>() {}
                    );
                    requestBuilder.variables(variables);
                }
            }

            EmailRequest request = requestBuilder.build();

            // 이메일 발송
            EmailResult result = emailService.send(request);

            // 결과 처리
            if (result.isSuccess()) {
                queueService.markAsSent(job.getId(), result.getMessageId());
            } else {
                queueService.markAsFailed(job.getId(), result.getError());
            }

        } catch (Exception e) {
            log.error("Failed to process email job: ID={}", job.getId(), e);
            queueService.markAsFailed(job.getId(), e.getMessage());
        }
    }

    /**
     * 오래된 작업 정리 (매일 자정 실행)
     */
    @Scheduled(cron = "0 0 0 * * *")
    public void cleanupOldJobs() {
        try {
            log.info("Starting cleanup of old email jobs");
            queueService.cleanupOldJobs();
        } catch (Exception e) {
            log.error("Error cleaning up old email jobs", e);
        }
    }

    /**
     * 큐 상태 로깅 (10분마다 실행)
     */
    @Scheduled(fixedDelay = 600000) // 10분
    public void logQueueStatus() {
        try {
            long pending = emailJobRepository.countByStatus(EmailJobStatus.PENDING);
            long processing = emailJobRepository.countByStatus(EmailJobStatus.PROCESSING);
            long retrying = emailJobRepository.countByStatus(EmailJobStatus.RETRYING);
            long sent = emailJobRepository.countByStatus(EmailJobStatus.SENT);
            long failed = emailJobRepository.countByStatus(EmailJobStatus.FAILED);

            log.info("Email Queue Status - Pending: {}, Processing: {}, Retrying: {}, Sent: {}, Failed: {}",
                    pending, processing, retrying, sent, failed);

        } catch (Exception e) {
            log.error("Error logging queue status", e);
        }
    }
}
