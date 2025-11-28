package com.eduforum.api.domain.seminar.entity;

/**
 * Participant role in seminar room
 */
public enum ParticipantRole {
    HOST,           // Professor/instructor who created the room
    CO_HOST,        // Teaching assistant or co-instructor
    PARTICIPANT     // Student participant
}
