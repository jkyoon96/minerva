package com.eduforum.api.domain.file.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * 파일 업로드 요청 DTO
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "파일 업로드 요청")
public class FileUploadRequest {

    @Schema(description = "코스 ID")
    @NotNull(message = "코스 ID는 필수입니다")
    private Long courseId;

    @Schema(description = "폴더 ID (null이면 최상위)")
    private Long folderId;

    @Schema(description = "파일 설명")
    private String description;

    @Schema(description = "공개 여부")
    @Builder.Default
    private Boolean isPublic = false;
}
