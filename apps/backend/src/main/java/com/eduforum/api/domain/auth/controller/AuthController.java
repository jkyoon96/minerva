package com.eduforum.api.domain.auth.controller;

import com.eduforum.api.common.dto.ApiResponse;
import com.eduforum.api.domain.auth.dto.*;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
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
        log.info("Login request for email: {}", request.getEmail());

        // TODO: 실제 로그인 로직 구현
        LoginResponse response = LoginResponse.builder()
            .accessToken("eyJhbGciOiJIUzUxMiJ9.sample.access.token")
            .refreshToken("eyJhbGciOiJIUzUxMiJ9.sample.refresh.token")
            .tokenType("Bearer")
            .expiresIn(3600L)
            .user(LoginResponse.UserInfo.builder()
                .id(1L)
                .email(request.getEmail())
                .name("홍길동")
                .username("student123")
                .role("STUDENT")
                .profileImageUrl("https://cdn.eduforum.com/profiles/1.jpg")
                .build())
            .build();

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
    public ResponseEntity<ApiResponse<Object>> register(
        @Valid @RequestBody RegisterRequest request
    ) {
        log.info("Register request for email: {}", request.getEmail());

        // TODO: 실제 회원가입 로직 구현
        // 비밀번호 확인 검증
        if (!request.getPassword().equals(request.getPasswordConfirm())) {
            return ResponseEntity.badRequest()
                .body(ApiResponse.error(400, "비밀번호가 일치하지 않습니다"));
        }

        Object response = new Object() {
            public final Long id = 1L;
            public final String email = request.getEmail();
            public final String name = request.getName();
            public final String username = request.getUsername();
            public final String role = request.getRole();
        };

        return ResponseEntity.ok(ApiResponse.success("회원가입이 완료되었습니다", response));
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
        log.info("Token refresh request");

        // TODO: 실제 토큰 갱신 로직 구현
        TokenRefreshResponse response = TokenRefreshResponse.builder()
            .accessToken("eyJhbGciOiJIUzUxMiJ9.new.access.token")
            .refreshToken("eyJhbGciOiJIUzUxMiJ9.new.refresh.token")
            .tokenType("Bearer")
            .expiresIn(3600L)
            .build();

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
    public ResponseEntity<ApiResponse<Void>> logout() {
        log.info("Logout request");

        // TODO: 실제 로그아웃 로직 구현 (토큰 블랙리스트 등)

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
    public ResponseEntity<ApiResponse<LoginResponse.UserInfo>> getCurrentUser() {
        log.info("Get current user request");

        // TODO: 실제 사용자 정보 조회 로직 구현
        LoginResponse.UserInfo userInfo = LoginResponse.UserInfo.builder()
            .id(1L)
            .email("student@minerva.edu")
            .name("홍길동")
            .username("student123")
            .role("STUDENT")
            .profileImageUrl("https://cdn.eduforum.com/profiles/1.jpg")
            .build();

        return ResponseEntity.ok(ApiResponse.success("사용자 정보 조회 성공", userInfo));
    }
}
