package com.eduforum.api.domain.assessment.entity;

/**
 * Grading type enum
 */
public enum GradingType {
    /**
     * Automatic grading (multiple choice)
     */
    AUTO,

    /**
     * AI-based grading (essay, short answer)
     */
    AI,

    /**
     * Manual grading by professor
     */
    MANUAL,

    /**
     * Peer review grading
     */
    PEER
}
