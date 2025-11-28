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
@Schema(description = "이메일 인증 요청")
public class EmailVerificationRequest {

    @Schema(description = "인증 토큰", required = true)
    @NotBlank(message = "토큰은 필수입니다")
    private String token;
}
