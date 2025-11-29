package com.eduforum.api.domain.active.entity;

/**
 * Poll type enumeration
 */
public enum PollType {
    MULTIPLE_CHOICE,  // Single or multiple selection
    RATING,           // Rating scale (e.g., 1-5)
    WORD_CLOUD,       // Free text for word cloud
    OPEN_ENDED        // Free text response
}
