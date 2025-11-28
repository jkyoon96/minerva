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
        log.info("Login attempt for email: {}", request.getEmail());

        // 사용자 조회
        User user = userRepository.findActiveByEmail(request.getEmail())
            .orElseThrow(() -> new BusinessException(ErrorCode.AUTHENTICATION_FAILED, "이메일 또는 비밀번호가 올바르지 않습니다"));

        // 비밀번호 검증
        if (!passwordEncoder.matches(request.getPassword(), user.getPasswordHash())) {
            throw new BusinessException(ErrorCode.AUTHENTICATION_FAILED, "이메일 또는 비밀번호가 올바르지 않습니다");
        }

        // 사용자 상태 확인
        if (user.getStatus() == UserStatus.SUSPENDED) {
            throw new BusinessException(ErrorCode.ACCESS_DENIED, "정지된 계정입니다");
        }

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
