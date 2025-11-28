package com.eduforum.api.domain.course.entity;

/**
 * Session status enum (maps to session_status type in database)
 */
public enum SessionStatus {
    SCHEDULED,
    LIVE,
    ENDED,
    CANCELLED
}
