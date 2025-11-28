package com.eduforum.api.domain.seminar.controller;

import com.eduforum.api.common.dto.ApiResponse;
import com.eduforum.api.domain.seminar.dto.ChatMessageRequest;
import com.eduforum.api.domain.seminar.dto.ChatMessageResponse;
import com.eduforum.api.domain.seminar.service.ChatService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.OffsetDateTime;
import java.util.List;

/**
 * REST API controller for chat messages
 */
@RestController
@RequestMapping("/v1/rooms/{roomId}/messages")
@RequiredArgsConstructor
@Tag(name = "Chat", description = "채팅 메시지 API")
public class ChatController {

    private final ChatService chatService;

    @PostMapping
    @Operation(summary = "메시지 전송", description = "채팅 메시지를 전송합니다")
    @ApiResponses({
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "메시지 전송 성공"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "채팅이 비활성화됨"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "403", description = "룸에 참가하지 않음")
    })
    public ResponseEntity<ApiResponse<ChatMessageResponse>> sendMessage(
            @RequestHeader("X-User-Id") Long userId,
            @Parameter(description = "룸 ID") @PathVariable Long roomId,
            @Valid @RequestBody ChatMessageRequest request) {
        request.setRoomId(roomId); // Ensure roomId from path is used
        ChatMessageResponse response = chatService.sendMessage(userId, request);
        return ResponseEntity.ok(ApiResponse.success("메시지가 전송되었습니다", response));
    }

    @GetMapping
    @Operation(summary = "메시지 히스토리", description = "채팅 메시지 히스토리를 페이징하여 조회합니다")
    public ResponseEntity<ApiResponse<Page<ChatMessageResponse>>> getMessages(
            @Parameter(description = "룸 ID") @PathVariable Long roomId,
            @PageableDefault(size = 50, sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable) {
        Page<ChatMessageResponse> messages = chatService.getMessages(roomId, pageable);
        return ResponseEntity.ok(ApiResponse.success(messages));
    }

    @GetMapping("/recent")
    @Operation(summary = "최근 메시지", description = "최근 N개의 메시지를 조회합니다")
    public ResponseEntity<ApiResponse<List<ChatMessageResponse>>> getRecentMessages(
            @Parameter(description = "룸 ID") @PathVariable Long roomId,
            @Parameter(description = "조회할 메시지 개수") @RequestParam(defaultValue = "50") int limit) {
        List<ChatMessageResponse> messages = chatService.getRecentMessages(roomId, limit);
        return ResponseEntity.ok(ApiResponse.success(messages));
    }

    @GetMapping("/since")
    @Operation(summary = "특정 시간 이후 메시지", description = "특정 시간 이후의 메시지를 조회합니다 (실시간 동기화용)")
    public ResponseEntity<ApiResponse<List<ChatMessageResponse>>> getMessagesSince(
            @Parameter(description = "룸 ID") @PathVariable Long roomId,
            @Parameter(description = "기준 시간 (ISO-8601)") @RequestParam OffsetDateTime since) {
        List<ChatMessageResponse> messages = chatService.getMessagesSince(roomId, since);
        return ResponseEntity.ok(ApiResponse.success(messages));
    }

    @GetMapping("/files")
    @Operation(summary = "공유된 파일 목록", description = "룸에서 공유된 파일 목록을 조회합니다")
    public ResponseEntity<ApiResponse<List<ChatMessageResponse>>> getSharedFiles(
            @Parameter(description = "룸 ID") @PathVariable Long roomId) {
        List<ChatMessageResponse> files = chatService.getSharedFiles(roomId);
        return ResponseEntity.ok(ApiResponse.success(files));
    }

    @GetMapping("/count")
    @Operation(summary = "메시지 개수", description = "룸의 총 메시지 개수를 조회합니다")
    public ResponseEntity<ApiResponse<Long>> getMessageCount(
            @Parameter(description = "룸 ID") @PathVariable Long roomId) {
        Long count = chatService.getMessageCount(roomId);
        return ResponseEntity.ok(ApiResponse.success(count));
    }

    // File upload endpoint would be handled separately with multipart/form-data
    // This is just a placeholder for the API structure
    @PostMapping("/files")
    @Operation(summary = "파일 업로드", description = "파일을 업로드하고 채팅에 공유합니다")
    public ResponseEntity<ApiResponse<ChatMessageResponse>> uploadFile(
            @RequestHeader("X-User-Id") Long userId,
            @Parameter(description = "룸 ID") @PathVariable Long roomId,
            @RequestParam String fileName,
            @RequestParam String fileUrl,
            @RequestParam Long fileSize) {
        ChatMessageResponse response = chatService.uploadFile(userId, roomId, fileName, fileUrl, fileSize);
        return ResponseEntity.ok(ApiResponse.success("파일이 업로드되었습니다", response));
    }
}
