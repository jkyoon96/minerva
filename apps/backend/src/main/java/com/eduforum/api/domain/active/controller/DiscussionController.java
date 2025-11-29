package com.eduforum.api.domain.active.controller;

import com.eduforum.api.common.dto.ApiResponse;
import com.eduforum.api.domain.active.dto.discussion.*;
import com.eduforum.api.domain.active.service.DiscussionService;
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
@Tag(name = "Discussion", description = "토론 및 발언 관리 API")
public class DiscussionController {

    private final DiscussionService discussionService;

    // Speaking Queue Endpoints

    @PostMapping("/seminars/{roomId}/speaking-queue")
    @Operation(summary = "발언 대기열 참가", description = "발언 대기열에 참가합니다")
    public ResponseEntity<ApiResponse<SpeakingQueueResponse>> joinSpeakingQueue(
            @Parameter(hidden = true) @RequestHeader("X-User-Id") Long userId,
            @PathVariable Long roomId,
            @Valid @RequestBody JoinQueueRequest request) {
        SpeakingQueueResponse response = discussionService.joinSpeakingQueue(userId, roomId, request);
        return ResponseEntity.status(HttpStatus.CREATED)
            .body(ApiResponse.success("대기열에 참가했습니다", response));
    }

    @DeleteMapping("/speaking-queue/{queueId}")
    @Operation(summary = "발언 대기열 나가기", description = "발언 대기열에서 나갑니다")
    public ResponseEntity<ApiResponse<Void>> leaveSpeakingQueue(
            @Parameter(hidden = true) @RequestHeader("X-User-Id") Long userId,
            @PathVariable Long queueId) {
        discussionService.leaveSpeakingQueue(userId, queueId);
        return ResponseEntity.ok(ApiResponse.success("대기열에서 나갔습니다", null));
    }

    @GetMapping("/seminars/{roomId}/speaking-queue")
    @Operation(summary = "발언 대기열 조회", description = "발언 대기열을 조회합니다")
    public ResponseEntity<ApiResponse<List<SpeakingQueueResponse>>> getSpeakingQueue(
            @PathVariable Long roomId) {
        List<SpeakingQueueResponse> responses = discussionService.getSpeakingQueue(roomId);
        return ResponseEntity.ok(ApiResponse.success(responses));
    }

    @PostMapping("/speaking-queue/{queueId}/grant")
    @Operation(summary = "발언권 부여", description = "대기 중인 참여자에게 발언권을 부여합니다")
    public ResponseEntity<ApiResponse<SpeakingQueueResponse>> grantSpeakingTurn(
            @Parameter(hidden = true) @RequestHeader("X-User-Id") Long userId,
            @PathVariable Long queueId) {
        SpeakingQueueResponse response = discussionService.grantSpeakingTurn(userId, queueId);
        return ResponseEntity.ok(ApiResponse.success("발언권이 부여되었습니다", response));
    }

    @GetMapping("/seminars/{roomId}/participation")
    @Operation(summary = "참여 통계 조회", description = "세미나의 참여 통계를 조회합니다")
    public ResponseEntity<ApiResponse<ParticipationStatsResponse>> getParticipationStats(
            @PathVariable Long roomId) {
        ParticipationStatsResponse response = discussionService.getParticipationStats(roomId);
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    // Discussion Thread Endpoints

    @PostMapping("/seminars/{roomId}/threads")
    @Operation(summary = "토론 스레드 생성", description = "새로운 토론 스레드를 생성합니다")
    public ResponseEntity<ApiResponse<ThreadResponse>> createThread(
            @Parameter(hidden = true) @RequestHeader("X-User-Id") Long userId,
            @PathVariable Long roomId,
            @Valid @RequestBody CreateThreadRequest request) {
        ThreadResponse response = discussionService.createThread(userId, roomId, request);
        return ResponseEntity.status(HttpStatus.CREATED)
            .body(ApiResponse.success("스레드가 생성되었습니다", response));
    }

    @GetMapping("/seminars/{roomId}/threads")
    @Operation(summary = "토론 스레드 목록", description = "세미나의 모든 토론 스레드를 조회합니다")
    public ResponseEntity<ApiResponse<List<ThreadResponse>>> getThreads(
            @PathVariable Long roomId) {
        List<ThreadResponse> responses = discussionService.getThreads(roomId);
        return ResponseEntity.ok(ApiResponse.success(responses));
    }

    @GetMapping("/threads/{threadId}")
    @Operation(summary = "토론 스레드 조회", description = "토론 스레드를 조회합니다")
    public ResponseEntity<ApiResponse<ThreadResponse>> getThread(
            @PathVariable Long threadId) {
        ThreadResponse response = discussionService.getThread(threadId);
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @PostMapping("/threads/{threadId}/replies")
    @Operation(summary = "토론 스레드 답글", description = "토론 스레드에 답글을 작성합니다")
    public ResponseEntity<ApiResponse<ThreadResponse>> replyToThread(
            @Parameter(hidden = true) @RequestHeader("X-User-Id") Long userId,
            @PathVariable Long threadId,
            @Valid @RequestBody CreateThreadRequest request) {
        ThreadResponse response = discussionService.replyToThread(userId, threadId, request);
        return ResponseEntity.status(HttpStatus.CREATED)
            .body(ApiResponse.success("답글이 작성되었습니다", response));
    }

    @DeleteMapping("/threads/{threadId}")
    @Operation(summary = "토론 스레드 삭제", description = "토론 스레드를 삭제합니다")
    public ResponseEntity<ApiResponse<Void>> deleteThread(
            @Parameter(hidden = true) @RequestHeader("X-User-Id") Long userId,
            @PathVariable Long threadId) {
        discussionService.deleteThread(userId, threadId);
        return ResponseEntity.ok(ApiResponse.success("스레드가 삭제되었습니다", null));
    }
}
