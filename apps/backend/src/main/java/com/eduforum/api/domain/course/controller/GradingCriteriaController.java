package com.eduforum.api.domain.course.controller;

import com.eduforum.api.common.dto.ApiResponse;
import com.eduforum.api.domain.course.dto.GradingCriteriaRequest;
import com.eduforum.api.domain.course.dto.GradingCriteriaResponse;
import com.eduforum.api.domain.course.service.GradingCriteriaService;
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
 * Controller for grading criteria management
 */
@Slf4j
@RestController
@RequestMapping("/v1")
@RequiredArgsConstructor
@Tag(name = "Grading Criteria", description = "평가 기준 관리 API")
public class GradingCriteriaController {

    private final GradingCriteriaService gradingCriteriaService;

    @Operation(summary = "평가 기준 생성", description = "코스의 평가 기준을 생성합니다 (교수 전용)")
    @ApiResponses({
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "생성 성공"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "잘못된 요청"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "401", description = "인증 실패"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "403", description = "권한 없음")
    })
    @PostMapping("/courses/{courseId}/grading-criteria")
    @SecurityRequirement(name = "bearer-jwt")
    @PreAuthorize("hasRole('PROFESSOR')")
    public ResponseEntity<ApiResponse<GradingCriteriaResponse>> createCriteria(
        @PathVariable Long courseId,
        @Valid @RequestBody GradingCriteriaRequest request
    ) {
        GradingCriteriaResponse response = gradingCriteriaService.createCriteria(courseId, request);
        return ResponseEntity.ok(ApiResponse.success("평가 기준이 생성되었습니다", response));
    }

    @Operation(summary = "평가 기준 목록 조회", description = "코스의 평가 기준 목록을 조회합니다")
    @ApiResponses({
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "조회 성공"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "401", description = "인증 실패"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "코스를 찾을 수 없음")
    })
    @GetMapping("/courses/{courseId}/grading-criteria")
    @SecurityRequirement(name = "bearer-jwt")
    public ResponseEntity<ApiResponse<List<GradingCriteriaResponse>>> getCriteriaByCourse(
        @PathVariable Long courseId
    ) {
        List<GradingCriteriaResponse> response = gradingCriteriaService.getCriteriaByCourse(courseId);
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @Operation(summary = "평가 기준 상세 조회", description = "평가 기준 상세 정보를 조회합니다")
    @ApiResponses({
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "조회 성공"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "401", description = "인증 실패"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "평가 기준을 찾을 수 없음")
    })
    @GetMapping("/grading-criteria/{criteriaId}")
    @SecurityRequirement(name = "bearer-jwt")
    public ResponseEntity<ApiResponse<GradingCriteriaResponse>> getCriteria(
        @PathVariable Long criteriaId
    ) {
        GradingCriteriaResponse response = gradingCriteriaService.getCriteria(criteriaId);
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @Operation(summary = "평가 기준 수정", description = "평가 기준을 수정합니다 (교수 전용)")
    @ApiResponses({
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "수정 성공"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "잘못된 요청"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "401", description = "인증 실패"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "403", description = "권한 없음"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "평가 기준을 찾을 수 없음")
    })
    @PutMapping("/grading-criteria/{criteriaId}")
    @SecurityRequirement(name = "bearer-jwt")
    @PreAuthorize("hasRole('PROFESSOR')")
    public ResponseEntity<ApiResponse<GradingCriteriaResponse>> updateCriteria(
        @PathVariable Long criteriaId,
        @Valid @RequestBody GradingCriteriaRequest request
    ) {
        GradingCriteriaResponse response = gradingCriteriaService.updateCriteria(criteriaId, request);
        return ResponseEntity.ok(ApiResponse.success("평가 기준이 수정되었습니다", response));
    }

    @Operation(summary = "평가 기준 삭제", description = "평가 기준을 삭제합니다 (교수 전용)")
    @ApiResponses({
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "삭제 성공"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "401", description = "인증 실패"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "403", description = "권한 없음"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "평가 기준을 찾을 수 없음")
    })
    @DeleteMapping("/grading-criteria/{criteriaId}")
    @SecurityRequirement(name = "bearer-jwt")
    @PreAuthorize("hasRole('PROFESSOR')")
    public ResponseEntity<ApiResponse<Void>> deleteCriteria(
        @PathVariable Long criteriaId
    ) {
        gradingCriteriaService.deleteCriteria(criteriaId);
        return ResponseEntity.ok(ApiResponse.success("평가 기준이 삭제되었습니다"));
    }
}
