package com.eduforum.api.domain.auth.controller;

import com.eduforum.api.common.dto.ApiResponse;
import com.eduforum.api.domain.auth.dto.LoginResponse;
import com.eduforum.api.domain.auth.dto.OAuthLoginRequest;
import com.eduforum.api.domain.auth.service.OAuthService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.view.RedirectView;

/**
 * OAuth 인증 컨트롤러
 */
@Slf4j
@RestController
@RequestMapping("/v1/auth/oauth")
@RequiredArgsConstructor
@Tag(name = "OAuth", description = "OAuth 소셜 로그인 API")
public class OAuthController {

    private final OAuthService oauthService;

    @Value("${oauth2.google.client-id:}")
    private String googleClientId;

    @Value("${oauth2.google.redirect-uri:}")
    private String googleRedirectUri;

    @Value("${oauth2.microsoft.client-id:}")
    private String microsoftClientId;

    @Value("${oauth2.microsoft.redirect-uri:}")
    private String microsoftRedirectUri;

    @Operation(
        summary = "Google OAuth 시작",
        description = "Google OAuth 인증 페이지로 리다이렉트합니다."
    )
    @GetMapping("/google")
    public RedirectView initiateGoogleLogin() {
        log.info("Initiating Google OAuth login");

        String authUrl = String.format(
            "https://accounts.google.com/o/oauth2/v2/auth?client_id=%s&redirect_uri=%s&response_type=code&scope=email profile",
            googleClientId,
            googleRedirectUri
        );

        return new RedirectView(authUrl);
    }

    @Operation(
        summary = "Google OAuth 콜백",
        description = "Google OAuth 인증 후 콜백을 처리하고 JWT 토큰을 발급합니다."
    )
    @ApiResponses({
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "200",
            description = "로그인 성공",
            content = @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = LoginResponse.class)
            )
        ),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "400",
            description = "OAuth 인증 실패",
            content = @Content(schema = @Schema(ref = "#/components/schemas/ApiErrorResponse"))
        )
    })
    @GetMapping("/google/callback")
    public ResponseEntity<ApiResponse<LoginResponse>> handleGoogleCallback(
        @RequestParam("code") String code
    ) {
        log.info("Google OAuth callback received");

        OAuthLoginRequest request = OAuthLoginRequest.builder()
            .code(code)
            .redirectUri(googleRedirectUri)
            .build();

        LoginResponse response = oauthService.handleGoogleLogin(request);
        return ResponseEntity.ok(ApiResponse.success("Google 로그인 성공", response));
    }

    @Operation(
        summary = "Google OAuth 로그인 (API)",
        description = "Google OAuth 인증 코드로 직접 로그인합니다. (모바일/SPA용)"
    )
    @PostMapping("/google/login")
    public ResponseEntity<ApiResponse<LoginResponse>> googleLogin(
        @Valid @RequestBody OAuthLoginRequest request
    ) {
        log.info("Google OAuth API login");

        LoginResponse response = oauthService.handleGoogleLogin(request);
        return ResponseEntity.ok(ApiResponse.success("Google 로그인 성공", response));
    }

    @Operation(
        summary = "Microsoft OAuth 시작",
        description = "Microsoft OAuth 인증 페이지로 리다이렉트합니다."
    )
    @GetMapping("/microsoft")
    public RedirectView initiateMicrosoftLogin() {
        log.info("Initiating Microsoft OAuth login");

        String authUrl = String.format(
            "https://login.microsoftonline.com/common/oauth2/v2.0/authorize?client_id=%s&redirect_uri=%s&response_type=code&scope=openid profile email",
            microsoftClientId,
            microsoftRedirectUri
        );

        return new RedirectView(authUrl);
    }

    @Operation(
        summary = "Microsoft OAuth 콜백",
        description = "Microsoft OAuth 인증 후 콜백을 처리하고 JWT 토큰을 발급합니다."
    )
    @ApiResponses({
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "200",
            description = "로그인 성공",
            content = @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = LoginResponse.class)
            )
        ),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "400",
            description = "OAuth 인증 실패",
            content = @Content(schema = @Schema(ref = "#/components/schemas/ApiErrorResponse"))
        )
    })
    @GetMapping("/microsoft/callback")
    public ResponseEntity<ApiResponse<LoginResponse>> handleMicrosoftCallback(
        @RequestParam("code") String code
    ) {
        log.info("Microsoft OAuth callback received");

        OAuthLoginRequest request = OAuthLoginRequest.builder()
            .code(code)
            .redirectUri(microsoftRedirectUri)
            .build();

        LoginResponse response = oauthService.handleMicrosoftLogin(request);
        return ResponseEntity.ok(ApiResponse.success("Microsoft 로그인 성공", response));
    }

    @Operation(
        summary = "Microsoft OAuth 로그인 (API)",
        description = "Microsoft OAuth 인증 코드로 직접 로그인합니다. (모바일/SPA용)"
    )
    @PostMapping("/microsoft/login")
    public ResponseEntity<ApiResponse<LoginResponse>> microsoftLogin(
        @Valid @RequestBody OAuthLoginRequest request
    ) {
        log.info("Microsoft OAuth API login");

        LoginResponse response = oauthService.handleMicrosoftLogin(request);
        return ResponseEntity.ok(ApiResponse.success("Microsoft 로그인 성공", response));
    }
}
