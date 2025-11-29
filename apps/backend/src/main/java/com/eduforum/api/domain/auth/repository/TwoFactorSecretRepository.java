package com.eduforum.api.domain.auth.repository;

import com.eduforum.api.domain.auth.entity.TwoFactorSecret;
import com.eduforum.api.domain.auth.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface TwoFactorSecretRepository extends JpaRepository<TwoFactorSecret, Long> {

    /**
     * Find TwoFactorSecret by user
     */
    Optional<TwoFactorSecret> findByUser(User user);

    /**
     * Find TwoFactorSecret by user ID
     */
    Optional<TwoFactorSecret> findByUserId(Long userId);

    /**
     * Check if user has 2FA enabled
     */
    boolean existsByUserAndIsEnabledTrue(User user);

    /**
     * Delete by user
     */
    void deleteByUser(User user);
}
