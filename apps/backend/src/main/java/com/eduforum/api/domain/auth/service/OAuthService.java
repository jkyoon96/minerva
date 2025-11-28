package com.eduforum.api.domain.auth.service;

import com.eduforum.api.common.exception.BusinessException;
import com.eduforum.api.common.exception.ErrorCode;
import com.eduforum.api.domain.auth.dto.LoginResponse;
import com.eduforum.api.domain.auth.dto.OAuthLoginRequest;
import com.eduforum.api.domain.auth.entity.*;
import com.eduforum.api.domain.auth.repository.*;
import com.eduforum.api.security.jwt.JwtTokenProvider;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.time.OffsetDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class OAuthService {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final UserRoleRepository userRoleRepository;
    private final OAuthAccountRepository oauthAccountRepository;
    private final RefreshTokenRepository refreshTokenRepository;
    private final JwtTokenProvider jwtTokenProvider;
    private final RestTemplate restTemplate = new RestTemplate();

    @Value("${oauth2.google.client-id:}")
    private String googleClientId;

    @Value("${oauth2.google.client-secret:}")
    private String googleClientSecret;

    @Value("${oauth2.google.redirect-uri:}")
    private String googleRedirectUri;

    @Value("${oauth2.microsoft.client-id:}")
    private String microsoftClientId;

    @Value("${oauth2.microsoft.client-secret:}")
    private String microsoftClientSecret;

    @Value("${oauth2.microsoft.redirect-uri:}")
    private String microsoftRedirectUri;

    /**
     * Google OAuth 로그인 처리
     */
    @Transactional
    public LoginResponse handleGoogleLogin(OAuthLoginRequest request) {
        log.info("Google OAuth login request");

        // TODO: 실제 Google OAuth API 호출하여 사용자 정보 가져오기
        // 현재는 샘플 구현
        Map<String, Object> userInfo = fetchGoogleUserInfo(request.getCode());

        String providerUserId = (String) userInfo.get("id");
        String email = (String) userInfo.get("email");
        String name = (String) userInfo.getOrDefault("name", "Google User");

        return processOAuthLogin("google", providerUserId, email, name, userInfo);
    }

    /**
     * Microsoft OAuth 로그인 처리
     */
    @Transactional
    public LoginResponse handleMicrosoftLogin(OAuthLoginRequest request) {
        log.info("Microsoft OAuth login request");

        // TODO: 실제 Microsoft OAuth API 호출하여 사용자 정보 가져오기
        // 현재는 샘플 구현
        Map<String, Object> userInfo = fetchMicrosoftUserInfo(request.getCode());

        String providerUserId = (String) userInfo.get("id");
        String email = (String) userInfo.get("userPrincipalName");
        String name = (String) userInfo.getOrDefault("displayName", "Microsoft User");

        return processOAuthLogin("microsoft", providerUserId, email, name, userInfo);
    }

    /**
     * OAuth 로그인 공통 처리
     */
    private LoginResponse processOAuthLogin(String provider, String providerUserId, String email, String name, Map<String, Object> userInfo) {
        User user;

        // OAuth 계정 조회
        Optional<OAuthAccount> oauthAccountOpt = oauthAccountRepository.findByProviderAndProviderUserId(provider, providerUserId);

        if (oauthAccountOpt.isPresent()) {
            // 기존 OAuth 계정이 있는 경우
            user = oauthAccountOpt.get().getUser();
            log.info("Existing OAuth user found: {}", email);
        } else {
            // 신규 OAuth 계정
            Optional<User> existingUserOpt = userRepository.findActiveByEmail(email);

            if (existingUserOpt.isPresent()) {
                // 이메일로 등록된 사용자가 있는 경우 OAuth 계정 연결
                user = existingUserOpt.get();
                log.info("Linking OAuth account to existing user: {}", email);
            } else {
                // 완전히 새로운 사용자 생성
                String[] nameParts = name.split(" ", 2);
                String firstName = nameParts.length > 1 ? nameParts[1] : nameParts[0];
                String lastName = nameParts.length > 1 ? nameParts[0] : "";

                user = User.builder()
                    .email(email)
                    .firstName(firstName)
                    .lastName(lastName)
                    .status(UserStatus.ACTIVE)
                    .emailVerifiedAt(OffsetDateTime.now()) // OAuth는 이메일 검증 완료로 간주
                    .build();

                user = userRepository.save(user);

                // 기본 역할 할당 (STUDENT)
                Role role = roleRepository.findByName("STUDENT")
                    .orElseGet(() -> {
                        Role newRole = Role.builder()
                            .name("STUDENT")
                            .description("학생 역할")
                            .build();
                        return roleRepository.save(newRole);
                    });

                UserRole userRole = UserRole.builder()
                    .user(user)
                    .role(role)
                    .build();

                userRoleRepository.save(userRole);

                log.info("New user created via OAuth: {}", email);
            }

            // OAuth 계정 정보 저장
            OAuthAccount oauthAccount = OAuthAccount.builder()
                .user(user)
                .provider(provider)
                .providerUserId(providerUserId)
                .accessToken((String) userInfo.get("access_token"))
                .refreshToken((String) userInfo.get("refresh_token"))
                .build();

            oauthAccountRepository.save(oauthAccount);
        }

        // JWT 토큰 생성
        List<UserRole> userRoles = userRoleRepository.findByUser(user);
        List<SimpleGrantedAuthority> authorities = userRoles.stream()
            .map(ur -> new SimpleGrantedAuthority("ROLE_" + ur.getRole().getName()))
            .collect(Collectors.toList());

        Authentication authentication = new UsernamePasswordAuthenticationToken(
            user.getEmail(), null, authorities
        );

        String accessToken = jwtTokenProvider.createAccessToken(authentication);
        String refreshToken = jwtTokenProvider.createRefreshToken(authentication);

        // 리프레시 토큰 저장
        RefreshToken refreshTokenEntity = RefreshToken.builder()
            .tokenHash(hashToken(refreshToken))
            .user(user)
            .expiresAt(OffsetDateTime.now().plusDays(14))
            .build();

        refreshTokenRepository.save(refreshTokenEntity);

        // 마지막 로그인 시간 업데이트
        user.updateLastLogin();
        userRepository.save(user);

        // 응답 생성
        String primaryRole = userRoles.isEmpty() ? "USER" : userRoles.get(0).getRole().getName();

        return LoginResponse.builder()
            .accessToken(accessToken)
            .refreshToken(refreshToken)
            .tokenType("Bearer")
            .expiresIn(3600L)
            .user(LoginResponse.UserInfo.builder()
                .id(user.getId())
                .email(user.getEmail())
                .name(user.getFullName())
                .username(user.getEmail().split("@")[0])
                .role(primaryRole)
                .profileImageUrl(user.getProfileImageUrl())
                .build())
            .build();
    }

    /**
     * Google 사용자 정보 가져오기 (샘플)
     */
    private Map<String, Object> fetchGoogleUserInfo(String code) {
        // TODO: 실제 Google OAuth API 구현
        // 현재는 테스트용 샘플 데이터
        log.warn("Using sample Google user info - implement actual OAuth flow");

        Map<String, Object> userInfo = new HashMap<>();
        userInfo.put("id", "google_" + UUID.randomUUID().toString().substring(0, 8));
        userInfo.put("email", "googleuser@example.com");
        userInfo.put("name", "Google User");
        userInfo.put("access_token", "sample_google_access_token");
        userInfo.put("refresh_token", "sample_google_refresh_token");

        return userInfo;
    }

    /**
     * Microsoft 사용자 정보 가져오기 (샘플)
     */
    private Map<String, Object> fetchMicrosoftUserInfo(String code) {
        // TODO: 실제 Microsoft OAuth API 구현
        // 현재는 테스트용 샘플 데이터
        log.warn("Using sample Microsoft user info - implement actual OAuth flow");

        Map<String, Object> userInfo = new HashMap<>();
        userInfo.put("id", "microsoft_" + UUID.randomUUID().toString().substring(0, 8));
        userInfo.put("userPrincipalName", "msuser@example.com");
        userInfo.put("displayName", "Microsoft User");
        userInfo.put("access_token", "sample_microsoft_access_token");
        userInfo.put("refresh_token", "sample_microsoft_refresh_token");

        return userInfo;
    }

    /**
     * 토큰 해시 생성 (SHA-256)
     */
    private String hashToken(String token) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(token.getBytes(StandardCharsets.UTF_8));
            return Base64.getEncoder().encodeToString(hash);
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("SHA-256 algorithm not found", e);
        }
    }
}
