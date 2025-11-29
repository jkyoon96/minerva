package com.eduforum.api.domain.auth.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * Password change request DTO
 */
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "비밀번호 변경 요청")
public class PasswordChangeRequest {

    @Schema(description = "현재 비밀번호", example = "currentPassword123!")
    @NotBlank(message = "현재 비밀번호는 필수입니다")
    private String currentPassword;

    @Schema(description = "새 비밀번호", example = "newPassword123!")
    @NotBlank(message = "새 비밀번호는 필수입니다")
    @Size(min = 8, max = 100, message = "비밀번호는 8-100자 이내여야 합니다")
    @Pattern(
        regexp = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[@$!%*?&])[A-Za-z\\d@$!%*?&]{8,}$",
        message = "비밀번호는 최소 8자 이상이며, 대문자, 소문자, 숫자, 특수문자를 각각 최소 1개 이상 포함해야 합니다"
    )
    private String newPassword;

    @Schema(description = "새 비밀번호 확인", example = "newPassword123!")
    @NotBlank(message = "새 비밀번호 확인은 필수입니다")
    private String confirmPassword;
}
