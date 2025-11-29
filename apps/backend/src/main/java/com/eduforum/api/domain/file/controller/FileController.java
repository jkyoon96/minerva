package com.eduforum.api.domain.file.controller;

import com.eduforum.api.common.dto.ApiResponse;
import com.eduforum.api.domain.file.dto.FileResponse;
import com.eduforum.api.domain.file.dto.FileSearchRequest;
import com.eduforum.api.domain.file.dto.FileUploadRequest;
import com.eduforum.api.domain.file.dto.PermissionRequest;
import com.eduforum.api.domain.file.dto.PermissionResponse;
import com.eduforum.api.domain.file.service.FilePermissionService;
import com.eduforum.api.domain.file.service.FileSearchService;
import com.eduforum.api.domain.file.service.FileService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.Resource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

/**
 * 파일 관리 컨트롤러
 */
@Slf4j
@RestController
@RequestMapping("/v1/files")
@RequiredArgsConstructor
@Tag(name = "File", description = "파일 관리 API")
public class FileController {

    private final FileService fileService;
    private final FileSearchService fileSearchService;
    private final FilePermissionService permissionService;

    @Operation(summary = "파일 업로드", description = "파일을 업로드합니다")
    @PostMapping(value = "/upload", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @SecurityRequirement(name = "bearer-jwt")
    public ResponseEntity<ApiResponse<FileResponse>> uploadFile(
        @RequestPart("file") MultipartFile file,
        @RequestPart("request") @Valid FileUploadRequest request
    ) {
        FileResponse response = fileService.uploadFile(file, request);
        return ResponseEntity.ok(ApiResponse.success("파일이 업로드되었습니다", response));
    }

    @Operation(summary = "파일 조회", description = "파일 정보를 조회합니다")
    @GetMapping("/{fileId}")
    @SecurityRequirement(name = "bearer-jwt")
    public ResponseEntity<ApiResponse<FileResponse>> getFile(@PathVariable Long fileId) {
        FileResponse response = fileService.getFile(fileId);
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @Operation(summary = "파일 다운로드", description = "파일을 다운로드합니다")
    @GetMapping("/{fileId}/download")
    @SecurityRequirement(name = "bearer-jwt")
    public ResponseEntity<Resource> downloadFile(@PathVariable Long fileId) {
        Resource resource = fileService.downloadFile(fileId);
        FileResponse fileInfo = fileService.getFile(fileId);

        return ResponseEntity.ok()
            .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + fileInfo.getOriginalName() + "\"")
            .contentType(MediaType.parseMediaType(fileInfo.getMimeType()))
            .body(resource);
    }

    @Operation(summary = "파일 삭제", description = "파일을 삭제합니다")
    @DeleteMapping("/{fileId}")
    @SecurityRequirement(name = "bearer-jwt")
    public ResponseEntity<ApiResponse<Void>> deleteFile(@PathVariable Long fileId) {
        fileService.deleteFile(fileId);
        return ResponseEntity.ok(ApiResponse.success("파일이 삭제되었습니다"));
    }

    @Operation(summary = "파일 공개 설정 변경", description = "파일의 공개/비공개를 설정합니다")
    @PutMapping("/{fileId}/visibility")
    @SecurityRequirement(name = "bearer-jwt")
    public ResponseEntity<ApiResponse<FileResponse>> updateFileVisibility(
        @PathVariable Long fileId,
        @RequestParam boolean isPublic
    ) {
        FileResponse response = fileService.updateFileVisibility(fileId, isPublic);
        return ResponseEntity.ok(ApiResponse.success("파일 공개 설정이 변경되었습니다", response));
    }

    @Operation(summary = "파일 설명 수정", description = "파일의 설명을 수정합니다")
    @PutMapping("/{fileId}/description")
    @SecurityRequirement(name = "bearer-jwt")
    public ResponseEntity<ApiResponse<FileResponse>> updateFileDescription(
        @PathVariable Long fileId,
        @RequestParam String description
    ) {
        FileResponse response = fileService.updateFileDescription(fileId, description);
        return ResponseEntity.ok(ApiResponse.success("파일 설명이 수정되었습니다", response));
    }

    @Operation(summary = "다운로드 URL 생성", description = "임시 다운로드 URL을 생성합니다 (Pre-signed URL)")
    @GetMapping("/{fileId}/download-url")
    @SecurityRequirement(name = "bearer-jwt")
    public ResponseEntity<ApiResponse<String>> generateDownloadUrl(
        @PathVariable Long fileId,
        @RequestParam(defaultValue = "60") int expirationMinutes
    ) {
        String url = fileService.generateDownloadUrl(fileId, expirationMinutes);
        return ResponseEntity.ok(ApiResponse.success(url));
    }

    @Operation(summary = "파일 검색", description = "파일을 검색합니다")
    @GetMapping("/search")
    @SecurityRequirement(name = "bearer-jwt")
    public ResponseEntity<ApiResponse<Page<FileResponse>>> searchFiles(
        @RequestParam Long courseId,
        @ModelAttribute FileSearchRequest searchRequest
    ) {
        Page<FileResponse> response = fileSearchService.searchFiles(courseId, searchRequest);
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @Operation(summary = "코스별 파일 목록", description = "코스의 모든 파일을 조회합니다")
    @GetMapping("/courses/{courseId}")
    @SecurityRequirement(name = "bearer-jwt")
    public ResponseEntity<ApiResponse<Page<FileResponse>>> getCourseFiles(
        @PathVariable Long courseId,
        @RequestParam(defaultValue = "0") int page,
        @RequestParam(defaultValue = "20") int size
    ) {
        Page<FileResponse> response = fileService.getCourseFiles(courseId, PageRequest.of(page, size));
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @Operation(summary = "폴더별 파일 목록", description = "폴더의 모든 파일을 조회합니다")
    @GetMapping("/folders/{folderId}")
    @SecurityRequirement(name = "bearer-jwt")
    public ResponseEntity<ApiResponse<List<FileResponse>>> getFolderFiles(@PathVariable Long folderId) {
        List<FileResponse> response = fileService.getFolderFiles(folderId);
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    // 권한 관리

    @Operation(summary = "파일 권한 부여", description = "사용자에게 파일 권한을 부여합니다")
    @PostMapping("/{fileId}/permissions")
    @SecurityRequirement(name = "bearer-jwt")
    public ResponseEntity<ApiResponse<PermissionResponse>> grantPermission(
        @PathVariable Long fileId,
        @Valid @RequestBody PermissionRequest request
    ) {
        PermissionResponse response = permissionService.grantPermission(fileId, request);
        return ResponseEntity.ok(ApiResponse.success("권한이 부여되었습니다", response));
    }

    @Operation(summary = "파일 권한 제거", description = "사용자의 파일 권한을 제거합니다")
    @DeleteMapping("/{fileId}/permissions/{userId}")
    @SecurityRequirement(name = "bearer-jwt")
    public ResponseEntity<ApiResponse<Void>> revokePermission(
        @PathVariable Long fileId,
        @PathVariable Long userId
    ) {
        permissionService.revokePermission(fileId, userId);
        return ResponseEntity.ok(ApiResponse.success("권한이 제거되었습니다"));
    }

    @Operation(summary = "파일 권한 목록", description = "파일의 모든 권한을 조회합니다")
    @GetMapping("/{fileId}/permissions")
    @SecurityRequirement(name = "bearer-jwt")
    public ResponseEntity<ApiResponse<List<PermissionResponse>>> getFilePermissions(@PathVariable Long fileId) {
        List<PermissionResponse> response = permissionService.getFilePermissions(fileId);
        return ResponseEntity.ok(ApiResponse.success(response));
    }
}
