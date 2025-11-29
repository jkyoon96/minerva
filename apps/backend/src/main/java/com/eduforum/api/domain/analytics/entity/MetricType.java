package com.eduforum.api.domain.analytics.entity;

/**
 * Type of learning metric
 */
public enum MetricType {
    ATTENDANCE,        // 출석률
    ENGAGEMENT,        // 참여도
    PERFORMANCE,       // 성과
    PARTICIPATION,     // 참여 점수
    COMPLETION,        // 완료율
    INTERACTION,       // 상호작용
    RESPONSE_TIME,     // 응답 시간
    QUIZ_SCORE,        // 퀴즈 점수
    ASSIGNMENT_SCORE,  // 과제 점수
    POLL_PARTICIPATION // 투표 참여
}
