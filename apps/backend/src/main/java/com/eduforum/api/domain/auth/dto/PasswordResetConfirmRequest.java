package com.eduforum.api.domain.auth.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "비밀번호 재설정 확인")
public class PasswordResetConfirmRequest {

    @Schema(description = "재설정 토큰", required = true)
    @NotBlank(message = "토큰은 필수입니다")
    private String token;

    @Schema(description = "새 비밀번호 (8-20자, 영문/숫자/특수문자 포함)", example = "newPassword123!", required = true)
    @NotBlank(message = "비밀번호는 필수입니다")
    @Size(min = 8, max = 20, message = "비밀번호는 8-20자 이내여야 합니다")
    @Pattern(
        regexp = "^(?=.*[A-Za-z])(?=.*\\d)(?=.*[@$!%*#?&])[A-Za-z\\d@$!%*#?&]{8,20}$",
        message = "비밀번호는 영문, 숫자, 특수문자를 포함해야 합니다"
    )
    private String newPassword;

    @Schema(description = "비밀번호 확인", example = "newPassword123!", required = true)
    @NotBlank(message = "비밀번호 확인은 필수입니다")
    private String newPasswordConfirm;
}
