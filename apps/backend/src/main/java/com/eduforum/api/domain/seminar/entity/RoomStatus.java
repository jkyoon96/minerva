package com.eduforum.api.domain.seminar.entity;

/**
 * Seminar room status enum
 */
public enum RoomStatus {
    WAITING,    // Waiting room / before session starts
    ACTIVE,     // Session is live and active
    ENDED       // Session has ended
}
