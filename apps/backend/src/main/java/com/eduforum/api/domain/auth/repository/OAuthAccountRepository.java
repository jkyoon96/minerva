package com.eduforum.api.domain.auth.repository;

import com.eduforum.api.domain.auth.entity.OAuthAccount;
import com.eduforum.api.domain.auth.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface OAuthAccountRepository extends JpaRepository<OAuthAccount, Long> {

    Optional<OAuthAccount> findByProviderAndProviderUserId(String provider, String providerUserId);

    Optional<OAuthAccount> findByUserAndProvider(User user, String provider);

    boolean existsByProviderAndProviderUserId(String provider, String providerUserId);
}
