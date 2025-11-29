package com.eduforum.api.domain.course.controller;

import com.eduforum.api.common.dto.ApiResponse;
import com.eduforum.api.domain.course.dto.AssignTARequest;
import com.eduforum.api.domain.course.dto.CourseTAResponse;
import com.eduforum.api.domain.course.dto.TAPermissions;
import com.eduforum.api.domain.course.service.CourseTAService;
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

/**
 * Controller for course TA management
 */
@Slf4j
@RestController
@RequestMapping("/v1/courses/{courseId}/tas")
@RequiredArgsConstructor
@Tag(name = "Course TA", description = "코스 TA 관리 API")
public class CourseTAController {

    private final CourseTAService courseTAService;

    @Operation(summary = "TA 배정", description = "코스에 TA를 배정합니다 (교수 전용)")
    @ApiResponses({
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "배정 성공"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "잘못된 요청"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "401", description = "인증 실패"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "403", description = "권한 없음"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "코스 또는 사용자를 찾을 수 없음")
    })
    @PostMapping
    @SecurityRequirement(name = "bearer-jwt")
    @PreAuthorize("hasRole('PROFESSOR')")
    public ResponseEntity<ApiResponse<CourseTAResponse>> assignTA(
        @PathVariable Long courseId,
        @Valid @RequestBody AssignTARequest request
    ) {
        CourseTAResponse response = courseTAService.assignTA(courseId, request);
        return ResponseEntity.ok(ApiResponse.success("TA가 배정되었습니다", response));
    }

    @Operation(summary = "TA 목록 조회", description = "코스의 TA 목록을 조회합니다")
    @ApiResponses({
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "조회 성공"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "401", description = "인증 실패"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "코스를 찾을 수 없음")
    })
    @GetMapping
    @SecurityRequirement(name = "bearer-jwt")
    public ResponseEntity<ApiResponse<List<CourseTAResponse>>> getTAsByCourse(
        @PathVariable Long courseId
    ) {
        List<CourseTAResponse> response = courseTAService.getTAsByCourse(courseId);
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @Operation(summary = "TA 배정 해제", description = "코스에서 TA 배정을 해제합니다 (교수 전용)")
    @ApiResponses({
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "해제 성공"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "401", description = "인증 실패"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "403", description = "권한 없음"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "TA 배정을 찾을 수 없음")
    })
    @DeleteMapping("/{taId}")
    @SecurityRequirement(name = "bearer-jwt")
    @PreAuthorize("hasRole('PROFESSOR')")
    public ResponseEntity<ApiResponse<Void>> removeTA(
        @PathVariable Long courseId,
        @PathVariable Long taId
    ) {
        courseTAService.removeTA(courseId, taId);
        return ResponseEntity.ok(ApiResponse.success("TA 배정이 해제되었습니다"));
    }

    @Operation(summary = "TA 권한 수정", description = "TA의 권한을 수정합니다 (교수 전용)")
    @ApiResponses({
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "수정 성공"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "잘못된 요청"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "401", description = "인증 실패"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "403", description = "권한 없음"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "TA 배정을 찾을 수 없음")
    })
    @PutMapping("/{taId}/permissions")
    @SecurityRequirement(name = "bearer-jwt")
    @PreAuthorize("hasRole('PROFESSOR')")
    public ResponseEntity<ApiResponse<CourseTAResponse>> updateTAPermissions(
        @PathVariable Long courseId,
        @PathVariable Long taId,
        @Valid @RequestBody TAPermissions permissions
    ) {
        CourseTAResponse response = courseTAService.updateTAPermissions(courseId, taId, permissions);
        return ResponseEntity.ok(ApiResponse.success("TA 권한이 수정되었습니다", response));
    }
}
