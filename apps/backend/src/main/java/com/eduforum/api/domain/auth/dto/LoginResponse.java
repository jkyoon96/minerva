package com.eduforum.api.domain.auth.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * 로그인 응답 DTO
 */
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "로그인 응답")
public class LoginResponse {

    @Schema(description = "액세스 토큰", example = "eyJhbGciOiJIUzUxMiJ9...")
    private String accessToken;

    @Schema(description = "리프레시 토큰", example = "eyJhbGciOiJIUzUxMiJ9...")
    private String refreshToken;

    @Schema(description = "토큰 타입", example = "Bearer")
    @Builder.Default
    private String tokenType = "Bearer";

    @Schema(description = "토큰 만료 시간 (초)", example = "3600")
    private Long expiresIn;

    @Schema(description = "사용자 정보")
    private UserInfo user;

    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    @Schema(description = "사용자 기본 정보")
    public static class UserInfo {

        @Schema(description = "사용자 ID", example = "1")
        private Long id;

        @Schema(description = "이메일", example = "student@minerva.edu")
        private String email;

        @Schema(description = "이름", example = "홍길동")
        private String name;

        @Schema(description = "사용자명", example = "student123")
        private String username;

        @Schema(description = "역할", example = "STUDENT")
        private String role;

        @Schema(description = "프로필 이미지 URL", example = "https://cdn.eduforum.com/profiles/1.jpg")
        private String profileImageUrl;
    }
}
