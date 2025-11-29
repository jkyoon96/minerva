package com.eduforum.api.domain.active.controller;

import com.eduforum.api.common.dto.ApiResponse;
import com.eduforum.api.domain.active.dto.whiteboard.*;
import com.eduforum.api.domain.active.service.WhiteboardService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/v1")
@RequiredArgsConstructor
@Tag(name = "Whiteboard", description = "화이트보드 관리 API")
public class WhiteboardController {

    private final WhiteboardService whiteboardService;

    @PostMapping("/seminars/{roomId}/whiteboards")
    @Operation(summary = "화이트보드 생성", description = "세미나에 새로운 화이트보드를 생성합니다")
    public ResponseEntity<ApiResponse<WhiteboardResponse>> createWhiteboard(
            @Parameter(hidden = true) @RequestHeader("X-User-Id") Long userId,
            @PathVariable Long roomId,
            @Valid @RequestBody CreateWhiteboardRequest request) {
        WhiteboardResponse response = whiteboardService.createWhiteboard(userId, roomId, request);
        return ResponseEntity.status(HttpStatus.CREATED)
            .body(ApiResponse.success("화이트보드가 생성되었습니다", response));
    }

    @GetMapping("/whiteboards/{whiteboardId}")
    @Operation(summary = "화이트보드 조회", description = "화이트보드 상태를 조회합니다")
    public ResponseEntity<ApiResponse<WhiteboardResponse>> getWhiteboard(
            @PathVariable Long whiteboardId) {
        WhiteboardResponse response = whiteboardService.getWhiteboard(whiteboardId);
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @GetMapping("/seminars/{roomId}/whiteboards")
    @Operation(summary = "화이트보드 목록", description = "세미나의 모든 화이트보드를 조회합니다")
    public ResponseEntity<ApiResponse<List<WhiteboardResponse>>> getWhiteboardsBySeminar(
            @PathVariable Long roomId) {
        List<WhiteboardResponse> responses = whiteboardService.getWhiteboardsBySeminar(roomId);
        return ResponseEntity.ok(ApiResponse.success(responses));
    }

    @PutMapping("/whiteboards/{whiteboardId}")
    @Operation(summary = "화이트보드 저장", description = "화이트보드 상태를 저장합니다")
    public ResponseEntity<ApiResponse<WhiteboardResponse>> saveWhiteboard(
            @Parameter(hidden = true) @RequestHeader("X-User-Id") Long userId,
            @PathVariable Long whiteboardId,
            @Valid @RequestBody SaveWhiteboardRequest request) {
        WhiteboardResponse response = whiteboardService.saveWhiteboardState(userId, whiteboardId, request);
        return ResponseEntity.ok(ApiResponse.success("화이트보드가 저장되었습니다", response));
    }

    @PostMapping("/whiteboards/{whiteboardId}/elements")
    @Operation(summary = "요소 추가", description = "화이트보드에 요소를 추가합니다")
    public ResponseEntity<ApiResponse<Void>> addElement(
            @PathVariable Long whiteboardId,
            @Valid @RequestBody WhiteboardElementDto dto) {
        whiteboardService.addElement(whiteboardId, dto);
        return ResponseEntity.ok(ApiResponse.success("요소가 추가되었습니다", null));
    }

    @PutMapping("/whiteboards/{whiteboardId}/elements/{elementId}")
    @Operation(summary = "요소 수정", description = "화이트보드 요소를 수정합니다")
    public ResponseEntity<ApiResponse<Void>> updateElement(
            @PathVariable Long whiteboardId,
            @PathVariable String elementId,
            @Valid @RequestBody WhiteboardElementDto dto) {
        whiteboardService.updateElement(whiteboardId, elementId, dto);
        return ResponseEntity.ok(ApiResponse.success("요소가 수정되었습니다", null));
    }

    @DeleteMapping("/whiteboards/{whiteboardId}/elements/{elementId}")
    @Operation(summary = "요소 삭제", description = "화이트보드 요소를 삭제합니다")
    public ResponseEntity<ApiResponse<Void>> removeElement(
            @PathVariable Long whiteboardId,
            @PathVariable String elementId) {
        whiteboardService.removeElement(whiteboardId, elementId);
        return ResponseEntity.ok(ApiResponse.success("요소가 삭제되었습니다", null));
    }

    @PostMapping("/whiteboards/{whiteboardId}/clear")
    @Operation(summary = "화이트보드 초기화", description = "화이트보드의 모든 요소를 삭제합니다")
    public ResponseEntity<ApiResponse<Void>> clearWhiteboard(
            @Parameter(hidden = true) @RequestHeader("X-User-Id") Long userId,
            @PathVariable Long whiteboardId) {
        whiteboardService.clearWhiteboard(userId, whiteboardId);
        return ResponseEntity.ok(ApiResponse.success("화이트보드가 초기화되었습니다", null));
    }

    @DeleteMapping("/whiteboards/{whiteboardId}")
    @Operation(summary = "화이트보드 삭제", description = "화이트보드를 삭제합니다")
    public ResponseEntity<ApiResponse<Void>> deleteWhiteboard(
            @Parameter(hidden = true) @RequestHeader("X-User-Id") Long userId,
            @PathVariable Long whiteboardId) {
        whiteboardService.deleteWhiteboard(userId, whiteboardId);
        return ResponseEntity.ok(ApiResponse.success("화이트보드가 삭제되었습니다", null));
    }

    @GetMapping("/whiteboards/{whiteboardId}/export")
    @Operation(summary = "이미지 내보내기", description = "화이트보드를 이미지로 내보냅니다 (placeholder)")
    public ResponseEntity<ApiResponse<String>> exportWhiteboardImage(
            @PathVariable Long whiteboardId) {
        String imageData = whiteboardService.exportWhiteboardImage(whiteboardId);
        return ResponseEntity.ok(ApiResponse.success("이미지가 생성되었습니다", imageData));
    }
}
