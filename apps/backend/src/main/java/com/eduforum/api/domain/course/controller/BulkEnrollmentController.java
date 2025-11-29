package com.eduforum.api.domain.course.controller;

import com.eduforum.api.common.dto.ApiResponse;
import com.eduforum.api.domain.course.dto.BulkEnrollmentRequest;
import com.eduforum.api.domain.course.dto.BulkEnrollmentResult;
import com.eduforum.api.domain.course.dto.EnrollmentPreview;
import com.eduforum.api.domain.course.service.BulkEnrollmentService;
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

/**
 * Controller for bulk enrollment operations
 */
@Slf4j
@RestController
@RequestMapping("/v1/courses/{courseId}/enrollments")
@RequiredArgsConstructor
@Tag(name = "Bulk Enrollment", description = "일괄 등록 API")
public class BulkEnrollmentController {

    private final BulkEnrollmentService bulkEnrollmentService;

    @Operation(summary = "일괄 등록 미리보기",
               description = "CSV 파일의 일괄 등록을 미리보기합니다 (교수 전용)")
    @ApiResponses({
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "미리보기 성공"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "잘못된 요청"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "401", description = "인증 실패"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "403", description = "권한 없음")
    })
    @PostMapping("/bulk/preview")
    @SecurityRequirement(name = "bearer-jwt")
    @PreAuthorize("hasRole('PROFESSOR')")
    public ResponseEntity<ApiResponse<EnrollmentPreview>> previewBulkEnrollment(
        @PathVariable Long courseId,
        @Valid @RequestBody BulkEnrollmentRequest request
    ) {
        EnrollmentPreview response = bulkEnrollmentService.previewBulkEnrollment(courseId, request);
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @Operation(summary = "일괄 사용자 생성 및 등록",
               description = "CSV 파일을 통해 사용자를 생성하고 코스에 등록합니다 (교수 전용)")
    @ApiResponses({
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "등록 완료"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "잘못된 요청"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "401", description = "인증 실패"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "403", description = "권한 없음")
    })
    @PostMapping("/bulk")
    @SecurityRequirement(name = "bearer-jwt")
    @PreAuthorize("hasRole('PROFESSOR')")
    public ResponseEntity<ApiResponse<BulkEnrollmentResult>> bulkEnroll(
        @PathVariable Long courseId,
        @Valid @RequestBody BulkEnrollmentRequest request
    ) {
        BulkEnrollmentResult response = bulkEnrollmentService.bulkEnroll(courseId, request);
        return ResponseEntity.ok(ApiResponse.success("일괄 등록이 완료되었습니다", response));
    }
}
