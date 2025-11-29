package com.eduforum.api.domain.active.entity;

/**
 * Poll status enumeration
 */
public enum PollStatus {
    DRAFT,    // Created but not activated
    ACTIVE,   // Currently accepting responses
    CLOSED    // No longer accepting responses
}
