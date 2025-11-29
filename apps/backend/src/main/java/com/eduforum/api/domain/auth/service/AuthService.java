package com.eduforum.api.domain.auth.service;

import com.eduforum.api.common.exception.BusinessException;
import com.eduforum.api.common.exception.ErrorCode;
import com.eduforum.api.domain.auth.dto.*;
import com.eduforum.api.domain.auth.entity.*;
import com.eduforum.api.domain.auth.repository.*;
import com.eduforum.api.security.jwt.JwtTokenProvider;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.time.OffsetDateTime;
import java.util.Base64;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final UserRoleRepository userRoleRepository;
    private final RefreshTokenRepository refreshTokenRepository;
    private final EmailVerificationTokenRepository emailVerificationTokenRepository;
    private final TwoFactorSecretRepository twoFactorSecretRepository;
    private final LoginAttemptService loginAttemptService;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenProvider jwtTokenProvider;

    /**
     * 회원가입
     */
    @Transactional
    public UserProfileResponse register(RegisterRequest request) {
        log.info("Registering new user: {}", request.getEmail());

        // 비밀번호 확인 검증
        if (!request.getPassword().equals(request.getPasswordConfirm())) {
            throw new BusinessException(ErrorCode.INVALID_INPUT_VALUE, "비밀번호가 일치하지 않습니다");
        }

        // 이메일 중복 검사
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new BusinessException(ErrorCode.DUPLICATE_EMAIL);
        }

        // 이용약관 동의 확인
        if (!Boolean.TRUE.equals(request.getTermsAgreed()) || !Boolean.TRUE.equals(request.getPrivacyAgreed())) {
            throw new BusinessException(ErrorCode.INVALID_INPUT_VALUE, "이용약관 및 개인정보 처리방침에 동의해야 합니다");
        }

        // 사용자 생성
        String[] nameParts = request.getName().split(" ", 2);
        String firstName = nameParts.length > 1 ? nameParts[1] : nameParts[0];
        String lastName = nameParts.length > 1 ? nameParts[0] : "";

        User user = User.builder()
            .email(request.getEmail())
            .passwordHash(passwordEncoder.encode(request.getPassword()))
            .firstName(firstName)
            .lastName(lastName)
            .status(UserStatus.PENDING)
            .build();

        user = userRepository.save(user);

        // 역할 할당
        Role role = roleRepository.findByName(request.getRole())
            .orElseThrow(() -> new BusinessException(ErrorCode.ENTITY_NOT_FOUND, "역할을 찾을 수 없습니다"));

        UserRole userRole = UserRole.builder()
            .user(user)
            .role(role)
            .build();

        userRoleRepository.save(userRole);

        // 이메일 인증 토큰 생성
        String verificationToken = UUID.randomUUID().toString();
        EmailVerificationToken emailToken = EmailVerificationToken.builder()
            .token(verificationToken)
            .user(user)
            .expiresAt(OffsetDateTime.now().plusDays(1))
            .build();

        emailVerificationTokenRepository.save(emailToken);

        log.info("User registered successfully: {} (ID: {})", user.getEmail(), user.getId());
        // TODO: 이메일 발송 (인증 링크)

        return mapToUserProfileResponse(user);
    }

    /**
     * 로그인
     */
    @Transactional
    public LoginResponse login(LoginRequest request) {
        return login(request, "unknown");
    }

    /**
     * 로그인 (IP 주소 포함)
     */
    @Transactional
    public LoginResponse login(LoginRequest request, String ipAddress) {
        log.info("Login attempt for email: {} from IP: {}", request.getEmail(), ipAddress);

        // 계정 잠금 확인
        if (loginAttemptService.isAccountLocked(request.getEmail())) {
            OffsetDateTime lockoutExpiry = loginAttemptService.getLockoutExpiryTime(request.getEmail());
            loginAttemptService.recordAttempt(request.getEmail(), ipAddress, false, "ACCOUNT_LOCKED", null);
            throw new BusinessException(ErrorCode.ACCESS_DENIED,
                String.format("계정이 잠겼습니다. %s 이후 다시 시도해주세요.", lockoutExpiry));
        }

        // 사용자 조회
        User user = userRepository.findActiveByEmail(request.getEmail()).orElse(null);

        if (user == null) {
            loginAttemptService.recordAttempt(request.getEmail(), ipAddress, false, "USER_NOT_FOUND", null);
            throw new BusinessException(ErrorCode.AUTHENTICATION_FAILED, "이메일 또는 비밀번호가 올바르지 않습니다");
        }

        // 비밀번호 검증
        if (!passwordEncoder.matches(request.getPassword(), user.getPasswordHash())) {
            loginAttemptService.recordAttempt(request.getEmail(), ipAddress, false, "INVALID_PASSWORD", user);
            int remainingAttempts = loginAttemptService.getRemainingAttempts(request.getEmail());
            throw new BusinessException(ErrorCode.AUTHENTICATION_FAILED,
                String.format("이메일 또는 비밀번호가 올바르지 않습니다. (남은 시도: %d회)", remainingAttempts));
        }

        // 사용자 상태 확인
        if (user.getStatus() == UserStatus.SUSPENDED) {
            loginAttemptService.recordAttempt(request.getEmail(), ipAddress, false, "ACCOUNT_SUSPENDED", user);
            throw new BusinessException(ErrorCode.ACCESS_DENIED, "정지된 계정입니다");
        }

        // 2FA 활성화 확인
        TwoFactorSecret twoFactorSecret = twoFactorSecretRepository.findByUser(user).orElse(null);
        if (twoFactorSecret != null && twoFactorSecret.isEnabled()) {
            // 2FA가 활성화된 경우, 임시 토큰 발급
            String temporaryToken = generateTemporaryToken(user);

            log.info("2FA required for user: {} (ID: {})", user.getEmail(), user.getId());

            return LoginResponse.builder()
                .twoFactorRequired(true)
                .temporaryToken(temporaryToken)
                .tokenType("Bearer")
                .user(LoginResponse.UserInfo.builder()
                    .id(user.getId())
                    .email(user.getEmail())
                    .name(user.getFullName())
                    .username(user.getEmail().split("@")[0])
                    .build())
                .build();
        }

        // 2FA가 비활성화된 경우, 일반 로그인 진행
        return completeLogin(user, ipAddress);
    }

    /**
     * 2FA 검증 후 로그인 완료
     */
    @Transactional
    public LoginResponse completeTwoFactorLogin(TwoFactorLoginRequest request, TwoFactorService twoFactorService) {
        log.info("Completing 2FA login with temporary token");

        // 임시 토큰 검증 및 사용자 조회
        User user = validateTemporaryTokenAndGetUser(request.getTemporaryToken());

        // 2FA 시크릿 조회
        TwoFactorSecret twoFactorSecret = twoFactorSecretRepository.findByUser(user)
            .orElseThrow(() -> new BusinessException(ErrorCode.AUTHENTICATION_FAILED, "2FA가 설정되지 않았습니다"));

        if (!twoFactorSecret.isEnabled()) {
            throw new BusinessException(ErrorCode.AUTHENTICATION_FAILED, "2FA가 활성화되어 있지 않습니다");
        }

        // TOTP 코드 또는 백업 코드 검증
        boolean verified = false;
        if (request.getCode() != null && !request.getCode().isEmpty()) {
            verified = twoFactorService.verifyCode(twoFactorSecret.getSecret(), request.getCode());
        } else if (request.getBackupCode() != null && !request.getBackupCode().isEmpty()) {
            verified = twoFactorService.verifyAndUseBackupCode(user.getId(), request.getBackupCode());
        }

        if (!verified) {
            throw new BusinessException(ErrorCode.AUTHENTICATION_FAILED, "잘못된 인증 코드입니다");
        }

        log.info("2FA verification successful for user: {} (ID: {})", user.getEmail(), user.getId());

        // 로그인 완료
        return completeLogin(user);
    }

    /**
     * 로그인 완료 처리 (공통 로직)
     */
    private LoginResponse completeLogin(User user) {
        return completeLogin(user, "unknown");
    }

    /**
     * 로그인 완료 처리 (IP 주소 포함)
     */
    private LoginResponse completeLogin(User user, String ipAddress) {
        // 성공한 로그인 시도 기록
        loginAttemptService.recordAttempt(user.getEmail(), ipAddress, true, null, user);
        loginAttemptService.clearFailedAttempts(user.getEmail());

        // 역할 조회
        List<UserRole> userRoles = userRoleRepository.findByUser(user);
        List<SimpleGrantedAuthority> authorities = userRoles.stream()
            .map(ur -> new SimpleGrantedAuthority("ROLE_" + ur.getRole().getName()))
            .collect(Collectors.toList());

        // JWT 토큰 생성
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

        log.info("User logged in successfully: {} (ID: {})", user.getEmail(), user.getId());

        // 응답 생성
        String primaryRole = userRoles.isEmpty() ? "USER" : userRoles.get(0).getRole().getName();

        return LoginResponse.builder()
            .accessToken(accessToken)
            .refreshToken(refreshToken)
            .tokenType("Bearer")
            .expiresIn(3600L)
            .twoFactorRequired(false)
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
     * 임시 토큰 생성 (2FA용)
     */
    private String generateTemporaryToken(User user) {
        // 임시 토큰: "2FA:" + userId + ":" + timestamp + ":" + random
        String data = "2FA:" + user.getId() + ":" + System.currentTimeMillis() + ":" + UUID.randomUUID();
        return Base64.getEncoder().encodeToString(data.getBytes(StandardCharsets.UTF_8));
    }

    /**
     * 임시 토큰 검증 및 사용자 조회
     */
    private User validateTemporaryTokenAndGetUser(String temporaryToken) {
        try {
            String decoded = new String(Base64.getDecoder().decode(temporaryToken), StandardCharsets.UTF_8);
            String[] parts = decoded.split(":");

            if (parts.length != 4 || !"2FA".equals(parts[0])) {
                throw new BusinessException(ErrorCode.INVALID_TOKEN, "유효하지 않은 임시 토큰입니다");
            }

            Long userId = Long.parseLong(parts[1]);
            long timestamp = Long.parseLong(parts[2]);

            // 토큰 유효 시간 확인 (5분)
            if (System.currentTimeMillis() - timestamp > 5 * 60 * 1000) {
                throw new BusinessException(ErrorCode.EXPIRED_TOKEN, "임시 토큰이 만료되었습니다");
            }

            return userRepository.findById(userId)
                .orElseThrow(() -> new BusinessException(ErrorCode.ENTITY_NOT_FOUND, "사용자를 찾을 수 없습니다"));
        } catch (IllegalArgumentException e) {
            throw new BusinessException(ErrorCode.INVALID_TOKEN, "유효하지 않은 임시 토큰입니다");
        }
    }

    /**
     * 로그아웃
     */
    @Transactional
    public void logout(Long userId, String refreshToken) {
        log.info("Logout request for user ID: {}", userId);

        if (refreshToken != null && !refreshToken.isEmpty()) {
            String tokenHash = hashToken(refreshToken);
            refreshTokenRepository.findByTokenHash(tokenHash)
                .ifPresent(token -> {
                    token.revoke();
                    refreshTokenRepository.save(token);
                });
        }

        log.info("User logged out successfully: {}", userId);
    }

    /**
     * 토큰 갱신
     */
    @Transactional
    public TokenRefreshResponse refreshToken(String refreshToken) {
        log.info("Token refresh request");

        // 토큰 검증
        if (!jwtTokenProvider.validateToken(refreshToken)) {
            throw new BusinessException(ErrorCode.INVALID_TOKEN);
        }

        // 리프레시 토큰 조회
        String tokenHash = hashToken(refreshToken);
        RefreshToken tokenEntity = refreshTokenRepository.findByTokenHash(tokenHash)
            .orElseThrow(() -> new BusinessException(ErrorCode.INVALID_TOKEN, "리프레시 토큰을 찾을 수 없습니다"));

        // 토큰 만료 확인
        if (tokenEntity.isExpired()) {
            throw new BusinessException(ErrorCode.EXPIRED_TOKEN);
        }

        // 토큰 폐기 확인
        if (tokenEntity.isRevoked()) {
            throw new BusinessException(ErrorCode.INVALID_TOKEN, "폐기된 토큰입니다");
        }

        // 사용자 조회
        User user = tokenEntity.getUser();

        // 역할 조회
        List<UserRole> userRoles = userRoleRepository.findByUser(user);
        List<SimpleGrantedAuthority> authorities = userRoles.stream()
            .map(ur -> new SimpleGrantedAuthority("ROLE_" + ur.getRole().getName()))
            .collect(Collectors.toList());

        // 새 토큰 생성
        Authentication authentication = new UsernamePasswordAuthenticationToken(
            user.getEmail(), null, authorities
        );

        String newAccessToken = jwtTokenProvider.createAccessToken(authentication);
        String newRefreshToken = jwtTokenProvider.createRefreshToken(authentication);

        // 기존 리프레시 토큰 폐기
        tokenEntity.revoke();
        refreshTokenRepository.save(tokenEntity);

        // 새 리프레시 토큰 저장
        RefreshToken newTokenEntity = RefreshToken.builder()
            .tokenHash(hashToken(newRefreshToken))
            .user(user)
            .expiresAt(OffsetDateTime.now().plusDays(14))
            .build();

        refreshTokenRepository.save(newTokenEntity);

        log.info("Token refreshed successfully for user: {}", user.getEmail());

        return TokenRefreshResponse.builder()
            .accessToken(newAccessToken)
            .refreshToken(newRefreshToken)
            .tokenType("Bearer")
            .expiresIn(3600L)
            .build();
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

    /**
     * 이메일 인증
     */
    @Transactional
    public void verifyEmail(String token) {
        log.info("Email verification request with token: {}", token);

        // 토큰 조회
        EmailVerificationToken verificationToken = emailVerificationTokenRepository.findByToken(token)
            .orElseThrow(() -> new BusinessException(ErrorCode.INVALID_TOKEN, "유효하지 않은 인증 토큰입니다"));

        // 토큰 만료 확인
        if (verificationToken.isExpired()) {
            throw new BusinessException(ErrorCode.EXPIRED_TOKEN, "만료된 인증 토큰입니다");
        }

        // 이미 인증된 토큰 확인
        if (verificationToken.isVerified()) {
            throw new BusinessException(ErrorCode.INVALID_INPUT_VALUE, "이미 인증된 토큰입니다");
        }

        // 사용자 이메일 인증 처리
        User user = verificationToken.getUser();
        user.verifyEmail();
        user.setStatus(UserStatus.ACTIVE);
        userRepository.save(user);

        // 토큰 인증 완료 처리
        verificationToken.markAsVerified();
        emailVerificationTokenRepository.save(verificationToken);

        log.info("Email verified successfully for user: {}", user.getEmail());
    }

    /**
     * User를 UserProfileResponse로 변환
     */
    private UserProfileResponse mapToUserProfileResponse(User user) {
        List<String> roleNames = userRoleRepository.findByUser(user).stream()
            .map(ur -> ur.getRole().getName())
            .collect(Collectors.toList());

        return UserProfileResponse.builder()
            .id(user.getId())
            .email(user.getEmail())
            .firstName(user.getFirstName())
            .lastName(user.getLastName())
            .fullName(user.getFullName())
            .profileImageUrl(user.getProfileImageUrl())
            .phone(user.getPhone())
            .status(user.getStatus().name())
            .roles(roleNames)
            .emailVerified(user.isEmailVerified())
            .emailVerifiedAt(user.getEmailVerifiedAt())
            .lastLoginAt(user.getLastLoginAt())
            .createdAt(user.getCreatedAt())
            .build();
    }
}
