package com.eduforum.api.domain.active.entity;

/**
 * Breakout room status enumeration
 */
public enum BreakoutStatus {
    WAITING,  // Created but not started
    ACTIVE,   // Currently running
    CLOSED    // Ended
}
