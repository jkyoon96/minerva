package com.eduforum.api.domain.auth.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "OAuth 로그인 요청")
public class OAuthLoginRequest {

    @Schema(description = "OAuth 인증 코드", required = true)
    @NotBlank(message = "인증 코드는 필수입니다")
    private String code;

    @Schema(description = "리다이렉트 URI", example = "http://localhost:3000/auth/callback")
    private String redirectUri;
}
