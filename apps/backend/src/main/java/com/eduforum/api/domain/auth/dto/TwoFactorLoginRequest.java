package com.eduforum.api.domain.auth.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * 2FA 로그인 완료 요청 DTO
 */
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "2FA 로그인 완료 요청")
public class TwoFactorLoginRequest {

    @Schema(description = "임시 토큰", example = "temp_token_abc123", required = true)
    @NotBlank(message = "임시 토큰은 필수입니다")
    private String temporaryToken;

    @Schema(description = "6자리 TOTP 코드 (코드와 백업코드 중 하나 필수)", example = "123456")
    @Pattern(regexp = "^[0-9]{6}$", message = "인증 코드는 6자리 숫자여야 합니다")
    private String code;

    @Schema(description = "8자리 백업 코드 (코드와 백업코드 중 하나 필수)", example = "12345678")
    @Pattern(regexp = "^[0-9]{8}$", message = "백업 코드는 8자리 숫자여야 합니다")
    private String backupCode;
}
