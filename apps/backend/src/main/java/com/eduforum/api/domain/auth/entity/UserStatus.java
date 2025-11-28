package com.eduforum.api.domain.auth.entity;

/**
 * User account status enum (maps to user_status type in database)
 */
public enum UserStatus {
    ACTIVE,
    INACTIVE,
    SUSPENDED,
    PENDING
}
