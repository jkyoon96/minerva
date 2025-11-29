package com.eduforum.api.common.email.queue;

import com.eduforum.api.common.email.dto.EmailRequest;
import com.eduforum.api.common.util.JsonUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Map;

/**
 * 이메일 큐 서비스
 * 이메일 작업을 큐에 등록하고 관리합니다.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class EmailQueueService {

    private final EmailJobRepository emailJobRepository;

    /**
     * 이메일 작업을 큐에 등록
     *
     * @param request 이메일 요청
     * @return 생성된 작업 ID
     */
    @Transactional
    public Long enqueue(EmailRequest request) {
        return enqueue(request, null);
    }

    /**
     * 이메일 작업을 큐에 등록 (예약 발송)
     *
     * @param request 이메일 요청
     * @param scheduledAt 예약 발송 시간 (null이면 즉시 발송)
     * @return 생성된 작업 ID
     */
    @Transactional
    public Long enqueue(EmailRequest request, LocalDateTime scheduledAt) {
        try {
            // 템플릿 변수를 JSON으로 변환
            String variablesJson = null;
            if (request.isTemplateEmail() && request.getVariables() != null) {
                variablesJson = JsonUtil.toJson(request.getVariables());
            }

            // 이메일 작업 생성
            EmailJob job = EmailJob.builder()
                    .recipientEmail(request.getTo())
                    .subject(request.getSubject())
                    .body(request.getBody())
                    .templateName(request.getTemplateName())
                    .templateVariables(variablesJson)
                    .isHtml(request.isHtml())
                    .fromName(request.getFromName())
                    .replyTo(request.getReplyTo())
                    .status(EmailJobStatus.PENDING)
                    .retryCount(0)
                    .maxRetries(3)
                    .scheduledAt(scheduledAt)
                    .build();

            EmailJob savedJob = emailJobRepository.save(job);
            log.info("Email job enqueued: ID={}, To={}, Subject={}",
                    savedJob.getId(), request.getTo(), request.getSubject());

            return savedJob.getId();

        } catch (Exception e) {
            log.error("Failed to enqueue email job", e);
            throw new RuntimeException("Failed to enqueue email job", e);
        }
    }

    /**
     * 작업 상태를 처리 중으로 변경
     */
    @Transactional
    public void markAsProcessing(Long jobId) {
        emailJobRepository.findById(jobId).ifPresent(job -> {
            job.setStatus(EmailJobStatus.PROCESSING);
            emailJobRepository.save(job);
        });
    }

    /**
     * 작업을 성공으로 표시
     */
    @Transactional
    public void markAsSent(Long jobId, String messageId) {
        emailJobRepository.findById(jobId).ifPresent(job -> {
            job.setStatus(EmailJobStatus.SENT);
            job.setSentAt(LocalDateTime.now());
            job.setMessageId(messageId);
            emailJobRepository.save(job);
            log.info("Email job marked as sent: ID={}, MessageID={}", jobId, messageId);
        });
    }

    /**
     * 작업을 실패로 표시 (재시도 가능한 경우 재시도 상태로 변경)
     */
    @Transactional
    public void markAsFailed(Long jobId, String errorMessage) {
        emailJobRepository.findById(jobId).ifPresent(job -> {
            job.incrementRetry();
            job.setErrorMessage(errorMessage);

            if (job.canRetry()) {
                job.setStatus(EmailJobStatus.RETRYING);
                log.warn("Email job failed, will retry: ID={}, Retry={}/{}, Error={}",
                        jobId, job.getRetryCount(), job.getMaxRetries(), errorMessage);
            } else {
                job.setStatus(EmailJobStatus.FAILED);
                log.error("Email job permanently failed: ID={}, Error={}", jobId, errorMessage);
            }

            emailJobRepository.save(job);
        });
    }

    /**
     * 작업 취소
     */
    @Transactional
    public void cancel(Long jobId) {
        emailJobRepository.findById(jobId).ifPresent(job -> {
            if (job.isPending()) {
                job.setStatus(EmailJobStatus.FAILED);
                job.setErrorMessage("Cancelled by user");
                emailJobRepository.save(job);
                log.info("Email job cancelled: ID={}", jobId);
            }
        });
    }

    /**
     * 오래된 완료/실패 작업 정리 (30일 이상 경과)
     */
    @Transactional
    public void cleanupOldJobs() {
        LocalDateTime before = LocalDateTime.now().minusDays(30);
        var oldJobs = emailJobRepository.findOldCompletedJobs(before);

        if (!oldJobs.isEmpty()) {
            emailJobRepository.deleteAll(oldJobs);
            log.info("Cleaned up {} old email jobs", oldJobs.size());
        }
    }
}
