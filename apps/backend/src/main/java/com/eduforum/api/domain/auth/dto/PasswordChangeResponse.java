package com.eduforum.api.domain.auth.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * Password change response DTO
 */
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "비밀번호 변경 응답")
public class PasswordChangeResponse {

    @Schema(description = "성공 여부", example = "true")
    private Boolean success;

    @Schema(description = "메시지", example = "비밀번호가 성공적으로 변경되었습니다")
    private String message;
}
