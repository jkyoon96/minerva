package com.eduforum.api.domain.auth.controller;

import com.eduforum.api.common.dto.ApiResponse;
import com.eduforum.api.domain.auth.dto.*;
import com.eduforum.api.domain.auth.service.ProfileService;
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
 * Profile management controller
 */
@Slf4j
@RestController
@RequestMapping("/v1/users")
@RequiredArgsConstructor
@Tag(name = "Profile", description = "프로필 관리 API")
@SecurityRequirement(name = "bearer-jwt")
public class ProfileController {

    private final ProfileService profileService;

    @Operation(
        summary = "내 프로필 조회",
        description = "현재 로그인한 사용자의 프로필 정보를 조회합니다."
    )
    @ApiResponses({
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "200",
            description = "조회 성공",
            content = @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = ProfileResponse.class),
                examples = @ExampleObject(
                    value = """
                        {
                          "status": 200,
                          "message": "프로필 조회 성공",
                          "data": {
                            "id": 1,
                            "email": "student@minerva.edu",
                            "name": "홍길동",
                            "avatarUrl": "/static/avatars/user_1.jpg",
                            "bio": "컴퓨터공학과 3학년입니다.",
                            "role": "STUDENT",
                            "createdAt": "2025-11-01T10:00:00Z",
                            "emailVerified": true,
                            "twoFactorEnabled": false
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
    @GetMapping("/profile")
    public ResponseEntity<ApiResponse<ProfileResponse>> getProfile() {
        ProfileResponse response = profileService.getProfile();
        return ResponseEntity.ok(ApiResponse.success("프로필 조회 성공", response));
    }

    @Operation(
        summary = "프로필 수정",
        description = "사용자의 이름과 자기소개를 수정합니다."
    )
    @ApiResponses({
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "200",
            description = "수정 성공",
            content = @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = ProfileResponse.class),
                examples = @ExampleObject(
                    value = """
                        {
                          "status": 200,
                          "message": "프로필이 수정되었습니다",
                          "data": {
                            "id": 1,
                            "email": "student@minerva.edu",
                            "name": "홍길동",
                            "avatarUrl": "/static/avatars/user_1.jpg",
                            "bio": "업데이트된 자기소개입니다.",
                            "role": "STUDENT",
                            "createdAt": "2025-11-01T10:00:00Z",
                            "emailVerified": true,
                            "twoFactorEnabled": false
                          },
                          "timestamp": "2025-11-29T10:30:00"
                        }
                        """
                )
            )
        ),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "400",
            description = "잘못된 요청",
            content = @Content(schema = @Schema(ref = "#/components/schemas/ValidationErrorResponse"))
        ),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "401",
            description = "인증 실패",
            content = @Content(schema = @Schema(ref = "#/components/schemas/ApiErrorResponse"))
        )
    })
    @PutMapping("/profile")
    public ResponseEntity<ApiResponse<ProfileResponse>> updateProfile(
        @Valid @RequestBody ProfileUpdateRequest request
    ) {
        ProfileResponse response = profileService.updateProfile(request);
        return ResponseEntity.ok(ApiResponse.success("프로필이 수정되었습니다", response));
    }

    @Operation(
        summary = "프로필 사진 업로드",
        description = "Base64로 인코딩된 이미지를 업로드하여 프로필 사진을 설정합니다. (최대 5MB, jpg/jpeg/png 형식)"
    )
    @ApiResponses({
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "200",
            description = "업로드 성공",
            content = @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = AvatarUploadResponse.class),
                examples = @ExampleObject(
                    value = """
                        {
                          "status": 200,
                          "message": "프로필 사진이 업로드되었습니다",
                          "data": {
                            "avatarUrl": "/static/avatars/user_1_1638259200000.jpg",
                            "message": "프로필 사진이 업로드되었습니다"
                          },
                          "timestamp": "2025-11-29T10:30:00"
                        }
                        """
                )
            )
        ),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "400",
            description = "잘못된 요청 (파일 형식 오류, 크기 초과 등)",
            content = @Content(schema = @Schema(ref = "#/components/schemas/ValidationErrorResponse"))
        ),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "401",
            description = "인증 실패",
            content = @Content(schema = @Schema(ref = "#/components/schemas/ApiErrorResponse"))
        )
    })
    @PostMapping("/profile/avatar")
    public ResponseEntity<ApiResponse<AvatarUploadResponse>> uploadAvatar(
        @Valid @RequestBody AvatarUploadRequest request
    ) {
        AvatarUploadResponse response = profileService.uploadAvatar(request);
        return ResponseEntity.ok(ApiResponse.success("프로필 사진이 업로드되었습니다", response));
    }

    @Operation(
        summary = "프로필 사진 삭제",
        description = "현재 설정된 프로필 사진을 삭제합니다."
    )
    @ApiResponses({
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "200",
            description = "삭제 성공",
            content = @Content(
                mediaType = "application/json",
                examples = @ExampleObject(
                    value = """
                        {
                          "status": 200,
                          "message": "프로필 사진이 삭제되었습니다",
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
        ),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "404",
            description = "프로필 사진이 존재하지 않음",
            content = @Content(schema = @Schema(ref = "#/components/schemas/ApiErrorResponse"))
        )
    })
    @DeleteMapping("/profile/avatar")
    public ResponseEntity<ApiResponse<Void>> deleteAvatar() {
        profileService.deleteAvatar();
        return ResponseEntity.ok(ApiResponse.success("프로필 사진이 삭제되었습니다"));
    }

    @Operation(
        summary = "이메일 변경 요청",
        description = "새로운 이메일로 변경을 요청합니다. 새 이메일로 인증 링크가 발송됩니다. (현재 비밀번호 필요)"
    )
    @ApiResponses({
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "200",
            description = "요청 성공",
            content = @Content(
                mediaType = "application/json",
                examples = @ExampleObject(
                    value = """
                        {
                          "status": 200,
                          "message": "새 이메일로 인증 링크가 발송되었습니다. 24시간 이내에 인증해주세요.",
                          "timestamp": "2025-11-29T10:30:00"
                        }
                        """
                )
            )
        ),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "400",
            description = "잘못된 요청 (이미 사용 중인 이메일 등)",
            content = @Content(schema = @Schema(ref = "#/components/schemas/ValidationErrorResponse"))
        ),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "401",
            description = "인증 실패 (비밀번호 불일치)",
            content = @Content(schema = @Schema(ref = "#/components/schemas/ApiErrorResponse"))
        )
    })
    @PostMapping("/email/change")
    public ResponseEntity<ApiResponse<Void>> requestEmailChange(
        @Valid @RequestBody EmailChangeRequest request
    ) {
        profileService.requestEmailChange(request);
        return ResponseEntity.ok(ApiResponse.success("새 이메일로 인증 링크가 발송되었습니다. 24시간 이내에 인증해주세요."));
    }

    @Operation(
        summary = "이메일 변경 확인",
        description = "이메일로 받은 토큰을 사용하여 이메일 변경을 완료합니다."
    )
    @ApiResponses({
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "200",
            description = "변경 성공",
            content = @Content(
                mediaType = "application/json",
                examples = @ExampleObject(
                    value = """
                        {
                          "status": 200,
                          "message": "이메일이 성공적으로 변경되었습니다",
                          "timestamp": "2025-11-29T10:30:00"
                        }
                        """
                )
            )
        ),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "400",
            description = "잘못된 요청 (유효하지 않거나 만료된 토큰)",
            content = @Content(schema = @Schema(ref = "#/components/schemas/ValidationErrorResponse"))
        )
    })
    @PostMapping("/email/verify")
    public ResponseEntity<ApiResponse<Void>> confirmEmailChange(
        @Valid @RequestBody EmailVerifyRequest request
    ) {
        profileService.confirmEmailChange(request);
        return ResponseEntity.ok(ApiResponse.success("이메일이 성공적으로 변경되었습니다"));
    }

    @Operation(
        summary = "비밀번호 변경",
        description = "현재 비밀번호를 확인한 후 새로운 비밀번호로 변경합니다."
    )
    @ApiResponses({
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "200",
            description = "변경 성공",
            content = @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = PasswordChangeResponse.class),
                examples = @ExampleObject(
                    value = """
                        {
                          "status": 200,
                          "message": "비밀번호가 성공적으로 변경되었습니다",
                          "data": {
                            "success": true,
                            "message": "비밀번호가 성공적으로 변경되었습니다"
                          },
                          "timestamp": "2025-11-29T10:30:00"
                        }
                        """
                )
            )
        ),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "400",
            description = "잘못된 요청 (비밀번호 불일치, 형식 오류 등)",
            content = @Content(schema = @Schema(ref = "#/components/schemas/ValidationErrorResponse"))
        ),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "401",
            description = "인증 실패 (현재 비밀번호 불일치)",
            content = @Content(schema = @Schema(ref = "#/components/schemas/ApiErrorResponse"))
        )
    })
    @PutMapping("/password")
    public ResponseEntity<ApiResponse<PasswordChangeResponse>> changePassword(
        @Valid @RequestBody PasswordChangeRequest request
    ) {
        PasswordChangeResponse response = profileService.changePassword(request);
        return ResponseEntity.ok(ApiResponse.success("비밀번호가 성공적으로 변경되었습니다", response));
    }
}
