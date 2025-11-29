package com.eduforum.api.common.email.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * 이메일 발송 결과 DTO
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class EmailResult {

    /**
     * 성공 여부
     */
    private boolean success;

    /**
     * 메시지 ID (SMTP/SendGrid에서 반환)
     */
    private String messageId;

    /**
     * 에러 메시지 (실패 시)
     */
    private String error;

    /**
     * 발송 일시
     */
    private LocalDateTime sentAt;

    /**
     * 수신자 이메일
     */
    private String to;

    /**
     * 제목
     */
    private String subject;

    /**
     * 성공 결과 생성
     */
    public static EmailResult success(String to, String subject, String messageId) {
        return EmailResult.builder()
                .success(true)
                .to(to)
                .subject(subject)
                .messageId(messageId)
                .sentAt(LocalDateTime.now())
                .build();
    }

    /**
     * 실패 결과 생성
     */
    public static EmailResult failure(String to, String subject, String error) {
        return EmailResult.builder()
                .success(false)
                .to(to)
                .subject(subject)
                .error(error)
                .sentAt(LocalDateTime.now())
                .build();
    }
}
