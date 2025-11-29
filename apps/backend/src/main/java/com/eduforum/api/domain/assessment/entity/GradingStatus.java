package com.eduforum.api.domain.assessment.entity;

/**
 * Grading status enum
 */
public enum GradingStatus {
    /**
     * Pending grading
     */
    PENDING,

    /**
     * Graded (auto or AI)
     */
    GRADED,

    /**
     * Reviewed by professor
     */
    REVIEWED,

    /**
     * Finalized
     */
    FINALIZED
}
