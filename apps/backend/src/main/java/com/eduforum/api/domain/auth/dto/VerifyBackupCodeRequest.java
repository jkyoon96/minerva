package com.eduforum.api.domain.auth.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * 백업 코드 검증 요청 DTO
 */
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "백업 코드 검증 요청")
public class VerifyBackupCodeRequest {

    @Schema(description = "8자리 백업 코드", example = "12345678", required = true)
    @NotBlank(message = "백업 코드는 필수입니다")
    @Pattern(regexp = "^[0-9]{8}$", message = "백업 코드는 8자리 숫자여야 합니다")
    private String code;
}
