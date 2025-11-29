package com.eduforum.api.domain.auth.service;

import com.eduforum.api.common.email.EmailService;
import com.eduforum.api.common.exception.BusinessException;
import com.eduforum.api.common.exception.ErrorCode;
import com.eduforum.api.domain.auth.dto.LoginRequest;
import com.eduforum.api.domain.auth.dto.LoginResponse;
import com.eduforum.api.domain.auth.dto.RegisterRequest;
import com.eduforum.api.domain.auth.dto.UserProfileResponse;
import com.eduforum.api.domain.auth.entity.Role;
import com.eduforum.api.domain.auth.entity.User;
import com.eduforum.api.domain.auth.entity.UserRole;
import com.eduforum.api.domain.auth.entity.UserStatus;
import com.eduforum.api.domain.auth.repository.*;
import com.eduforum.api.security.jwt.JwtTokenProvider;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.OffsetDateTime;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

/**
 * Unit tests for AuthService
 * Tests user registration, login, and authentication flows
 */
@ExtendWith(MockitoExtension.class)
class AuthServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private RoleRepository roleRepository;

    @Mock
    private UserRoleRepository userRoleRepository;

    @Mock
    private RefreshTokenRepository refreshTokenRepository;

    @Mock
    private EmailVerificationTokenRepository emailVerificationTokenRepository;

    @Mock
    private TwoFactorSecretRepository twoFactorSecretRepository;

    @Mock
    private LoginAttemptService loginAttemptService;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private JwtTokenProvider jwtTokenProvider;

    @Mock
    private EmailService emailService;

    @InjectMocks
    private AuthService authService;

    private User testUser;
    private Role studentRole;

    @BeforeEach
    void setUp() {
        // Setup test data
        studentRole = Role.builder()
            .id(1L)
            .name("STUDENT")
            .description("Student role")
            .build();

        testUser = User.builder()
            .id(1L)
            .email("test@minerva.edu")
            .passwordHash("$2a$10$hashedPassword")
            .firstName("Test")
            .lastName("User")
            .status(UserStatus.ACTIVE)
            .emailVerified(true)
            .build();
    }

    @Test
    @DisplayName("회원가입 성공 - 유효한 정보로 회원가입")
    void register_Success_WithValidData() {
        // Given
        RegisterRequest request = RegisterRequest.builder()
            .email("newuser@minerva.edu")
            .password("Password123!")
            .passwordConfirm("Password123!")
            .name("홍길동")
            .role("STUDENT")
            .termsAgreed(true)
            .privacyAgreed(true)
            .build();

        when(userRepository.existsByEmail(request.getEmail())).thenReturn(false);
        when(roleRepository.findByName("STUDENT")).thenReturn(Optional.of(studentRole));
        when(passwordEncoder.encode(anyString())).thenReturn("$2a$10$encodedPassword");
        when(userRepository.save(any(User.class))).thenAnswer(invocation -> {
            User user = invocation.getArgument(0);
            user.setId(1L);
            return user;
        });
        when(userRoleRepository.save(any(UserRole.class))).thenReturn(mock(UserRole.class));
        when(emailVerificationTokenRepository.save(any())).thenReturn(null);

        // When
        UserProfileResponse response = authService.register(request);

        // Then
        assertThat(response).isNotNull();
        assertThat(response.getEmail()).isEqualTo(request.getEmail());
        verify(userRepository).save(any(User.class));
        verify(userRoleRepository).save(any(UserRole.class));
        verify(emailVerificationTokenRepository).save(any());
    }

    @Test
    @DisplayName("회원가입 실패 - 이메일 중복")
    void register_Fail_DuplicateEmail() {
        // Given
        RegisterRequest request = RegisterRequest.builder()
            .email("existing@minerva.edu")
            .password("Password123!")
            .passwordConfirm("Password123!")
            .name("홍길동")
            .role("STUDENT")
            .termsAgreed(true)
            .privacyAgreed(true)
            .build();

        when(userRepository.existsByEmail(request.getEmail())).thenReturn(true);

        // When & Then
        assertThatThrownBy(() -> authService.register(request))
            .isInstanceOf(BusinessException.class)
            .hasFieldOrPropertyWithValue("errorCode", ErrorCode.DUPLICATE_EMAIL);

        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    @DisplayName("회원가입 실패 - 비밀번호 불일치")
    void register_Fail_PasswordMismatch() {
        // Given
        RegisterRequest request = RegisterRequest.builder()
            .email("newuser@minerva.edu")
            .password("Password123!")
            .passwordConfirm("DifferentPassword!")
            .name("홍길동")
            .role("STUDENT")
            .termsAgreed(true)
            .privacyAgreed(true)
            .build();

        // When & Then
        assertThatThrownBy(() -> authService.register(request))
            .isInstanceOf(BusinessException.class)
            .extracting("message")
            .asString()
            .contains("비밀번호가 일치하지 않습니다");

        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    @DisplayName("회원가입 실패 - 이용약관 미동의")
    void register_Fail_TermsNotAgreed() {
        // Given
        RegisterRequest request = RegisterRequest.builder()
            .email("newuser@minerva.edu")
            .password("Password123!")
            .passwordConfirm("Password123!")
            .name("홍길동")
            .role("STUDENT")
            .termsAgreed(false)
            .privacyAgreed(true)
            .build();

        // When & Then
        assertThatThrownBy(() -> authService.register(request))
            .isInstanceOf(BusinessException.class)
            .extracting("message")
            .asString()
            .contains("이용약관");

        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    @DisplayName("로그인 성공 - 유효한 자격증명")
    void login_Success_WithValidCredentials() {
        // Given
        LoginRequest request = LoginRequest.builder()
            .email("test@minerva.edu")
            .password("Password123!")
            .build();

        when(loginAttemptService.isAccountLocked(anyString())).thenReturn(false);
        when(userRepository.findActiveByEmail(request.getEmail())).thenReturn(Optional.of(testUser));
        when(passwordEncoder.matches(anyString(), anyString())).thenReturn(true);
        when(twoFactorSecretRepository.findByUser(any())).thenReturn(Optional.empty());
        when(jwtTokenProvider.createAccessToken(any())).thenReturn("access-token");
        when(jwtTokenProvider.createRefreshToken(any())).thenReturn("refresh-token");
        when(refreshTokenRepository.save(any())).thenReturn(null);
        when(userRepository.save(any(User.class))).thenReturn(testUser);

        // When
        LoginResponse response = authService.login(request, "127.0.0.1");

        // Then
        assertThat(response).isNotNull();
        assertThat(response.getAccessToken()).isEqualTo("access-token");
        assertThat(response.getRefreshToken()).isEqualTo("refresh-token");
        assertThat(response.getTwoFactorRequired()).isFalse();
        assertThat(response.getUser()).isNotNull();
        assertThat(response.getUser().getEmail()).isEqualTo(testUser.getEmail());

        verify(loginAttemptService).recordAttempt(anyString(), anyString(), eq(true), any(), any());
        verify(loginAttemptService).clearFailedAttempts(anyString());
    }

    @Test
    @DisplayName("로그인 실패 - 존재하지 않는 사용자")
    void login_Fail_UserNotFound() {
        // Given
        LoginRequest request = LoginRequest.builder()
            .email("nonexistent@minerva.edu")
            .password("Password123!")
            .build();

        when(loginAttemptService.isAccountLocked(anyString())).thenReturn(false);
        when(userRepository.findActiveByEmail(request.getEmail())).thenReturn(Optional.empty());

        // When & Then
        assertThatThrownBy(() -> authService.login(request, "127.0.0.1"))
            .isInstanceOf(BusinessException.class)
            .hasFieldOrPropertyWithValue("errorCode", ErrorCode.AUTHENTICATION_FAILED);

        verify(loginAttemptService).recordAttempt(anyString(), anyString(), eq(false), eq("USER_NOT_FOUND"), isNull());
    }

    @Test
    @DisplayName("로그인 실패 - 잘못된 비밀번호")
    void login_Fail_InvalidPassword() {
        // Given
        LoginRequest request = LoginRequest.builder()
            .email("test@minerva.edu")
            .password("WrongPassword!")
            .build();

        when(loginAttemptService.isAccountLocked(anyString())).thenReturn(false);
        when(userRepository.findActiveByEmail(request.getEmail())).thenReturn(Optional.of(testUser));
        when(passwordEncoder.matches(anyString(), anyString())).thenReturn(false);
        when(loginAttemptService.getRemainingAttempts(anyString())).thenReturn(4);

        // When & Then
        assertThatThrownBy(() -> authService.login(request, "127.0.0.1"))
            .isInstanceOf(BusinessException.class)
            .hasFieldOrPropertyWithValue("errorCode", ErrorCode.AUTHENTICATION_FAILED);

        verify(loginAttemptService).recordAttempt(anyString(), anyString(), eq(false), eq("INVALID_PASSWORD"), any());
    }

    @Test
    @DisplayName("로그인 실패 - 계정 잠김")
    void login_Fail_AccountLocked() {
        // Given
        LoginRequest request = LoginRequest.builder()
            .email("test@minerva.edu")
            .password("Password123!")
            .build();

        OffsetDateTime lockoutExpiry = OffsetDateTime.now().plusMinutes(30);
        when(loginAttemptService.isAccountLocked(anyString())).thenReturn(true);
        when(loginAttemptService.getLockoutExpiryTime(anyString())).thenReturn(lockoutExpiry);

        // When & Then
        assertThatThrownBy(() -> authService.login(request, "127.0.0.1"))
            .isInstanceOf(BusinessException.class)
            .hasFieldOrPropertyWithValue("errorCode", ErrorCode.ACCESS_DENIED)
            .extracting("message")
            .asString()
            .contains("계정이 잠겼습니다");

        verify(loginAttemptService).recordAttempt(anyString(), anyString(), eq(false), eq("ACCOUNT_LOCKED"), isNull());
    }

    @Test
    @DisplayName("로그인 실패 - 정지된 계정")
    void login_Fail_SuspendedAccount() {
        // Given
        LoginRequest request = LoginRequest.builder()
            .email("test@minerva.edu")
            .password("Password123!")
            .build();

        User suspendedUser = User.builder()
            .id(1L)
            .email("test@minerva.edu")
            .passwordHash("$2a$10$hashedPassword")
            .firstName("Test")
            .lastName("User")
            .status(UserStatus.SUSPENDED)
            .build();

        when(loginAttemptService.isAccountLocked(anyString())).thenReturn(false);
        when(userRepository.findActiveByEmail(request.getEmail())).thenReturn(Optional.of(suspendedUser));
        when(passwordEncoder.matches(anyString(), anyString())).thenReturn(true);

        // When & Then
        assertThatThrownBy(() -> authService.login(request, "127.0.0.1"))
            .isInstanceOf(BusinessException.class)
            .hasFieldOrPropertyWithValue("errorCode", ErrorCode.ACCESS_DENIED)
            .extracting("message")
            .asString()
            .contains("정지된 계정입니다");

        verify(loginAttemptService).recordAttempt(anyString(), anyString(), eq(false), eq("ACCOUNT_SUSPENDED"), any());
    }

    @Test
    @DisplayName("로그아웃 성공")
    void logout_Success() {
        // Given
        Long userId = 1L;
        String refreshToken = "refresh-token";

        // When
        authService.logout(userId, refreshToken);

        // Then
        verify(refreshTokenRepository).findByTokenHash(anyString());
    }
}
