package com.eduforum.api.common.email.queue;

/**
 * 이메일 작업 상태
 */
public enum EmailJobStatus {
    /**
     * 대기 중
     */
    PENDING,

    /**
     * 처리 중
     */
    PROCESSING,

    /**
     * 재시도 중
     */
    RETRYING,

    /**
     * 발송 성공
     */
    SENT,

    /**
     * 발송 실패 (재시도 초과)
     */
    FAILED
}
