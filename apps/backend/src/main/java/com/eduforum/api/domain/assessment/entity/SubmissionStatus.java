package com.eduforum.api.domain.assessment.entity;

/**
 * Code submission status enum
 */
public enum SubmissionStatus {
    /**
     * Submitted but not run
     */
    SUBMITTED,

    /**
     * Currently running
     */
    RUNNING,

    /**
     * Execution completed
     */
    COMPLETED,

    /**
     * Execution error
     */
    ERROR,

    /**
     * Compilation error
     */
    COMPILE_ERROR
}
