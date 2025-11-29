package com.eduforum.api.domain.file.dto;

import com.eduforum.api.domain.file.entity.FileFolder;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 폴더 응답 DTO
 */
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "폴더 정보")
public class FolderResponse {

    @Schema(description = "폴더 ID")
    private Long id;

    @Schema(description = "폴더명")
    private String name;

    @Schema(description = "폴더 설명")
    private String description;

    @Schema(description = "코스 ID")
    private Long courseId;

    @Schema(description = "부모 폴더 ID")
    private Long parentId;

    @Schema(description = "부모 폴더명")
    private String parentName;

    @Schema(description = "전체 경로")
    private String fullPath;

    @Schema(description = "깊이")
    private Integer depth;

    @Schema(description = "공개 여부")
    private Boolean isPublic;

    @Schema(description = "정렬 순서")
    private Integer sortOrder;

    @Schema(description = "하위 폴더 수")
    private Integer childFolderCount;

    @Schema(description = "파일 수")
    private Integer fileCount;

    @Schema(description = "하위 폴더 목록")
    private List<FolderResponse> children;

    @Schema(description = "파일 목록")
    private List<FileResponse> files;

    @Schema(description = "생성자 ID")
    private Long createdById;

    @Schema(description = "생성자 이름")
    private String createdByName;

    @Schema(description = "생성일시")
    private LocalDateTime createdAt;

    @Schema(description = "수정일시")
    private LocalDateTime updatedAt;

    /**
     * Entity를 DTO로 변환 (하위 폴더/파일 제외)
     */
    public static FolderResponse from(FileFolder folder) {
        return from(folder, false, false);
    }

    /**
     * Entity를 DTO로 변환
     *
     * @param folder 폴더 엔티티
     * @param includeChildren 하위 폴더 포함 여부
     * @param includeFiles 파일 포함 여부
     */
    public static FolderResponse from(FileFolder folder, boolean includeChildren, boolean includeFiles) {
        FolderResponseBuilder builder = FolderResponse.builder()
            .id(folder.getId())
            .name(folder.getName())
            .description(folder.getDescription())
            .courseId(folder.getCourse().getId())
            .parentId(folder.getParent() != null ? folder.getParent().getId() : null)
            .parentName(folder.getParent() != null ? folder.getParent().getName() : null)
            .fullPath(folder.getFullPath())
            .depth(folder.getDepth())
            .isPublic(folder.getIsPublic())
            .sortOrder(folder.getSortOrder())
            .childFolderCount(folder.getChildren().size())
            .fileCount(folder.getFiles().size())
            .createdById(folder.getCreatedByUser().getId())
            .createdByName(folder.getCreatedByUser().getFullName())
            .createdAt(folder.getCreatedAt())
            .updatedAt(folder.getUpdatedAt());

        if (includeChildren) {
            builder.children(folder.getChildren().stream()
                .map(FolderResponse::from)
                .collect(Collectors.toList()));
        }

        if (includeFiles) {
            builder.files(folder.getFiles().stream()
                .filter(file -> !file.getIsDeleted())
                .map(FileResponse::from)
                .collect(Collectors.toList()));
        }

        return builder.build();
    }
}
