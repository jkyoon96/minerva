package com.eduforum.api.domain.auth.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * 2FA 코드 검증 요청 DTO
 */
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "2FA 코드 검증 요청")
public class TwoFactorVerifyRequest {

    @Schema(description = "6자리 TOTP 코드", example = "123456", required = true)
    @NotBlank(message = "인증 코드는 필수입니다")
    @Pattern(regexp = "^[0-9]{6}$", message = "인증 코드는 6자리 숫자여야 합니다")
    private String code;
}
