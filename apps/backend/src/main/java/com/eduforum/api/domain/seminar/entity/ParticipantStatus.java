package com.eduforum.api.domain.seminar.entity;

/**
 * Participant status in seminar room
 */
public enum ParticipantStatus {
    WAITING,    // In waiting room
    JOINED,     // Joined the active session
    LEFT        // Left the session
}
