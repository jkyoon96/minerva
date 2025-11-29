package com.eduforum.api.domain.file.dto;

import com.eduforum.api.domain.file.entity.StoredFile;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * 파일 응답 DTO
 */
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "파일 정보")
public class FileResponse {

    @Schema(description = "파일 ID")
    private Long id;

    @Schema(description = "원본 파일명")
    private String originalName;

    @Schema(description = "파일 크기 (bytes)")
    private Long size;

    @Schema(description = "파일 크기 (포맷됨)")
    private String formattedSize;

    @Schema(description = "MIME 타입")
    private String mimeType;

    @Schema(description = "확장자")
    private String extension;

    @Schema(description = "파일 URL")
    private String url;

    @Schema(description = "다운로드 URL")
    private String downloadUrl;

    @Schema(description = "코스 ID")
    private Long courseId;

    @Schema(description = "폴더 ID")
    private Long folderId;

    @Schema(description = "폴더명")
    private String folderName;

    @Schema(description = "설명")
    private String description;

    @Schema(description = "공개 여부")
    private Boolean isPublic;

    @Schema(description = "다운로드 횟수")
    private Long downloadCount;

    @Schema(description = "업로드자 ID")
    private Long uploadedById;

    @Schema(description = "업로드자 이름")
    private String uploadedByName;

    @Schema(description = "생성일시")
    private LocalDateTime createdAt;

    @Schema(description = "수정일시")
    private LocalDateTime updatedAt;

    /**
     * Entity를 DTO로 변환
     */
    public static FileResponse from(StoredFile file) {
        return FileResponse.builder()
            .id(file.getId())
            .originalName(file.getOriginalName())
            .size(file.getSize())
            .formattedSize(file.getFormattedSize())
            .mimeType(file.getMimeType())
            .extension(file.getExtension())
            .url(file.getUrl())
            .downloadUrl("/api/v1/files/" + file.getId() + "/download")
            .courseId(file.getCourse() != null ? file.getCourse().getId() : null)
            .folderId(file.getFolder() != null ? file.getFolder().getId() : null)
            .folderName(file.getFolder() != null ? file.getFolder().getName() : null)
            .description(file.getDescription())
            .isPublic(file.getIsPublic())
            .downloadCount(file.getDownloadCount())
            .uploadedById(file.getUploadedBy().getId())
            .uploadedByName(file.getUploadedBy().getFullName())
            .createdAt(file.getCreatedAt())
            .updatedAt(file.getUpdatedAt())
            .build();
    }
}
