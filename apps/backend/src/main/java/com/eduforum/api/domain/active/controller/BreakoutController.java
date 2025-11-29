package com.eduforum.api.domain.active.controller;

import com.eduforum.api.common.dto.ApiResponse;
import com.eduforum.api.domain.active.dto.breakout.*;
import com.eduforum.api.domain.active.service.BreakoutService;
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
@Tag(name = "Breakout Rooms", description = "분반 토론 관리 API")
public class BreakoutController {

    private final BreakoutService breakoutService;

    @PostMapping("/seminars/{roomId}/breakouts")
    @Operation(summary = "분반 생성", description = "세미나에 새로운 분반을 생성합니다")
    public ResponseEntity<ApiResponse<BreakoutResponse>> createBreakoutRoom(
            @Parameter(hidden = true) @RequestHeader("X-User-Id") Long userId,
            @PathVariable Long roomId,
            @Valid @RequestBody CreateBreakoutRequest request) {
        BreakoutResponse response = breakoutService.createBreakoutRoom(userId, roomId, request);
        return ResponseEntity.status(HttpStatus.CREATED)
            .body(ApiResponse.success("분반이 생성되었습니다", response));
    }

    @GetMapping("/seminars/{roomId}/breakouts")
    @Operation(summary = "분반 목록 조회", description = "세미나의 모든 분반을 조회합니다")
    public ResponseEntity<ApiResponse<List<BreakoutResponse>>> getBreakoutRooms(
            @PathVariable Long roomId) {
        List<BreakoutResponse> responses = breakoutService.getBreakoutRooms(roomId);
        return ResponseEntity.ok(ApiResponse.success(responses));
    }

    @GetMapping("/breakouts/{breakoutId}")
    @Operation(summary = "분반 조회", description = "분반 정보를 조회합니다")
    public ResponseEntity<ApiResponse<BreakoutResponse>> getBreakoutRoom(
            @PathVariable Long breakoutId) {
        BreakoutResponse response = breakoutService.getBreakoutRoom(breakoutId);
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @PutMapping("/breakouts/{breakoutId}")
    @Operation(summary = "분반 수정", description = "분반 정보를 수정합니다")
    public ResponseEntity<ApiResponse<BreakoutResponse>> updateBreakoutRoom(
            @Parameter(hidden = true) @RequestHeader("X-User-Id") Long userId,
            @PathVariable Long breakoutId,
            @Valid @RequestBody CreateBreakoutRequest request) {
        BreakoutResponse response = breakoutService.updateBreakoutRoom(userId, breakoutId, request);
        return ResponseEntity.ok(ApiResponse.success("분반이 수정되었습니다", response));
    }

    @DeleteMapping("/breakouts/{breakoutId}")
    @Operation(summary = "분반 삭제", description = "분반을 삭제합니다")
    public ResponseEntity<ApiResponse<Void>> deleteBreakoutRoom(
            @Parameter(hidden = true) @RequestHeader("X-User-Id") Long userId,
            @PathVariable Long breakoutId) {
        breakoutService.deleteBreakoutRoom(userId, breakoutId);
        return ResponseEntity.ok(ApiResponse.success("분반이 삭제되었습니다", null));
    }

    @PostMapping("/seminars/{roomId}/breakouts/assign")
    @Operation(summary = "참여자 배정", description = "분반에 참여자를 배정합니다")
    public ResponseEntity<ApiResponse<Void>> assignParticipants(
            @Parameter(hidden = true) @RequestHeader("X-User-Id") Long userId,
            @PathVariable Long roomId,
            @Valid @RequestBody AssignParticipantsRequest request) {
        breakoutService.assignParticipants(userId, roomId, request);
        return ResponseEntity.ok(ApiResponse.success("참여자가 배정되었습니다", null));
    }

    @PostMapping("/breakouts/{breakoutId}/start")
    @Operation(summary = "분반 시작", description = "분반을 시작합니다")
    public ResponseEntity<ApiResponse<BreakoutResponse>> startBreakoutRoom(
            @Parameter(hidden = true) @RequestHeader("X-User-Id") Long userId,
            @PathVariable Long breakoutId) {
        BreakoutResponse response = breakoutService.startBreakoutRoom(userId, breakoutId);
        return ResponseEntity.ok(ApiResponse.success("분반이 시작되었습니다", response));
    }

    @PostMapping("/breakouts/{breakoutId}/close")
    @Operation(summary = "분반 종료", description = "분반을 종료합니다")
    public ResponseEntity<ApiResponse<BreakoutResponse>> closeBreakoutRoom(
            @Parameter(hidden = true) @RequestHeader("X-User-Id") Long userId,
            @PathVariable Long breakoutId) {
        BreakoutResponse response = breakoutService.closeBreakoutRoom(userId, breakoutId);
        return ResponseEntity.ok(ApiResponse.success("분반이 종료되었습니다", response));
    }

    @GetMapping("/breakouts/{breakoutId}/status")
    @Operation(summary = "분반 상태 조회", description = "분반의 현재 상태를 조회합니다")
    public ResponseEntity<ApiResponse<BreakoutStatusResponse>> getBreakoutStatus(
            @PathVariable Long breakoutId) {
        BreakoutStatusResponse response = breakoutService.getBreakoutStatus(breakoutId);
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @PostMapping("/breakouts/{breakoutId}/join")
    @Operation(summary = "교수자 분반 입장", description = "교수자가 분반에 입장합니다")
    public ResponseEntity<ApiResponse<Void>> professorJoinRoom(
            @Parameter(hidden = true) @RequestHeader("X-User-Id") Long userId,
            @PathVariable Long breakoutId) {
        breakoutService.professorJoinRoom(userId, breakoutId);
        return ResponseEntity.ok(ApiResponse.success("분반에 입장했습니다", null));
    }

    @PostMapping("/seminars/{roomId}/breakouts/broadcast")
    @Operation(summary = "전체 공지", description = "모든 분반에 메시지를 전송합니다")
    public ResponseEntity<ApiResponse<Void>> broadcastToAllRooms(
            @Parameter(hidden = true) @RequestHeader("X-User-Id") Long userId,
            @PathVariable Long roomId,
            @Valid @RequestBody BroadcastMessageRequest request) {
        breakoutService.broadcastToAllRooms(roomId, request);
        return ResponseEntity.ok(ApiResponse.success("메시지가 전송되었습니다", null));
    }
}
