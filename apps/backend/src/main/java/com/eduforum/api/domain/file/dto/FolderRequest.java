package com.eduforum.api.domain.file.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * 폴더 생성/수정 요청 DTO
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "폴더 생성/수정 요청")
public class FolderRequest {

    @Schema(description = "폴더명")
    @NotBlank(message = "폴더명은 필수입니다")
    @Size(max = 255, message = "폴더명은 255자를 초과할 수 없습니다")
    private String name;

    @Schema(description = "폴더 설명")
    private String description;

    @Schema(description = "코스 ID (생성 시 필수)")
    @NotNull(message = "코스 ID는 필수입니다")
    private Long courseId;

    @Schema(description = "부모 폴더 ID (null이면 최상위)")
    private Long parentId;

    @Schema(description = "공개 여부")
    @Builder.Default
    private Boolean isPublic = false;

    @Schema(description = "정렬 순서")
    @Builder.Default
    private Integer sortOrder = 0;
}
