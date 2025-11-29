package com.eduforum.api.domain.active.controller;

import com.eduforum.api.common.dto.ApiResponse;
import com.eduforum.api.domain.active.dto.poll.*;
import com.eduforum.api.domain.active.service.PollService;
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
@RequestMapping("/v1/polls")
@RequiredArgsConstructor
@Tag(name = "Polls", description = "투표/설문 관리 API")
public class PollController {

    private final PollService pollService;

    @PostMapping
    @Operation(summary = "투표 생성", description = "새로운 투표를 생성합니다")
    public ResponseEntity<ApiResponse<PollResponse>> createPoll(
            @Parameter(hidden = true) @RequestHeader("X-User-Id") Long userId,
            @Valid @RequestBody PollCreateRequest request) {
        PollResponse response = pollService.createPoll(userId, request);
        return ResponseEntity.status(HttpStatus.CREATED)
            .body(ApiResponse.success("투표가 생성되었습니다", response));
    }

    @GetMapping("/{pollId}")
    @Operation(summary = "투표 조회", description = "투표 정보를 조회합니다")
    public ResponseEntity<ApiResponse<PollResponse>> getPoll(
            @PathVariable Long pollId) {
        PollResponse response = pollService.getPoll(pollId);
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @GetMapping("/course/{courseId}")
    @Operation(summary = "코스별 투표 목록", description = "코스의 모든 투표를 조회합니다")
    public ResponseEntity<ApiResponse<List<PollResponse>>> getPollsByCourse(
            @PathVariable Long courseId) {
        List<PollResponse> responses = pollService.getPollsByCourse(courseId);
        return ResponseEntity.ok(ApiResponse.success(responses));
    }

    @PutMapping("/{pollId}")
    @Operation(summary = "투표 수정", description = "투표를 수정합니다")
    public ResponseEntity<ApiResponse<PollResponse>> updatePoll(
            @Parameter(hidden = true) @RequestHeader("X-User-Id") Long userId,
            @PathVariable Long pollId,
            @Valid @RequestBody PollCreateRequest request) {
        PollResponse response = pollService.updatePoll(userId, pollId, request);
        return ResponseEntity.ok(ApiResponse.success("투표가 수정되었습니다", response));
    }

    @DeleteMapping("/{pollId}")
    @Operation(summary = "투표 삭제", description = "투표를 삭제합니다")
    public ResponseEntity<ApiResponse<Void>> deletePoll(
            @Parameter(hidden = true) @RequestHeader("X-User-Id") Long userId,
            @PathVariable Long pollId) {
        pollService.deletePoll(userId, pollId);
        return ResponseEntity.ok(ApiResponse.success("투표가 삭제되었습니다", null));
    }

    @PostMapping("/{pollId}/start")
    @Operation(summary = "투표 시작", description = "투표를 활성화합니다")
    public ResponseEntity<ApiResponse<PollResponse>> activatePoll(
            @Parameter(hidden = true) @RequestHeader("X-User-Id") Long userId,
            @PathVariable Long pollId) {
        PollResponse response = pollService.activatePoll(userId, pollId);
        return ResponseEntity.ok(ApiResponse.success("투표가 시작되었습니다", response));
    }

    @PostMapping("/{pollId}/stop")
    @Operation(summary = "투표 종료", description = "투표를 종료합니다")
    public ResponseEntity<ApiResponse<PollResponse>> closePoll(
            @Parameter(hidden = true) @RequestHeader("X-User-Id") Long userId,
            @PathVariable Long pollId) {
        PollResponse response = pollService.closePoll(userId, pollId);
        return ResponseEntity.ok(ApiResponse.success("투표가 종료되었습니다", response));
    }

    @PostMapping("/{pollId}/responses")
    @Operation(summary = "투표 응답 제출", description = "투표에 응답합니다")
    public ResponseEntity<ApiResponse<Void>> submitResponse(
            @Parameter(hidden = true) @RequestHeader("X-User-Id") Long userId,
            @PathVariable Long pollId,
            @Valid @RequestBody PollSubmitRequest request) {
        pollService.submitResponse(userId, pollId, request);
        return ResponseEntity.ok(ApiResponse.success("응답이 제출되었습니다", null));
    }

    @GetMapping("/{pollId}/results")
    @Operation(summary = "투표 결과 조회", description = "투표 결과를 조회합니다")
    public ResponseEntity<ApiResponse<PollResultsResponse>> getResults(
            @PathVariable Long pollId) {
        PollResultsResponse response = pollService.getResults(pollId);
        return ResponseEntity.ok(ApiResponse.success(response));
    }
}
