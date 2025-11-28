package com.eduforum.api.domain.course.controller;

import com.eduforum.api.common.dto.ApiResponse;
import com.eduforum.api.domain.course.dto.*;
import com.eduforum.api.domain.course.service.AssignmentService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/v1")
@RequiredArgsConstructor
@Tag(name = "Assignment", description = "과제 관리 API")
public class AssignmentController {

    private final AssignmentService assignmentService;

    @Operation(summary = "과제 생성", description = "코스에 새로운 과제를 생성합니다 (교수 전용)")
    @ApiResponses({
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "생성 성공"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "잘못된 요청"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "401", description = "인증 실패"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "403", description = "권한 없음")
    })
    @PostMapping("/courses/{courseId}/assignments")
    @SecurityRequirement(name = "bearer-jwt")
    @PreAuthorize("hasRole('PROFESSOR')")
    public ResponseEntity<ApiResponse<AssignmentResponse>> createAssignment(
        @PathVariable Long courseId,
        @Valid @RequestBody AssignmentCreateRequest request
    ) {
        AssignmentResponse response = assignmentService.createAssignment(courseId, request);
        return ResponseEntity.ok(ApiResponse.success("과제가 생성되었습니다", response));
    }

    @Operation(summary = "과제 목록 조회", description = "코스의 과제 목록을 조회합니다")
    @ApiResponses({
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "조회 성공"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "401", description = "인증 실패"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "코스를 찾을 수 없음")
    })
    @GetMapping("/courses/{courseId}/assignments")
    @SecurityRequirement(name = "bearer-jwt")
    public ResponseEntity<ApiResponse<List<AssignmentResponse>>> getCourseAssignments(
        @PathVariable Long courseId
    ) {
        List<AssignmentResponse> response = assignmentService.getCourseAssignments(courseId);
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @Operation(summary = "과제 상세 조회", description = "과제 상세 정보를 조회합니다")
    @ApiResponses({
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "조회 성공"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "401", description = "인증 실패"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "과제를 찾을 수 없음")
    })
    @GetMapping("/assignments/{assignmentId}")
    @SecurityRequirement(name = "bearer-jwt")
    public ResponseEntity<ApiResponse<AssignmentResponse>> getAssignment(
        @PathVariable Long assignmentId
    ) {
        AssignmentResponse response = assignmentService.getAssignment(assignmentId);
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @Operation(summary = "과제 제출", description = "과제를 제출합니다 (학생)")
    @ApiResponses({
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "제출 성공"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "잘못된 요청 또는 제출 불가"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "401", description = "인증 실패"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "과제를 찾을 수 없음")
    })
    @PostMapping("/assignments/{assignmentId}/submit")
    @SecurityRequirement(name = "bearer-jwt")
    public ResponseEntity<ApiResponse<AssignmentSubmissionResponse>> submitAssignment(
        @PathVariable Long assignmentId,
        @Valid @RequestBody AssignmentSubmissionRequest request
    ) {
        AssignmentSubmissionResponse response = assignmentService.submitAssignment(assignmentId, request);
        return ResponseEntity.ok(ApiResponse.success("과제가 제출되었습니다", response));
    }
}
