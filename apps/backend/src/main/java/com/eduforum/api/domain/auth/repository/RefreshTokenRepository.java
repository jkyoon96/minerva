package com.eduforum.api.domain.auth.repository;

import com.eduforum.api.domain.auth.entity.RefreshToken;
import com.eduforum.api.domain.auth.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.Optional;

/**
 * Repository for RefreshToken entity
 */
@Repository
public interface RefreshTokenRepository extends JpaRepository<RefreshToken, Long> {

    /**
     * Find refresh token by token hash
     */
    Optional<RefreshToken> findByTokenHash(String tokenHash);

    /**
     * Find valid (not expired and not revoked) token by hash
     */
    @Query("SELECT rt FROM RefreshToken rt WHERE rt.tokenHash = :tokenHash " +
           "AND rt.expiresAt > :now AND rt.revokedAt IS NULL")
    Optional<RefreshToken> findValidByTokenHash(
        @Param("tokenHash") String tokenHash,
        @Param("now") OffsetDateTime now
    );

    /**
     * Find all valid tokens for a user
     */
    @Query("SELECT rt FROM RefreshToken rt WHERE rt.user = :user " +
           "AND rt.expiresAt > :now AND rt.revokedAt IS NULL")
    List<RefreshToken> findValidTokensByUser(
        @Param("user") User user,
        @Param("now") OffsetDateTime now
    );

    /**
     * Find all tokens for a user
     */
    List<RefreshToken> findByUser(User user);

    /**
     * Delete expired tokens
     */
    void deleteByExpiresAtBefore(OffsetDateTime dateTime);

    /**
     * Delete all tokens for a user
     */
    void deleteByUser(User user);
}
