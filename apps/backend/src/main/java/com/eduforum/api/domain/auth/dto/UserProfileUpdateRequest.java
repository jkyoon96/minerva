package com.eduforum.api.domain.auth.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "사용자 프로필 수정 요청")
public class UserProfileUpdateRequest {

    @Schema(description = "이름", example = "길동")
    @Size(min = 2, max = 100, message = "이름은 2-100자 이내여야 합니다")
    private String firstName;

    @Schema(description = "성", example = "홍")
    @Size(min = 1, max = 100, message = "성은 1-100자 이내여야 합니다")
    private String lastName;

    @Schema(description = "프로필 이미지 URL", example = "https://cdn.eduforum.com/profiles/1.jpg")
    private String profileImageUrl;

    @Schema(description = "전화번호", example = "010-1234-5678")
    @Size(max = 20, message = "전화번호는 20자 이내여야 합니다")
    private String phone;
}
