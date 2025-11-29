package com.eduforum.api.domain.auth.repository;

import com.eduforum.api.domain.auth.entity.LoginAttempt;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.OffsetDateTime;
import java.util.List;

/**
 * Repository for login attempts
 */
@Repository
public interface LoginAttemptRepository extends JpaRepository<LoginAttempt, Long> {

    /**
     * Find recent failed login attempts by email within a time window
     */
    @Query("SELECT la FROM LoginAttempt la WHERE la.email = :email " +
           "AND la.success = false " +
           "AND la.attemptTime >= :since " +
           "ORDER BY la.attemptTime DESC")
    List<LoginAttempt> findRecentFailedAttemptsByEmail(
        @Param("email") String email,
        @Param("since") OffsetDateTime since
    );

    /**
     * Find recent failed login attempts by IP address within a time window
     */
    @Query("SELECT la FROM LoginAttempt la WHERE la.ipAddress = :ipAddress " +
           "AND la.success = false " +
           "AND la.attemptTime >= :since " +
           "ORDER BY la.attemptTime DESC")
    List<LoginAttempt> findRecentFailedAttemptsByIpAddress(
        @Param("ipAddress") String ipAddress,
        @Param("since") OffsetDateTime since
    );

    /**
     * Count failed login attempts by email within a time window
     */
    @Query("SELECT COUNT(la) FROM LoginAttempt la WHERE la.email = :email " +
           "AND la.success = false " +
           "AND la.attemptTime >= :since")
    Long countFailedAttemptsByEmail(
        @Param("email") String email,
        @Param("since") OffsetDateTime since
    );

    /**
     * Find all login attempts by email
     */
    List<LoginAttempt> findByEmailOrderByAttemptTimeDesc(String email);

    /**
     * Find login attempts by user ID
     */
    @Query("SELECT la FROM LoginAttempt la WHERE la.user.id = :userId " +
           "ORDER BY la.attemptTime DESC")
    List<LoginAttempt> findByUserIdOrderByAttemptTimeDesc(@Param("userId") Long userId);

    /**
     * Delete old login attempts (for cleanup)
     */
    void deleteByAttemptTimeBefore(OffsetDateTime before);
}
