package com.eduforum.api.domain.assessment.entity;

/**
 * Participation event type enum
 */
public enum EventType {
    /**
     * Attendance event
     */
    ATTENDANCE,

    /**
     * Poll response
     */
    POLL_RESPONSE,

    /**
     * Quiz completion
     */
    QUIZ_COMPLETE,

    /**
     * Assignment submission
     */
    ASSIGNMENT_SUBMIT,

    /**
     * Discussion participation
     */
    DISCUSSION_PARTICIPATE,

    /**
     * Breakout room participation
     */
    BREAKOUT_PARTICIPATE,

    /**
     * Whiteboard contribution
     */
    WHITEBOARD_CONTRIBUTE,

    /**
     * Chat message
     */
    CHAT_MESSAGE,

    /**
     * Video on time
     */
    VIDEO_ON,

    /**
     * Raise hand
     */
    RAISE_HAND
}
