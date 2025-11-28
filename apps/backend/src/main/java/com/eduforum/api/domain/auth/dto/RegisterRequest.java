package com.eduforum.api.domain.auth.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * 회원가입 요청 DTO
 */
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "회원가입 요청")
public class RegisterRequest {

    @Schema(description = "이메일", example = "student@minerva.edu", required = true)
    @NotBlank(message = "이메일은 필수입니다")
    @Email(message = "이메일 형식이 올바르지 않습니다")
    private String email;

    @Schema(description = "비밀번호 (8-20자, 영문/숫자/특수문자 포함)", example = "password123!", required = true)
    @NotBlank(message = "비밀번호는 필수입니다")
    @Size(min = 8, max = 20, message = "비밀번호는 8-20자 이내여야 합니다")
    @Pattern(
        regexp = "^(?=.*[A-Za-z])(?=.*\\d)(?=.*[@$!%*#?&])[A-Za-z\\d@$!%*#?&]{8,20}$",
        message = "비밀번호는 영문, 숫자, 특수문자를 포함해야 합니다"
    )
    private String password;

    @Schema(description = "비밀번호 확인", example = "password123!", required = true)
    @NotBlank(message = "비밀번호 확인은 필수입니다")
    private String passwordConfirm;

    @Schema(description = "이름", example = "홍길동", required = true)
    @NotBlank(message = "이름은 필수입니다")
    @Size(min = 2, max = 50, message = "이름은 2-50자 이내여야 합니다")
    private String name;

    @Schema(description = "사용자명 (4-20자, 영문/숫자)", example = "student123", required = true)
    @NotBlank(message = "사용자명은 필수입니다")
    @Size(min = 4, max = 20, message = "사용자명은 4-20자 이내여야 합니다")
    @Pattern(
        regexp = "^[a-zA-Z0-9]+$",
        message = "사용자명은 영문과 숫자만 사용할 수 있습니다"
    )
    private String username;

    @Schema(description = "역할", example = "STUDENT", required = true, allowableValues = {"STUDENT", "PROFESSOR", "ADMIN"})
    @NotBlank(message = "역할은 필수입니다")
    @Pattern(
        regexp = "^(STUDENT|PROFESSOR|ADMIN)$",
        message = "역할은 STUDENT, PROFESSOR, ADMIN 중 하나여야 합니다"
    )
    private String role;

    @Schema(description = "이용약관 동의", example = "true", required = true)
    private Boolean termsAgreed;

    @Schema(description = "개인정보 처리방침 동의", example = "true", required = true)
    private Boolean privacyAgreed;
}
