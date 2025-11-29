package com.eduforum.api.domain.auth.controller;

import com.eduforum.api.common.dto.ApiResponse;
import com.eduforum.api.domain.auth.dto.*;
import com.eduforum.api.domain.auth.service.TwoFactorService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * Two-Factor Authentication API Controller
 */
@Slf4j
@RestController
@RequestMapping("/v1/auth/2fa")
@RequiredArgsConstructor
@SecurityRequirement(name = "bearer-jwt")
@Tag(name = "Two-Factor Authentication", description = "2단계 인증 관리 API")
public class TwoFactorController {

    private final TwoFactorService twoFactorService;

    @Operation(
        summary = "2FA 설정 시작",
        description = "2FA 설정을 시작합니다. TOTP 시크릿과 QR 코드 URI, 백업 코드를 반환합니다."
    )
    @ApiResponses({
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "200",
            description = "설정 시작 성공",
            content = @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = TwoFactorSetupResponse.class),
                examples = @ExampleObject(
                    value = """
                        {
                          "status": 200,
                          "message": "2FA 설정이 시작되었습니다",
                          "data": {
                            "secret": "JBSWY3DPEHPK3PXP",
                            "qrCodeUri": "otpauth://totp/EduForum:student@minerva.edu?secret=JBSWY3DPEHPK3PXP&issuer=EduForum",
                            "backupCodes": ["12345678", "87654321", "11223344", "44332211", "55667788", "88776655", "99001122", "22110099", "33445566", "66554433"],
                            "enabled": false
                          },
                          "timestamp": "2025-11-29T10:30:00"
                        }
                        """
                )
            )
        ),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "400",
            description = "이미 2FA가 활성화됨",
            content = @Content(schema = @Schema(ref = "#/components/schemas/ApiErrorResponse"))
        ),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "401",
            description = "인증 실패",
            content = @Content(schema = @Schema(ref = "#/components/schemas/ApiErrorResponse"))
        )
    })
    @PostMapping("/setup")
    public ResponseEntity<ApiResponse<TwoFactorSetupResponse>> setupTwoFactor() {
        // TODO: Get user ID from authentication context
        Long userId = 1L; // Placeholder

        TwoFactorSetupResponse response = twoFactorService.setupTwoFactor(userId);
        return ResponseEntity.ok(ApiResponse.success(
            "2FA 설정이 시작되었습니다. 인증 앱에서 QR 코드를 스캔하고 코드를 입력하여 활성화하세요.",
            response
        ));
    }

    @Operation(
        summary = "2FA 코드 검증 및 활성화",
        description = "TOTP 코드를 검증하고 2FA를 활성화합니다."
    )
    @ApiResponses({
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "200",
            description = "2FA 활성화 성공",
            content = @Content(
                mediaType = "application/json",
                examples = @ExampleObject(
                    value = """
                        {
                          "status": 200,
                          "message": "2FA가 활성화되었습니다",
                          "timestamp": "2025-11-29T10:30:00"
                        }
                        """
                )
            )
        ),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "400",
            description = "잘못된 인증 코드 또는 이미 활성화됨",
            content = @Content(schema = @Schema(ref = "#/components/schemas/ValidationErrorResponse"))
        ),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "401",
            description = "인증 실패",
            content = @Content(schema = @Schema(ref = "#/components/schemas/ApiErrorResponse"))
        )
    })
    @PostMapping("/verify")
    public ResponseEntity<ApiResponse<Void>> verifyAndEnable(
        @Valid @RequestBody TwoFactorVerifyRequest request
    ) {
        // TODO: Get user ID from authentication context
        Long userId = 1L; // Placeholder

        twoFactorService.enableTwoFactor(userId, request.getCode());
        return ResponseEntity.ok(ApiResponse.success("2FA가 활성화되었습니다"));
    }

    @Operation(
        summary = "2FA 비활성화",
        description = "TOTP 코드 또는 백업 코드를 사용하여 2FA를 비활성화합니다."
    )
    @ApiResponses({
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "200",
            description = "2FA 비활성화 성공",
            content = @Content(
                mediaType = "application/json",
                examples = @ExampleObject(
                    value = """
                        {
                          "status": 200,
                          "message": "2FA가 비활성화되었습니다",
                          "timestamp": "2025-11-29T10:30:00"
                        }
                        """
                )
            )
        ),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "400",
            description = "잘못된 인증 코드 또는 2FA가 활성화되어 있지 않음",
            content = @Content(schema = @Schema(ref = "#/components/schemas/ValidationErrorResponse"))
        ),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "401",
            description = "인증 실패",
            content = @Content(schema = @Schema(ref = "#/components/schemas/ApiErrorResponse"))
        )
    })
    @PostMapping("/disable")
    public ResponseEntity<ApiResponse<Void>> disableTwoFactor(
        @Valid @RequestBody TwoFactorDisableRequest request
    ) {
        // TODO: Get user ID from authentication context
        Long userId = 1L; // Placeholder

        twoFactorService.disableTwoFactor(userId, request.getCode(), request.getBackupCode());
        return ResponseEntity.ok(ApiResponse.success("2FA가 비활성화되었습니다"));
    }

    @Operation(
        summary = "2FA 상태 조회",
        description = "현재 사용자의 2FA 활성화 상태를 조회합니다."
    )
    @ApiResponses({
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "200",
            description = "조회 성공",
            content = @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = TwoFactorStatusResponse.class),
                examples = @ExampleObject(
                    value = """
                        {
                          "status": 200,
                          "message": "2FA 상태 조회 성공",
                          "data": {
                            "enabled": true,
                            "enabledAt": "2025-11-29T10:30:00+09:00",
                            "remainingBackupCodes": 8
                          },
                          "timestamp": "2025-11-29T10:30:00"
                        }
                        """
                )
            )
        ),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "401",
            description = "인증 실패",
            content = @Content(schema = @Schema(ref = "#/components/schemas/ApiErrorResponse"))
        )
    })
    @GetMapping("/status")
    public ResponseEntity<ApiResponse<TwoFactorStatusResponse>> getStatus() {
        // TODO: Get user ID from authentication context
        Long userId = 1L; // Placeholder

        TwoFactorStatusResponse response = twoFactorService.getStatus(userId);
        return ResponseEntity.ok(ApiResponse.success("2FA 상태 조회 성공", response));
    }

    @Operation(
        summary = "백업 코드 재생성",
        description = "새로운 백업 코드를 생성합니다. 기존 백업 코드는 모두 무효화됩니다."
    )
    @ApiResponses({
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "200",
            description = "백업 코드 재생성 성공",
            content = @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = BackupCodesResponse.class),
                examples = @ExampleObject(
                    value = """
                        {
                          "status": 200,
                          "message": "백업 코드가 재생성되었습니다",
                          "data": {
                            "codes": ["12345678", "87654321", "11223344", "44332211", "55667788", "88776655", "99001122", "22110099", "33445566", "66554433"],
                            "count": 10,
                            "warning": "이 백업 코드는 한 번만 표시됩니다. 안전한 곳에 보관하세요."
                          },
                          "timestamp": "2025-11-29T10:30:00"
                        }
                        """
                )
            )
        ),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "400",
            description = "2FA가 활성화되어 있지 않음",
            content = @Content(schema = @Schema(ref = "#/components/schemas/ApiErrorResponse"))
        ),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "401",
            description = "인증 실패",
            content = @Content(schema = @Schema(ref = "#/components/schemas/ApiErrorResponse"))
        )
    })
    @PostMapping("/backup-codes")
    public ResponseEntity<ApiResponse<BackupCodesResponse>> regenerateBackupCodes() {
        // TODO: Get user ID from authentication context
        Long userId = 1L; // Placeholder

        BackupCodesResponse response = twoFactorService.regenerateBackupCodes(userId);
        return ResponseEntity.ok(ApiResponse.success(
            "백업 코드가 재생성되었습니다. 안전한 곳에 보관하세요.",
            response
        ));
    }

    @Operation(
        summary = "백업 코드로 인증",
        description = "백업 코드를 사용하여 2FA 인증을 수행합니다. 백업 코드는 1회용입니다."
    )
    @ApiResponses({
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "200",
            description = "백업 코드 인증 성공",
            content = @Content(
                mediaType = "application/json",
                examples = @ExampleObject(
                    value = """
                        {
                          "status": 200,
                          "message": "백업 코드 인증 성공",
                          "timestamp": "2025-11-29T10:30:00"
                        }
                        """
                )
            )
        ),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "400",
            description = "잘못된 백업 코드",
            content = @Content(schema = @Schema(ref = "#/components/schemas/ValidationErrorResponse"))
        ),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "401",
            description = "인증 실패",
            content = @Content(schema = @Schema(ref = "#/components/schemas/ApiErrorResponse"))
        )
    })
    @PostMapping("/verify-backup")
    public ResponseEntity<ApiResponse<Void>> verifyBackupCode(
        @Valid @RequestBody VerifyBackupCodeRequest request
    ) {
        // TODO: Get user ID from authentication context
        Long userId = 1L; // Placeholder

        boolean verified = twoFactorService.verifyAndUseBackupCode(userId, request.getCode());
        if (!verified) {
            return ResponseEntity.badRequest()
                .body(ApiResponse.error("잘못된 백업 코드입니다"));
        }

        return ResponseEntity.ok(ApiResponse.success("백업 코드 인증 성공"));
    }
}
