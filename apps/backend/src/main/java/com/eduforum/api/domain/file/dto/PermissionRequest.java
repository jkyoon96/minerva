package com.eduforum.api.domain.file.dto;

import com.eduforum.api.domain.file.entity.FilePermission;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * 파일 권한 설정 요청 DTO
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "파일 권한 설정 요청")
public class PermissionRequest {

    @Schema(description = "사용자 ID")
    @NotNull(message = "사용자 ID는 필수입니다")
    private Long userId;

    @Schema(description = "권한 타입 (READ, WRITE, DELETE, OWNER)")
    @NotNull(message = "권한 타입은 필수입니다")
    private FilePermission.PermissionType permission;
}
