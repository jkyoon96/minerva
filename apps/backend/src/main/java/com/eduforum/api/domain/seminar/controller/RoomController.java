package com.eduforum.api.domain.seminar.controller;

import com.eduforum.api.common.dto.ApiResponse;
import com.eduforum.api.domain.seminar.dto.*;
import com.eduforum.api.domain.seminar.entity.LayoutType;
import com.eduforum.api.domain.seminar.service.RoomService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * REST API controller for seminar room management
 */
@RestController
@RequestMapping("/v1/rooms")
@RequiredArgsConstructor
@Tag(name = "Seminar Rooms", description = "세미나 룸 관리 API")
public class RoomController {

    private final RoomService roomService;

    @PostMapping
    @Operation(summary = "세미나 룸 생성", description = "세션에 대한 새로운 세미나 룸을 생성합니다")
    @ApiResponses({
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "201", description = "룸 생성 성공"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", ref = "BadRequest"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "401", ref = "Unauthorized"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "409", description = "룸이 이미 존재함")
    })
    public ResponseEntity<ApiResponse<RoomResponse>> createRoom(
            @Parameter(description = "사용자 ID (인증에서 추출)", hidden = true)
            @RequestHeader("X-User-Id") Long userId,
            @Valid @RequestBody RoomCreateRequest request) {
        RoomResponse response = roomService.createRoom(userId, request);
        return ResponseEntity.status(HttpStatus.CREATED)
            .body(ApiResponse.success("세미나 룸이 생성되었습니다", response));
    }

    @GetMapping("/{roomId}")
    @Operation(summary = "룸 정보 조회", description = "룸 ID로 세미나 룸 정보를 조회합니다")
    @ApiResponses({
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "조회 성공"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", ref = "NotFound")
    })
    public ResponseEntity<ApiResponse<RoomResponse>> getRoom(
            @Parameter(description = "룸 ID") @PathVariable Long roomId) {
        RoomResponse response = roomService.getRoom(roomId);
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @GetMapping("/session/{sessionId}")
    @Operation(summary = "세션의 룸 조회", description = "세션 ID로 세미나 룸을 조회합니다")
    @ApiResponses({
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "조회 성공"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", ref = "NotFound")
    })
    public ResponseEntity<ApiResponse<RoomResponse>> getRoomBySessionId(
            @Parameter(description = "세션 ID") @PathVariable Long sessionId) {
        RoomResponse response = roomService.getRoomBySessionId(sessionId);
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @PostMapping("/{roomId}/start")
    @Operation(summary = "룸 시작", description = "대기 중인 룸을 활성화합니다 (호스트만 가능)")
    @ApiResponses({
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "룸 시작 성공"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "이미 시작된 룸"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "403", description = "호스트 권한 필요")
    })
    public ResponseEntity<ApiResponse<RoomResponse>> startRoom(
            @RequestHeader("X-User-Id") Long userId,
            @Parameter(description = "룸 ID") @PathVariable Long roomId) {
        RoomResponse response = roomService.startRoom(userId, roomId);
        return ResponseEntity.ok(ApiResponse.success("세미나가 시작되었습니다", response));
    }

    @PostMapping("/{roomId}/end")
    @Operation(summary = "룸 종료", description = "활성 룸을 종료합니다 (호스트만 가능)")
    @ApiResponses({
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "룸 종료 성공"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "이미 종료된 룸"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "403", description = "호스트 권한 필요")
    })
    public ResponseEntity<ApiResponse<RoomResponse>> endRoom(
            @RequestHeader("X-User-Id") Long userId,
            @Parameter(description = "룸 ID") @PathVariable Long roomId) {
        RoomResponse response = roomService.endRoom(userId, roomId);
        return ResponseEntity.ok(ApiResponse.success("세미나가 종료되었습니다", response));
    }

    @PutMapping("/{roomId}/layout")
    @Operation(summary = "레이아웃 변경", description = "룸의 레이아웃을 변경합니다 (호스트만 가능)")
    @ApiResponses({
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "레이아웃 변경 성공"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "403", description = "호스트 권한 필요")
    })
    public ResponseEntity<ApiResponse<RoomResponse>> updateLayout(
            @RequestHeader("X-User-Id") Long userId,
            @Parameter(description = "룸 ID") @PathVariable Long roomId,
            @Valid @RequestBody LayoutUpdateRequest request) {
        RoomResponse response = roomService.updateLayout(userId, roomId, request);
        return ResponseEntity.ok(ApiResponse.success("레이아웃이 변경되었습니다", response));
    }

    @GetMapping("/{roomId}/layout")
    @Operation(summary = "현재 레이아웃 조회", description = "룸의 현재 레이아웃을 조회합니다")
    public ResponseEntity<ApiResponse<LayoutType>> getLayout(
            @Parameter(description = "룸 ID") @PathVariable Long roomId) {
        LayoutType layout = roomService.getLayout(roomId);
        return ResponseEntity.ok(ApiResponse.success(layout));
    }

    @GetMapping("/active")
    @Operation(summary = "활성 룸 목록", description = "현재 활성화된 모든 룸을 조회합니다")
    public ResponseEntity<ApiResponse<List<RoomResponse>>> getActiveRooms() {
        List<RoomResponse> rooms = roomService.getActiveRooms();
        return ResponseEntity.ok(ApiResponse.success(rooms));
    }

    @GetMapping("/host/{hostId}")
    @Operation(summary = "호스트의 룸 목록", description = "특정 호스트가 생성한 룸 목록을 조회합니다")
    public ResponseEntity<ApiResponse<List<RoomResponse>>> getRoomsByHost(
            @Parameter(description = "호스트 사용자 ID") @PathVariable Long hostId) {
        List<RoomResponse> rooms = roomService.getRoomsByHost(hostId);
        return ResponseEntity.ok(ApiResponse.success(rooms));
    }
}
