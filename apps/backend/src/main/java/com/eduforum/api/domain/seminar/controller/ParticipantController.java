package com.eduforum.api.domain.seminar.controller;

import com.eduforum.api.common.dto.ApiResponse;
import com.eduforum.api.domain.seminar.dto.ParticipantResponse;
import com.eduforum.api.domain.seminar.dto.RoomJoinRequest;
import com.eduforum.api.domain.seminar.service.ParticipantService;
import com.eduforum.api.domain.seminar.service.ScreenShareService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * REST API controller for room participant management
 */
@RestController
@RequestMapping("/v1/rooms/{roomId}/participants")
@RequiredArgsConstructor
@Tag(name = "Participants", description = "룸 참가자 관리 API")
public class ParticipantController {

    private final ParticipantService participantService;
    private final ScreenShareService screenShareService;

    @PostMapping("/join")
    @Operation(summary = "룸 참가", description = "세미나 룸에 참가합니다")
    @ApiResponses({
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "참가 성공"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "룸이 가득 참"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "409", description = "이미 참가 중")
    })
    public ResponseEntity<ApiResponse<ParticipantResponse>> joinRoom(
            @RequestHeader("X-User-Id") Long userId,
            @Parameter(description = "룸 ID") @PathVariable Long roomId,
            @Valid @RequestBody RoomJoinRequest request) {
        request.setRoomId(roomId); // Ensure roomId from path is used
        ParticipantResponse response = participantService.joinRoom(userId, request);
        return ResponseEntity.ok(ApiResponse.success("룸에 참가했습니다", response));
    }

    @PostMapping("/leave")
    @Operation(summary = "룸 퇴장", description = "세미나 룸에서 퇴장합니다")
    public ResponseEntity<ApiResponse<Void>> leaveRoom(
            @RequestHeader("X-User-Id") Long userId,
            @Parameter(description = "룸 ID") @PathVariable Long roomId) {
        participantService.leaveRoom(userId, roomId);
        return ResponseEntity.ok(ApiResponse.success("룸에서 퇴장했습니다"));
    }

    @GetMapping
    @Operation(summary = "참가자 목록", description = "룸의 모든 참가자 목록을 조회합니다")
    public ResponseEntity<ApiResponse<List<ParticipantResponse>>> getParticipants(
            @Parameter(description = "룸 ID") @PathVariable Long roomId) {
        List<ParticipantResponse> participants = participantService.getParticipants(roomId);
        return ResponseEntity.ok(ApiResponse.success(participants));
    }

    @GetMapping("/active")
    @Operation(summary = "활성 참가자 목록", description = "현재 활성화된 참가자만 조회합니다")
    public ResponseEntity<ApiResponse<List<ParticipantResponse>>> getActiveParticipants(
            @Parameter(description = "룸 ID") @PathVariable Long roomId) {
        List<ParticipantResponse> participants = participantService.getActiveParticipants(roomId);
        return ResponseEntity.ok(ApiResponse.success(participants));
    }

    @PostMapping("/hand-raise")
    @Operation(summary = "손들기", description = "손을 들어 발언 의사를 표시합니다")
    public ResponseEntity<ApiResponse<ParticipantResponse>> raiseHand(
            @RequestHeader("X-User-Id") Long userId,
            @Parameter(description = "룸 ID") @PathVariable Long roomId) {
        ParticipantResponse response = participantService.raiseHand(userId, roomId);
        return ResponseEntity.ok(ApiResponse.success("손을 들었습니다", response));
    }

    @DeleteMapping("/hand-raise")
    @Operation(summary = "손 내리기", description = "손을 내립니다")
    public ResponseEntity<ApiResponse<ParticipantResponse>> lowerHand(
            @RequestHeader("X-User-Id") Long userId,
            @Parameter(description = "룸 ID") @PathVariable Long roomId) {
        ParticipantResponse response = participantService.lowerHand(userId, roomId);
        return ResponseEntity.ok(ApiResponse.success("손을 내렸습니다", response));
    }

    @GetMapping("/raised-hands")
    @Operation(summary = "손든 참가자 목록", description = "손을 든 참가자 목록을 조회합니다")
    public ResponseEntity<ApiResponse<List<ParticipantResponse>>> getRaisedHands(
            @Parameter(description = "룸 ID") @PathVariable Long roomId) {
        List<ParticipantResponse> participants = participantService.getRaisedHands(roomId);
        return ResponseEntity.ok(ApiResponse.success(participants));
    }

    @PostMapping("/toggle-mute")
    @Operation(summary = "음소거 토글", description = "오디오 음소거를 켜거나 끕니다")
    public ResponseEntity<ApiResponse<ParticipantResponse>> toggleMute(
            @RequestHeader("X-User-Id") Long userId,
            @Parameter(description = "룸 ID") @PathVariable Long roomId) {
        ParticipantResponse response = participantService.toggleMute(userId, roomId);
        return ResponseEntity.ok(ApiResponse.success("음소거 상태가 변경되었습니다", response));
    }

    @PostMapping("/toggle-video")
    @Operation(summary = "비디오 토글", description = "비디오를 켜거나 끕니다")
    public ResponseEntity<ApiResponse<ParticipantResponse>> toggleVideo(
            @RequestHeader("X-User-Id") Long userId,
            @Parameter(description = "룸 ID") @PathVariable Long roomId) {
        ParticipantResponse response = participantService.toggleVideo(userId, roomId);
        return ResponseEntity.ok(ApiResponse.success("비디오 상태가 변경되었습니다", response));
    }

    @PostMapping("/screen-share/start")
    @Operation(summary = "화면 공유 시작", description = "화면 공유를 시작합니다")
    @ApiResponses({
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "화면 공유 시작 성공"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "403", description = "화면 공유 권한 없음"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "409", description = "이미 다른 사용자가 공유 중")
    })
    public ResponseEntity<ApiResponse<ParticipantResponse>> startScreenShare(
            @RequestHeader("X-User-Id") Long userId,
            @Parameter(description = "룸 ID") @PathVariable Long roomId) {
        ParticipantResponse response = screenShareService.startScreenShare(userId, roomId);
        return ResponseEntity.ok(ApiResponse.success("화면 공유를 시작했습니다", response));
    }

    @PostMapping("/screen-share/stop")
    @Operation(summary = "화면 공유 중지", description = "화면 공유를 중지합니다")
    public ResponseEntity<ApiResponse<ParticipantResponse>> stopScreenShare(
            @RequestHeader("X-User-Id") Long userId,
            @Parameter(description = "룸 ID") @PathVariable Long roomId) {
        ParticipantResponse response = screenShareService.stopScreenShare(userId, roomId);
        return ResponseEntity.ok(ApiResponse.success("화면 공유를 중지했습니다", response));
    }
}
