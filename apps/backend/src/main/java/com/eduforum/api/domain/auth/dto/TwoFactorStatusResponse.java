package com.eduforum.api.domain.auth.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.OffsetDateTime;

/**
 * 2FA 상태 응답 DTO
 */
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "2FA 상태 응답")
public class TwoFactorStatusResponse {

    @Schema(description = "2FA 활성화 여부", example = "true")
    private Boolean enabled;

    @Schema(description = "2FA 활성화 일시", example = "2025-11-29T10:30:00+09:00")
    private OffsetDateTime enabledAt;

    @Schema(description = "남은 백업 코드 개수", example = "8")
    private Long remainingBackupCodes;
}
