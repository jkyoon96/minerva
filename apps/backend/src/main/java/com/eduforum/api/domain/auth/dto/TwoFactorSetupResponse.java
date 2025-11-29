package com.eduforum.api.domain.auth.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * 2FA 설정 응답 DTO
 */
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "2FA 설정 응답")
public class TwoFactorSetupResponse {

    @Schema(description = "TOTP 시크릿 (Base32 인코딩)", example = "JBSWY3DPEHPK3PXP")
    private String secret;

    @Schema(description = "QR 코드 URI (otpauth://)",
            example = "otpauth://totp/EduForum:student@minerva.edu?secret=JBSWY3DPEHPK3PXP&issuer=EduForum")
    private String qrCodeUri;

    @Schema(description = "백업 코드 목록 (10개)",
            example = "[\"12345678\", \"87654321\", \"11223344\"]")
    private List<String> backupCodes;

    @Schema(description = "설정 완료 여부", example = "false")
    @Builder.Default
    private Boolean enabled = false;
}
