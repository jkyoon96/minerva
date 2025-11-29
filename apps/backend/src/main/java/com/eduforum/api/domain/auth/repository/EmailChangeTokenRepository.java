package com.eduforum.api.domain.auth.repository;

import com.eduforum.api.domain.auth.entity.EmailChangeToken;
import com.eduforum.api.domain.auth.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.Optional;

/**
 * Repository for EmailChangeToken entity
 */
@Repository
public interface EmailChangeTokenRepository extends JpaRepository<EmailChangeToken, Long> {

    /**
     * Find token by token string
     */
    Optional<EmailChangeToken> findByToken(String token);

    /**
     * Find all unused tokens by user
     */
    @Query("SELECT e FROM EmailChangeToken e WHERE e.user = :user AND e.used = false")
    List<EmailChangeToken> findUnusedByUser(User user);

    /**
     * Find valid (unused and not expired) token by user
     */
    @Query("SELECT e FROM EmailChangeToken e WHERE e.user = :user AND e.used = false AND e.expiresAt > :now ORDER BY e.createdAt DESC")
    Optional<EmailChangeToken> findValidByUser(User user, OffsetDateTime now);

    /**
     * Delete expired tokens
     */
    void deleteByExpiresAtBefore(OffsetDateTime expirationDate);

    /**
     * Check if there's a pending email change for a specific new email
     */
    @Query("SELECT COUNT(e) > 0 FROM EmailChangeToken e WHERE e.newEmail = :email AND e.used = false AND e.expiresAt > :now")
    boolean existsPendingChangeForEmail(String email, OffsetDateTime now);
}
