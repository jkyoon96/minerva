package com.eduforum.api.domain.auth.controller;

import com.eduforum.api.domain.auth.dto.*;
import com.eduforum.api.domain.auth.service.AuthService;
import com.eduforum.api.domain.auth.service.PasswordResetService;
import com.eduforum.api.domain.auth.service.TwoFactorService;
import com.eduforum.api.domain.auth.service.UserService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import java.time.OffsetDateTime;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Controller tests for AuthController
 * Tests REST API endpoints for authentication
 */
@WebMvcTest(AuthController.class)
@ActiveProfiles("test")
class AuthControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private AuthService authService;

    @MockBean
    private UserService userService;

    @MockBean
    private PasswordResetService passwordResetService;

    @MockBean
    private TwoFactorService twoFactorService;

    private RegisterRequest registerRequest;
    private LoginRequest loginRequest;
    private UserProfileResponse userProfileResponse;
    private LoginResponse loginResponse;

    @BeforeEach
    void setUp() {
        // Setup test data
        registerRequest = RegisterRequest.builder()
            .email("test@minerva.edu")
            .password("Password123!")
            .passwordConfirm("Password123!")
            .name("홍길동")
            .role("STUDENT")
            .termsAgreed(true)
            .privacyAgreed(true)
            .build();

        loginRequest = LoginRequest.builder()
            .email("test@minerva.edu")
            .password("Password123!")
            .build();

        userProfileResponse = UserProfileResponse.builder()
            .id(1L)
            .email("test@minerva.edu")
            .firstName("길동")
            .lastName("홍")
            .fullName("홍길동")
            .status("ACTIVE")
            .roles(List.of("STUDENT"))
            .emailVerified(false)
            .createdAt(OffsetDateTime.now())
            .build();

        loginResponse = LoginResponse.builder()
            .accessToken("access-token")
            .refreshToken("refresh-token")
            .tokenType("Bearer")
            .expiresIn(3600L)
            .twoFactorRequired(false)
            .user(LoginResponse.UserInfo.builder()
                .id(1L)
                .email("test@minerva.edu")
                .name("홍길동")
                .username("test")
                .role("STUDENT")
                .build())
            .build();
    }

    @Test
    @DisplayName("POST /v1/auth/register - 회원가입 성공")
    void register_Success() throws Exception {
        // Given
        when(authService.register(any(RegisterRequest.class))).thenReturn(userProfileResponse);

        // When & Then
        mockMvc.perform(post("/v1/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(registerRequest)))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.status").value(200))
            .andExpect(jsonPath("$.message").exists())
            .andExpect(jsonPath("$.data.email").value("test@minerva.edu"))
            .andExpect(jsonPath("$.data.fullName").value("홍길동"));
    }

    @Test
    @DisplayName("POST /v1/auth/register - 유효성 검증 실패 (이메일 형식)")
    void register_Fail_InvalidEmail() throws Exception {
        // Given
        registerRequest.setEmail("invalid-email");

        // When & Then
        mockMvc.perform(post("/v1/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(registerRequest)))
            .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("POST /v1/auth/login - 로그인 성공")
    void login_Success() throws Exception {
        // Given
        when(authService.login(any(LoginRequest.class))).thenReturn(loginResponse);

        // When & Then
        mockMvc.perform(post("/v1/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(loginRequest)))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.status").value(200))
            .andExpect(jsonPath("$.data.accessToken").value("access-token"))
            .andExpect(jsonPath("$.data.refreshToken").value("refresh-token"))
            .andExpect(jsonPath("$.data.tokenType").value("Bearer"))
            .andExpect(jsonPath("$.data.user.email").value("test@minerva.edu"))
            .andExpect(jsonPath("$.data.twoFactorRequired").value(false));
    }

    @Test
    @DisplayName("POST /v1/auth/login - 2FA 필요")
    void login_TwoFactorRequired() throws Exception {
        // Given
        LoginResponse twoFactorResponse = LoginResponse.builder()
            .twoFactorRequired(true)
            .temporaryToken("temp-token")
            .tokenType("Bearer")
            .user(LoginResponse.UserInfo.builder()
                .id(1L)
                .email("test@minerva.edu")
                .name("홍길동")
                .username("test")
                .build())
            .build();

        when(authService.login(any(LoginRequest.class))).thenReturn(twoFactorResponse);

        // When & Then
        mockMvc.perform(post("/v1/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(loginRequest)))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.data.twoFactorRequired").value(true))
            .andExpect(jsonPath("$.data.temporaryToken").value("temp-token"))
            .andExpect(jsonPath("$.data.accessToken").doesNotExist());
    }

    @Test
    @DisplayName("POST /v1/auth/login - 유효성 검증 실패 (빈 비밀번호)")
    void login_Fail_EmptyPassword() throws Exception {
        // Given
        loginRequest.setPassword("");

        // When & Then
        mockMvc.perform(post("/v1/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(loginRequest)))
            .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("POST /v1/auth/refresh - 토큰 갱신 성공")
    void refreshToken_Success() throws Exception {
        // Given
        TokenRefreshRequest request = TokenRefreshRequest.builder()
            .refreshToken("refresh-token")
            .build();

        TokenRefreshResponse response = TokenRefreshResponse.builder()
            .accessToken("new-access-token")
            .refreshToken("new-refresh-token")
            .tokenType("Bearer")
            .expiresIn(3600L)
            .build();

        when(authService.refreshToken(anyString())).thenReturn(response);

        // When & Then
        mockMvc.perform(post("/v1/auth/refresh")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.data.accessToken").value("new-access-token"))
            .andExpect(jsonPath("$.data.refreshToken").value("new-refresh-token"));
    }

    @Test
    @DisplayName("POST /v1/auth/verify-email - 이메일 인증 성공")
    void verifyEmail_Success() throws Exception {
        // Given
        EmailVerificationRequest request = EmailVerificationRequest.builder()
            .token("verification-token")
            .build();

        // When & Then
        mockMvc.perform(post("/v1/auth/verify-email")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.message").value("이메일 인증이 완료되었습니다"));
    }

    @Test
    @DisplayName("POST /v1/auth/password/request - 비밀번호 재설정 요청 성공")
    void requestPasswordReset_Success() throws Exception {
        // Given
        PasswordResetRequest request = PasswordResetRequest.builder()
            .email("test@minerva.edu")
            .build();

        // When & Then
        mockMvc.perform(post("/v1/auth/password/request")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.message").exists());
    }

    @Test
    @DisplayName("POST /v1/auth/password/reset - 비밀번호 재설정 성공")
    void resetPassword_Success() throws Exception {
        // Given
        PasswordResetConfirmRequest request = PasswordResetConfirmRequest.builder()
            .token("reset-token")
            .newPassword("NewPassword123!")
            .newPasswordConfirm("NewPassword123!")
            .build();

        // When & Then
        mockMvc.perform(post("/v1/auth/password/reset")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.message").value("비밀번호가 재설정되었습니다"));
    }
}
