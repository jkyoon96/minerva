package com.eduforum.api.domain.auth.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * Email change request DTO
 */
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "이메일 변경 요청")
public class EmailChangeRequest {

    @Schema(description = "새 이메일 주소", example = "newemail@minerva.edu")
    @NotBlank(message = "새 이메일은 필수입니다")
    @Email(message = "유효한 이메일 형식이어야 합니다")
    private String newEmail;

    @Schema(description = "현재 비밀번호 (본인 확인용)", example = "currentPassword123!")
    @NotBlank(message = "비밀번호는 필수입니다")
    private String password;
}
