package com.eduforum.api.domain.auth.service;

import com.eduforum.api.domain.auth.entity.LoginAttempt;
import com.eduforum.api.domain.auth.entity.User;
import com.eduforum.api.domain.auth.repository.LoginAttemptRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.OffsetDateTime;
import java.util.List;

/**
 * Service for managing login attempts and account locking logic
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class LoginAttemptService {

    private final LoginAttemptRepository loginAttemptRepository;

    // Configuration constants
    private static final int MAX_FAILED_ATTEMPTS = 5;
    private static final int LOCKOUT_DURATION_MINUTES = 15;
    private static final int ATTEMPT_WINDOW_MINUTES = 15;

    /**
     * Record a login attempt
     */
    @Transactional
    public void recordAttempt(String email, String ipAddress, boolean success, String failureReason, User user) {
        log.info("Recording login attempt for email: {}, success: {}", email, success);

        LoginAttempt attempt = LoginAttempt.builder()
            .email(email)
            .ipAddress(ipAddress)
            .attemptTime(OffsetDateTime.now())
            .success(success)
            .failureReason(failureReason)
            .user(user)
            .build();

        loginAttemptRepository.save(attempt);
    }

    /**
     * Check if account is locked due to failed login attempts
     */
    @Transactional(readOnly = true)
    public boolean isAccountLocked(String email) {
        OffsetDateTime lockoutWindowStart = OffsetDateTime.now().minusMinutes(LOCKOUT_DURATION_MINUTES);

        List<LoginAttempt> recentFailedAttempts =
            loginAttemptRepository.findRecentFailedAttemptsByEmail(email, lockoutWindowStart);

        if (recentFailedAttempts.isEmpty()) {
            return false;
        }

        // Check if there are MAX_FAILED_ATTEMPTS consecutive failures within the window
        int consecutiveFailures = 0;
        OffsetDateTime attemptWindowStart = OffsetDateTime.now().minusMinutes(ATTEMPT_WINDOW_MINUTES);

        for (LoginAttempt attempt : recentFailedAttempts) {
            if (attempt.getAttemptTime().isAfter(attemptWindowStart)) {
                consecutiveFailures++;
                if (consecutiveFailures >= MAX_FAILED_ATTEMPTS) {
                    log.warn("Account locked for email: {} due to {} failed attempts", email, consecutiveFailures);
                    return true;
                }
            }
        }

        return false;
    }

    /**
     * Get the number of failed login attempts within the attempt window
     */
    @Transactional(readOnly = true)
    public int getFailedAttempts(String email) {
        OffsetDateTime windowStart = OffsetDateTime.now().minusMinutes(ATTEMPT_WINDOW_MINUTES);
        Long count = loginAttemptRepository.countFailedAttemptsByEmail(email, windowStart);
        return count != null ? count.intValue() : 0;
    }

    /**
     * Get remaining attempts before lockout
     */
    @Transactional(readOnly = true)
    public int getRemainingAttempts(String email) {
        int failedAttempts = getFailedAttempts(email);
        int remaining = MAX_FAILED_ATTEMPTS - failedAttempts;
        return Math.max(0, remaining);
    }

    /**
     * Clear failed attempts for a user (called on successful login)
     */
    @Transactional
    public void clearFailedAttempts(String email) {
        log.info("Clearing failed attempts for email: {}", email);
        // Note: We don't delete attempts, we just let them age out
        // The recent attempts check already filters by time window
    }

    /**
     * Get lockout expiry time if account is locked
     */
    @Transactional(readOnly = true)
    public OffsetDateTime getLockoutExpiryTime(String email) {
        if (!isAccountLocked(email)) {
            return null;
        }

        OffsetDateTime lockoutWindowStart = OffsetDateTime.now().minusMinutes(LOCKOUT_DURATION_MINUTES);
        List<LoginAttempt> recentFailedAttempts =
            loginAttemptRepository.findRecentFailedAttemptsByEmail(email, lockoutWindowStart);

        if (recentFailedAttempts.isEmpty()) {
            return null;
        }

        // Find the earliest failed attempt in the recent window
        OffsetDateTime earliestFailure = recentFailedAttempts.stream()
            .map(LoginAttempt::getAttemptTime)
            .min(OffsetDateTime::compareTo)
            .orElse(null);

        if (earliestFailure != null) {
            return earliestFailure.plusMinutes(LOCKOUT_DURATION_MINUTES);
        }

        return null;
    }

    /**
     * Get recent login attempts for a user (for audit/security purposes)
     */
    @Transactional(readOnly = true)
    public List<LoginAttempt> getRecentAttempts(String email, int limit) {
        List<LoginAttempt> attempts = loginAttemptRepository.findByEmailOrderByAttemptTimeDesc(email);
        return attempts.stream().limit(limit).toList();
    }

    /**
     * Clean up old login attempts (to be called periodically)
     */
    @Transactional
    public void cleanupOldAttempts(int daysToKeep) {
        OffsetDateTime cutoffDate = OffsetDateTime.now().minusDays(daysToKeep);
        loginAttemptRepository.deleteByAttemptTimeBefore(cutoffDate);
        log.info("Cleaned up login attempts older than {} days", daysToKeep);
    }

    /**
     * Get configuration values
     */
    public int getMaxFailedAttempts() {
        return MAX_FAILED_ATTEMPTS;
    }

    public int getLockoutDurationMinutes() {
        return LOCKOUT_DURATION_MINUTES;
    }
}
