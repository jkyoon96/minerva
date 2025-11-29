package com.eduforum.api.domain.analytics.entity;

/**
 * Alert status for risk notifications
 */
public enum AlertStatus {
    PENDING,        // 대기 중
    SENT,           // 전송됨
    ACKNOWLEDGED,   // 확인됨
    RESOLVED        // 해결됨
}
