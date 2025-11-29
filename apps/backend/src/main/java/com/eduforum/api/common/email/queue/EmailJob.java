package com.eduforum.api.common.email.queue;

import com.eduforum.api.common.audit.BaseEntity;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * 이메일 작업 엔티티 (큐)
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EqualsAndHashCode(callSuper = true)
@Entity
@Table(name = "email_jobs", indexes = {
        @Index(name = "idx_email_jobs_status", columnList = "status"),
        @Index(name = "idx_email_jobs_scheduled_at", columnList = "scheduled_at")
})
public class EmailJob extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * 수신자 이메일
     */
    @Column(name = "recipient_email", nullable = false, length = 255)
    private String recipientEmail;

    /**
     * 이메일 제목
     */
    @Column(name = "subject", nullable = false, length = 500)
    private String subject;

    /**
     * 이메일 본문
     */
    @Column(name = "body", columnDefinition = "TEXT")
    private String body;

    /**
     * 템플릿 이름 (템플릿 사용 시)
     */
    @Column(name = "template_name", length = 100)
    private String templateName;

    /**
     * 템플릿 변수 (JSON 형식)
     */
    @Column(name = "template_variables", columnDefinition = "TEXT")
    private String templateVariables;

    /**
     * HTML 이메일 여부
     */
    @Column(name = "is_html", nullable = false)
    @Builder.Default
    private Boolean isHtml = true;

    /**
     * 발신자 이름
     */
    @Column(name = "from_name", length = 100)
    private String fromName;

    /**
     * 답장 이메일 주소
     */
    @Column(name = "reply_to", length = 255)
    private String replyTo;

    /**
     * 작업 상태
     */
    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 20)
    @Builder.Default
    private EmailJobStatus status = EmailJobStatus.PENDING;

    /**
     * 재시도 횟수
     */
    @Column(name = "retry_count", nullable = false)
    @Builder.Default
    private Integer retryCount = 0;

    /**
     * 최대 재시도 횟수
     */
    @Column(name = "max_retries", nullable = false)
    @Builder.Default
    private Integer maxRetries = 3;

    /**
     * 예약 발송 시간
     */
    @Column(name = "scheduled_at")
    private LocalDateTime scheduledAt;

    /**
     * 실제 발송 시간
     */
    @Column(name = "sent_at")
    private LocalDateTime sentAt;

    /**
     * 에러 메시지
     */
    @Column(name = "error_message", columnDefinition = "TEXT")
    private String errorMessage;

    /**
     * 메시지 ID (SMTP/SendGrid에서 반환)
     */
    @Column(name = "message_id", length = 255)
    private String messageId;

    /**
     * 재시도 가능 여부 확인
     */
    public boolean canRetry() {
        return retryCount < maxRetries;
    }

    /**
     * 재시도 횟수 증가
     */
    public void incrementRetry() {
        this.retryCount++;
    }

    /**
     * 발송 대기 중인 작업인지 확인
     */
    public boolean isPending() {
        return status == EmailJobStatus.PENDING || status == EmailJobStatus.RETRYING;
    }
}
