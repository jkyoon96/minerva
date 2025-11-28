package com.eduforum.api.domain.auth.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.OffsetDateTime;
import java.util.List;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "사용자 프로필 응답")
public class UserProfileResponse {

    @Schema(description = "사용자 ID", example = "1")
    private Long id;

    @Schema(description = "이메일", example = "student@minerva.edu")
    private String email;

    @Schema(description = "이름", example = "홍길동")
    private String firstName;

    @Schema(description = "성", example = "홍")
    private String lastName;

    @Schema(description = "전체 이름", example = "홍길동")
    private String fullName;

    @Schema(description = "프로필 이미지 URL", example = "https://cdn.eduforum.com/profiles/1.jpg")
    private String profileImageUrl;

    @Schema(description = "전화번호", example = "010-1234-5678")
    private String phone;

    @Schema(description = "상태", example = "ACTIVE")
    private String status;

    @Schema(description = "역할 목록", example = "[\"STUDENT\"]")
    private List<String> roles;

    @Schema(description = "이메일 인증 여부", example = "true")
    private Boolean emailVerified;

    @Schema(description = "이메일 인증일시")
    private OffsetDateTime emailVerifiedAt;

    @Schema(description = "마지막 로그인 일시")
    private OffsetDateTime lastLoginAt;

    @Schema(description = "생성일시")
    private OffsetDateTime createdAt;
}
