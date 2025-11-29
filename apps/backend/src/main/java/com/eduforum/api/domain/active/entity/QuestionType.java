package com.eduforum.api.domain.active.entity;

/**
 * Question type enumeration for quiz questions
 */
public enum QuestionType {
    MULTIPLE_CHOICE,  // Single correct answer from options
    TRUE_FALSE,       // Boolean question
    SHORT_ANSWER,     // Short text answer
    ESSAY             // Long text answer
}
