package com.eduforum.api.domain.auth.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * Avatar upload response DTO
 */
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "프로필 사진 업로드 응답")
public class AvatarUploadResponse {

    @Schema(description = "업로드된 프로필 사진 URL", example = "/static/avatars/user_1_1638259200000.jpg")
    private String avatarUrl;

    @Schema(description = "성공 메시지", example = "프로필 사진이 업로드되었습니다")
    private String message;
}
