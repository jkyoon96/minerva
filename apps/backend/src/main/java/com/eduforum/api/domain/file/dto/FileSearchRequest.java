package com.eduforum.api.domain.file.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * 파일 검색 요청 DTO
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "파일 검색 요청")
public class FileSearchRequest {

    @Schema(description = "검색 키워드")
    private String keyword;

    @Schema(description = "MIME 타입 필터 (예: image/*, application/pdf)")
    private String mimeType;

    @Schema(description = "폴더 ID")
    private Long folderId;

    @Schema(description = "최소 파일 크기 (bytes)")
    private Long minSize;

    @Schema(description = "최대 파일 크기 (bytes)")
    private Long maxSize;

    @Schema(description = "공개 파일만 조회")
    @Builder.Default
    private Boolean publicOnly = false;

    @Schema(description = "페이지 번호 (0부터 시작)")
    @Builder.Default
    private Integer page = 0;

    @Schema(description = "페이지 크기")
    @Builder.Default
    private Integer size = 20;

    @Schema(description = "정렬 기준 (name, size, createdAt)")
    @Builder.Default
    private String sortBy = "createdAt";

    @Schema(description = "정렬 방향 (asc, desc)")
    @Builder.Default
    private String sortDirection = "desc";
}
