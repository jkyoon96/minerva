package com.eduforum.api.domain.auth.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.OffsetDateTime;

/**
 * Profile response DTO
 */
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "프로필 조회 응답")
public class ProfileResponse {

    @Schema(description = "사용자 ID", example = "1")
    private Long id;

    @Schema(description = "이메일", example = "student@minerva.edu")
    private String email;

    @Schema(description = "전체 이름", example = "홍길동")
    private String name;

    @Schema(description = "프로필 이미지 URL", example = "/static/avatars/user_1.jpg")
    private String avatarUrl;

    @Schema(description = "자기소개", example = "컴퓨터공학과 3학년입니다.")
    private String bio;

    @Schema(description = "역할", example = "STUDENT")
    private String role;

    @Schema(description = "생성일시")
    private OffsetDateTime createdAt;

    @Schema(description = "이메일 인증 여부", example = "true")
    private Boolean emailVerified;

    @Schema(description = "2FA 활성화 여부", example = "false")
    private Boolean twoFactorEnabled;
}
