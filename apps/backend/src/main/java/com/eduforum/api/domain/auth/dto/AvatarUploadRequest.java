package com.eduforum.api.domain.auth.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * Avatar upload request DTO
 */
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "프로필 사진 업로드 요청")
public class AvatarUploadRequest {

    @Schema(description = "Base64로 인코딩된 이미지 데이터", example = "iVBORw0KGgoAAAANSUhEUgAAAAUA...")
    @NotBlank(message = "이미지 데이터는 필수입니다")
    private String imageBase64;

    @Schema(description = "MIME 타입", example = "image/jpeg", allowableValues = {"image/jpeg", "image/png", "image/jpg"})
    @NotBlank(message = "MIME 타입은 필수입니다")
    @Pattern(regexp = "^image/(jpeg|jpg|png)$", message = "지원하지 않는 이미지 형식입니다. jpg, jpeg, png만 가능합니다")
    private String mimeType;
}
