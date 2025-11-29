package com.eduforum.api.domain.auth.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * Email verification request DTO (for email change)
 */
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "이메일 변경 확인 요청")
public class EmailVerifyRequest {

    @Schema(description = "이메일로 받은 인증 토큰", example = "a1b2c3d4-e5f6-g7h8-i9j0-k1l2m3n4o5p6")
    @NotBlank(message = "토큰은 필수입니다")
    private String token;
}
