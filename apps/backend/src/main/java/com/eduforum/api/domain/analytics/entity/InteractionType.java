package com.eduforum.api.domain.analytics.entity;

/**
 * Type of student interaction
 */
public enum InteractionType {
    CHAT,           // 채팅 메시지
    REPLY,          // 답글
    COLLABORATION,  // 협업
    POLL_RESPONSE,  // 투표 응답
    QUIZ_ANSWER,    // 퀴즈 답변
    DISCUSSION,     // 토론 참여
    WHITEBOARD,     // 화이트보드 상호작용
    BREAKOUT        // 분반 활동
}
