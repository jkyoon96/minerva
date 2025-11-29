package com.eduforum.api.domain.auth.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * Profile update request DTO
 */
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "프로필 수정 요청")
public class ProfileUpdateRequest {

    @Schema(description = "전체 이름", example = "홍길동")
    @Size(min = 2, max = 100, message = "이름은 2-100자 이내여야 합니다")
    private String name;

    @Schema(description = "자기소개", example = "컴퓨터공학과 3학년입니다.")
    @Size(max = 500, message = "자기소개는 500자 이내여야 합니다")
    private String bio;
}
