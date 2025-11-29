package com.eduforum.api.domain.auth.controller;

import com.eduforum.api.common.dto.ApiResponse;
import com.eduforum.api.domain.auth.dto.*;
import com.eduforum.api.domain.auth.service.AuthService;
import com.eduforum.api.domain.auth.service.PasswordResetService;
import com.eduforum.api.domain.auth.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

/**
 * 인증 API 컨트롤러
 */
@Slf4j
@RestController
@RequestMapping("/v1/auth")
@RequiredArgsConstructor
@Tag(name = "Authentication", description = "인증 및 권한 관리 API")
public class AuthController {

    private final AuthService authService;
    private final UserService userService;
    private final PasswordResetService passwordResetService;
    private final TwoFactorService twoFactorService;

    @Operation(
        summary = "로그인",
        description = "이메일과 비밀번호로 로그인하여 JWT 토큰을 발급받습니다."
    )
    @ApiResponses({
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "200",
            description = "로그인 성공",
            content = @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = LoginResponse.class),
                examples = @ExampleObject(
                    value = """
                        {
                          "status": 200,
                          "message": "로그인 성공",
                          "data": {
                            "accessToken": "eyJhbGciOiJIUzUxMiJ9...",
                            "refreshToken": "eyJhbGciOiJIUzUxMiJ9...",
                            "tokenType": "Bearer",
                            "expiresIn": 3600,
                            "user": {
                              "id": 1,
                              "email": "student@minerva.edu",
                              "name": "홍길동",
                              "username": "student123",
                              "role": "STUDENT",
                              "profileImageUrl": "https://cdn.eduforum.com/profiles/1.jpg"
                            }
                          },
                          "timestamp": "2025-11-29T10:30:00"
                        }
                        """
                )
            )
        ),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "400",
            description = "잘못된 요청",
            content = @Content(schema = @Schema(ref = "#/components/schemas/ValidationErrorResponse"))
        ),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "401",
            description = "인증 실패",
            content = @Content(schema = @Schema(ref = "#/components/schemas/ApiErrorResponse"))
        )
    })
    @PostMapping("/login")
    public ResponseEntity<ApiResponse<LoginResponse>> login(
        @Valid @RequestBody LoginRequest request
    ) {
        LoginResponse response = authService.login(request);
        if (response.getTwoFactorRequired()) {
            return ResponseEntity.ok(ApiResponse.success("2FA 인증이 필요합니다", response));
        }
        return ResponseEntity.ok(ApiResponse.success("로그인 성공", response));
    }

    @Operation(
        summary = "2FA 로그인 완료",
        description = "2FA 코드 또는 백업 코드를 검증하여 로그인을 완료합니다."
    )
    @ApiResponses({
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "200",
            description = "로그인 성공",
            content = @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = LoginResponse.class),
                examples = @ExampleObject(
                    value = """
                        {
                          "status": 200,
                          "message": "로그인 성공",
                          "data": {
                            "accessToken": "eyJhbGciOiJIUzUxMiJ9...",
                            "refreshToken": "eyJhbGciOiJIUzUxMiJ9...",
                            "tokenType": "Bearer",
                            "expiresIn": 3600,
                            "twoFactorRequired": false,
                            "user": {
                              "id": 1,
                              "email": "student@minerva.edu",
                              "name": "홍길동",
                              "username": "student123",
                              "role": "STUDENT",
                              "profileImageUrl": "https://cdn.eduforum.com/profiles/1.jpg"
                            }
                          },
                          "timestamp": "2025-11-29T10:30:00"
                        }
                        """
                )
            )
        ),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "400",
            description = "잘못된 인증 코드 또는 만료된 임시 토큰",
            content = @Content(schema = @Schema(ref = "#/components/schemas/ValidationErrorResponse"))
        ),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "401",
            description = "인증 실패",
            content = @Content(schema = @Schema(ref = "#/components/schemas/ApiErrorResponse"))
        )
    })
    @PostMapping("/login/2fa")
    public ResponseEntity<ApiResponse<LoginResponse>> completeTwoFactorLogin(
        @Valid @RequestBody TwoFactorLoginRequest request
    ) {
        LoginResponse response = authService.completeTwoFactorLogin(request, twoFactorService);
        return ResponseEntity.ok(ApiResponse.success("로그인 성공", response));
    }

    @Operation(
        summary = "회원가입",
        description = "새로운 사용자 계정을 생성합니다."
    )
    @ApiResponses({
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "200",
            description = "회원가입 성공",
            content = @Content(
                mediaType = "application/json",
                examples = @ExampleObject(
                    value = """
                        {
                          "status": 200,
                          "message": "회원가입이 완료되었습니다",
                          "data": {
                            "id": 1,
                            "email": "student@minerva.edu",
                            "name": "홍길동",
                            "username": "student123",
                            "role": "STUDENT"
                          },
                          "timestamp": "2025-11-29T10:30:00"
                        }
                        """
                )
            )
        ),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "400",
            description = "잘못된 요청 (유효성 검증 실패)",
            content = @Content(schema = @Schema(ref = "#/components/schemas/ValidationErrorResponse"))
        ),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "409",
            description = "이미 존재하는 이메일",
            content = @Content(schema = @Schema(ref = "#/components/schemas/ApiErrorResponse"))
        )
    })
    @PostMapping("/register")
    public ResponseEntity<ApiResponse<UserProfileResponse>> register(
        @Valid @RequestBody RegisterRequest request
    ) {
        UserProfileResponse response = authService.register(request);
        return ResponseEntity.ok(ApiResponse.success("회원가입이 완료되었습니다. 이메일을 확인해주세요.", response));
    }

    @Operation(
        summary = "토큰 갱신",
        description = "리프레시 토큰을 사용하여 새로운 액세스 토큰을 발급받습니다."
    )
    @ApiResponses({
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "200",
            description = "토큰 갱신 성공",
            content = @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = TokenRefreshResponse.class),
                examples = @ExampleObject(
                    value = """
                        {
                          "status": 200,
                          "message": "토큰 갱신 성공",
                          "data": {
                            "accessToken": "eyJhbGciOiJIUzUxMiJ9...",
                            "refreshToken": "eyJhbGciOiJIUzUxMiJ9...",
                            "tokenType": "Bearer",
                            "expiresIn": 3600
                          },
                          "timestamp": "2025-11-29T10:30:00"
                        }
                        """
                )
            )
        ),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "400",
            description = "잘못된 요청",
            content = @Content(schema = @Schema(ref = "#/components/schemas/ValidationErrorResponse"))
        ),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "401",
            description = "유효하지 않거나 만료된 리프레시 토큰",
            content = @Content(schema = @Schema(ref = "#/components/schemas/ApiErrorResponse"))
        )
    })
    @PostMapping("/refresh")
    public ResponseEntity<ApiResponse<TokenRefreshResponse>> refreshToken(
        @Valid @RequestBody TokenRefreshRequest request
    ) {
        TokenRefreshResponse response = authService.refreshToken(request.getRefreshToken());
        return ResponseEntity.ok(ApiResponse.success("토큰 갱신 성공", response));
    }

    @Operation(
        summary = "로그아웃",
        description = "현재 사용자의 세션을 종료하고 토큰을 무효화합니다."
    )
    @ApiResponses({
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "200",
            description = "로그아웃 성공",
            content = @Content(
                mediaType = "application/json",
                examples = @ExampleObject(
                    value = """
                        {
                          "status": 200,
                          "message": "로그아웃 성공",
                          "timestamp": "2025-11-29T10:30:00"
                        }
                        """
                )
            )
        ),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "401",
            description = "인증 실패",
            content = @Content(schema = @Schema(ref = "#/components/schemas/ApiErrorResponse"))
        )
    })
    @PostMapping("/logout")
    @SecurityRequirement(name = "bearer-jwt")
    public ResponseEntity<ApiResponse<Void>> logout(
        @RequestBody(required = false) LogoutRequest request
    ) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth != null && auth.isAuthenticated()) {
            // 사용자 ID는 실제로는 인증된 사용자로부터 가져와야 함
            String refreshToken = request != null ? request.getRefreshToken() : null;
            authService.logout(1L, refreshToken); // TODO: Get actual user ID from authentication
        }
        return ResponseEntity.ok(ApiResponse.success("로그아웃 성공"));
    }

    @Operation(
        summary = "현재 사용자 정보 조회",
        description = "현재 로그인한 사용자의 정보를 조회합니다."
    )
    @ApiResponses({
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "200",
            description = "조회 성공",
            content = @Content(
                mediaType = "application/json",
                examples = @ExampleObject(
                    value = """
                        {
                          "status": 200,
                          "message": "사용자 정보 조회 성공",
                          "data": {
                            "id": 1,
                            "email": "student@minerva.edu",
                            "name": "홍길동",
                            "username": "student123",
                            "role": "STUDENT",
                            "profileImageUrl": "https://cdn.eduforum.com/profiles/1.jpg"
                          },
                          "timestamp": "2025-11-29T10:30:00"
                        }
                        """
                )
            )
        ),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "401",
            description = "인증 실패",
            content = @Content(schema = @Schema(ref = "#/components/schemas/ApiErrorResponse"))
        )
    })
    @GetMapping("/me")
    @SecurityRequirement(name = "bearer-jwt")
    public ResponseEntity<ApiResponse<UserProfileResponse>> getCurrentUser() {
        UserProfileResponse response = userService.getCurrentUser();
        return ResponseEntity.ok(ApiResponse.success("사용자 정보 조회 성공", response));
    }

    @Operation(
        summary = "프로필 수정",
        description = "현재 로그인한 사용자의 프로필을 수정합니다."
    )
    @ApiResponses({
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "200",
            description = "수정 성공"
        ),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "401",
            description = "인증 실패",
            content = @Content(schema = @Schema(ref = "#/components/schemas/ApiErrorResponse"))
        )
    })
    @PutMapping("/me")
    @SecurityRequirement(name = "bearer-jwt")
    public ResponseEntity<ApiResponse<UserProfileResponse>> updateProfile(
        @Valid @RequestBody UserProfileUpdateRequest request
    ) {
        UserProfileResponse currentUser = userService.getCurrentUser();
        UserProfileResponse response = userService.updateProfile(currentUser.getId(), request);
        return ResponseEntity.ok(ApiResponse.success("프로필이 수정되었습니다", response));
    }

    @Operation(
        summary = "이메일 인증",
        description = "회원가입 후 받은 이메일의 인증 토큰으로 이메일을 인증합니다."
    )
    @ApiResponses({
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "200",
            description = "인증 성공"
        ),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "400",
            description = "유효하지 않거나 만료된 토큰",
            content = @Content(schema = @Schema(ref = "#/components/schemas/ApiErrorResponse"))
        )
    })
    @PostMapping("/verify-email")
    public ResponseEntity<ApiResponse<Void>> verifyEmail(
        @Valid @RequestBody EmailVerificationRequest request
    ) {
        authService.verifyEmail(request.getToken());
        return ResponseEntity.ok(ApiResponse.success("이메일 인증이 완료되었습니다"));
    }

    @Operation(
        summary = "비밀번호 재설정 요청",
        description = "비밀번호를 잊어버린 경우 재설정 링크를 이메일로 받습니다."
    )
    @ApiResponses({
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "200",
            description = "재설정 링크 발송 완료"
        )
    })
    @PostMapping("/password/request")
    public ResponseEntity<ApiResponse<Void>> requestPasswordReset(
        @Valid @RequestBody PasswordResetRequest request
    ) {
        passwordResetService.requestPasswordReset(request);
        return ResponseEntity.ok(ApiResponse.success("비밀번호 재설정 링크가 이메일로 발송되었습니다"));
    }

    @Operation(
        summary = "비밀번호 재설정",
        description = "재설정 토큰을 사용하여 새로운 비밀번호로 변경합니다."
    )
    @ApiResponses({
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "200",
            description = "비밀번호 재설정 완료"
        ),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "400",
            description = "유효하지 않거나 만료된 토큰",
            content = @Content(schema = @Schema(ref = "#/components/schemas/ApiErrorResponse"))
        )
    })
    @PostMapping("/password/reset")
    public ResponseEntity<ApiResponse<Void>> resetPassword(
        @Valid @RequestBody PasswordResetConfirmRequest request
    ) {
        passwordResetService.resetPassword(request);
        return ResponseEntity.ok(ApiResponse.success("비밀번호가 재설정되었습니다"));
    }
}
