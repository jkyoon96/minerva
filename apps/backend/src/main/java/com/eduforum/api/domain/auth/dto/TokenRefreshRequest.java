package com.eduforum.api.domain.auth.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * 토큰 갱신 요청 DTO
 */
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "토큰 갱신 요청")
public class TokenRefreshRequest {

    @Schema(description = "리프레시 토큰", example = "eyJhbGciOiJIUzUxMiJ9...", required = true)
    @NotBlank(message = "리프레시 토큰은 필수입니다")
    private String refreshToken;
}
