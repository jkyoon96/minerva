package com.eduforum.api.domain.auth.repository;

import com.eduforum.api.domain.auth.entity.PasswordResetToken;
import com.eduforum.api.domain.auth.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.OffsetDateTime;
import java.util.Optional;

@Repository
public interface PasswordResetTokenRepository extends JpaRepository<PasswordResetToken, Long> {

    Optional<PasswordResetToken> findByToken(String token);

    Optional<PasswordResetToken> findByUserAndUsedAtIsNullAndExpiresAtAfter(User user, OffsetDateTime now);

    void deleteByExpiresAtBefore(OffsetDateTime now);
}
