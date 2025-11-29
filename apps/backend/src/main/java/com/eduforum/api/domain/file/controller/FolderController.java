package com.eduforum.api.domain.file.controller;

import com.eduforum.api.common.dto.ApiResponse;
import com.eduforum.api.domain.file.dto.FolderRequest;
import com.eduforum.api.domain.file.dto.FolderResponse;
import com.eduforum.api.domain.file.service.FolderService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 폴더 관리 컨트롤러
 */
@Slf4j
@RestController
@RequestMapping("/v1/folders")
@RequiredArgsConstructor
@Tag(name = "Folder", description = "폴더 관리 API")
public class FolderController {

    private final FolderService folderService;

    @Operation(summary = "폴더 생성", description = "새 폴더를 생성합니다")
    @PostMapping
    @SecurityRequirement(name = "bearer-jwt")
    public ResponseEntity<ApiResponse<FolderResponse>> createFolder(
        @Valid @RequestBody FolderRequest request
    ) {
        FolderResponse response = folderService.createFolder(request);
        return ResponseEntity.ok(ApiResponse.success("폴더가 생성되었습니다", response));
    }

    @Operation(summary = "폴더 조회", description = "폴더 정보를 조회합니다")
    @GetMapping("/{folderId}")
    @SecurityRequirement(name = "bearer-jwt")
    public ResponseEntity<ApiResponse<FolderResponse>> getFolder(
        @PathVariable Long folderId,
        @RequestParam(defaultValue = "false") boolean includeChildren,
        @RequestParam(defaultValue = "false") boolean includeFiles
    ) {
        FolderResponse response = folderService.getFolder(folderId, includeChildren, includeFiles);
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @Operation(summary = "폴더 수정", description = "폴더 정보를 수정합니다")
    @PutMapping("/{folderId}")
    @SecurityRequirement(name = "bearer-jwt")
    public ResponseEntity<ApiResponse<FolderResponse>> updateFolder(
        @PathVariable Long folderId,
        @Valid @RequestBody FolderRequest request
    ) {
        FolderResponse response = folderService.updateFolder(folderId, request);
        return ResponseEntity.ok(ApiResponse.success("폴더가 수정되었습니다", response));
    }

    @Operation(summary = "폴더 삭제", description = "폴더를 삭제합니다 (하위 폴더/파일 포함)")
    @DeleteMapping("/{folderId}")
    @SecurityRequirement(name = "bearer-jwt")
    public ResponseEntity<ApiResponse<Void>> deleteFolder(@PathVariable Long folderId) {
        folderService.deleteFolder(folderId);
        return ResponseEntity.ok(ApiResponse.success("폴더가 삭제되었습니다"));
    }

    @Operation(summary = "코스 폴더 트리", description = "코스의 전체 폴더 구조를 트리 형태로 조회합니다")
    @GetMapping("/courses/{courseId}/tree")
    @SecurityRequirement(name = "bearer-jwt")
    public ResponseEntity<ApiResponse<List<FolderResponse>>> getCourseFolderTree(@PathVariable Long courseId) {
        List<FolderResponse> response = folderService.getCourseFolderTree(courseId);
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @Operation(summary = "최상위 폴더 목록", description = "코스의 최상위 폴더 목록을 조회합니다")
    @GetMapping("/courses/{courseId}/root")
    @SecurityRequirement(name = "bearer-jwt")
    public ResponseEntity<ApiResponse<List<FolderResponse>>> getRootFolders(@PathVariable Long courseId) {
        List<FolderResponse> response = folderService.getRootFolders(courseId);
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @Operation(summary = "하위 폴더 목록", description = "폴더의 하위 폴더 목록을 조회합니다")
    @GetMapping("/{folderId}/children")
    @SecurityRequirement(name = "bearer-jwt")
    public ResponseEntity<ApiResponse<List<FolderResponse>>> getSubFolders(@PathVariable Long folderId) {
        List<FolderResponse> response = folderService.getSubFolders(folderId);
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @Operation(summary = "폴더 검색", description = "폴더명으로 검색합니다")
    @GetMapping("/search")
    @SecurityRequirement(name = "bearer-jwt")
    public ResponseEntity<ApiResponse<List<FolderResponse>>> searchFolders(
        @RequestParam Long courseId,
        @RequestParam String keyword
    ) {
        List<FolderResponse> response = folderService.searchFolders(courseId, keyword);
        return ResponseEntity.ok(ApiResponse.success(response));
    }
}
