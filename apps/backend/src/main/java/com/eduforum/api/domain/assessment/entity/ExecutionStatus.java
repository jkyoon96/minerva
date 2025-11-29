package com.eduforum.api.domain.assessment.entity;

/**
 * Code execution status enum
 */
public enum ExecutionStatus {
    /**
     * Execution successful
     */
    SUCCESS,

    /**
     * Execution failed
     */
    FAILED,

    /**
     * Time limit exceeded
     */
    TIMEOUT,

    /**
     * Memory limit exceeded
     */
    MEMORY_LIMIT,

    /**
     * Runtime error
     */
    RUNTIME_ERROR,

    /**
     * Wrong answer
     */
    WRONG_ANSWER
}
