package com.eduforum.api.domain.auth.repository;

import com.eduforum.api.domain.auth.entity.EmailVerificationToken;
import com.eduforum.api.domain.auth.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.OffsetDateTime;
import java.util.Optional;

@Repository
public interface EmailVerificationTokenRepository extends JpaRepository<EmailVerificationToken, Long> {

    Optional<EmailVerificationToken> findByToken(String token);

    Optional<EmailVerificationToken> findByUserAndVerifiedAtIsNullAndExpiresAtAfter(User user, OffsetDateTime now);

    void deleteByExpiresAtBefore(OffsetDateTime now);
}
