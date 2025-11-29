package com.eduforum.api.domain.file.dto;

import com.eduforum.api.domain.file.entity.FilePermission;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * 파일 권한 응답 DTO
 */
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "파일 권한 정보")
public class PermissionResponse {

    @Schema(description = "권한 ID")
    private Long id;

    @Schema(description = "파일 ID")
    private Long fileId;

    @Schema(description = "파일명")
    private String fileName;

    @Schema(description = "사용자 ID")
    private Long userId;

    @Schema(description = "사용자 이름")
    private String userName;

    @Schema(description = "사용자 이메일")
    private String userEmail;

    @Schema(description = "권한 타입")
    private FilePermission.PermissionType permission;

    @Schema(description = "권한 부여자 ID")
    private Long grantedById;

    @Schema(description = "권한 부여자 이름")
    private String grantedByName;

    @Schema(description = "생성일시")
    private LocalDateTime createdAt;

    /**
     * Entity를 DTO로 변환
     */
    public static PermissionResponse from(FilePermission permission) {
        return PermissionResponse.builder()
            .id(permission.getId())
            .fileId(permission.getFile().getId())
            .fileName(permission.getFile().getOriginalName())
            .userId(permission.getUser().getId())
            .userName(permission.getUser().getFullName())
            .userEmail(permission.getUser().getEmail())
            .permission(permission.getPermission())
            .grantedById(permission.getGrantedBy() != null ? permission.getGrantedBy().getId() : null)
            .grantedByName(permission.getGrantedBy() != null ? permission.getGrantedBy().getFullName() : null)
            .createdAt(permission.getCreatedAt())
            .build();
    }
}
