package com.eduforum.api.domain.seminar.controller;

import com.eduforum.api.common.dto.ApiResponse;
import com.eduforum.api.domain.seminar.dto.ReactionRequest;
import com.eduforum.api.domain.seminar.dto.ReactionResponse;
import com.eduforum.api.domain.seminar.service.ReactionService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * REST API controller for reactions
 */
@RestController
@RequestMapping("/v1/rooms/{roomId}/reactions")
@RequiredArgsConstructor
@Tag(name = "Reactions", description = "반응 API")
public class ReactionController {

    private final ReactionService reactionService;

    @PostMapping
    @Operation(summary = "반응 보내기", description = "실시간 반응을 보냅니다 (👍, 👏, ❤️, 😂, 😮)")
    public ResponseEntity<ApiResponse<ReactionResponse>> sendReaction(
            @RequestHeader("X-User-Id") Long userId,
            @Parameter(description = "룸 ID") @PathVariable Long roomId,
            @Valid @RequestBody ReactionRequest request) {
        request.setRoomId(roomId); // Ensure roomId from path is used
        ReactionResponse response = reactionService.sendReaction(userId, request);
        return ResponseEntity.ok(ApiResponse.success("반응이 전송되었습니다", response));
    }

    @GetMapping("/recent")
    @Operation(summary = "최근 반응 조회", description = "최근 N분간의 반응을 조회합니다")
    public ResponseEntity<ApiResponse<List<ReactionResponse>>> getRecentReactions(
            @Parameter(description = "룸 ID") @PathVariable Long roomId,
            @Parameter(description = "조회할 시간 (분)") @RequestParam(defaultValue = "5") int minutes) {
        List<ReactionResponse> reactions = reactionService.getRecentReactions(roomId, minutes);
        return ResponseEntity.ok(ApiResponse.success(reactions));
    }
}
