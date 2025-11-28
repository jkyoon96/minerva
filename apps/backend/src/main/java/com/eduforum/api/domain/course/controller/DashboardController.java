package com.eduforum.api.domain.course.controller;

import com.eduforum.api.common.dto.ApiResponse;
import com.eduforum.api.domain.course.dto.DashboardResponse;
import com.eduforum.api.domain.course.dto.StudentDashboardResponse;
import com.eduforum.api.domain.course.service.DashboardService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("/v1")
@RequiredArgsConstructor
@Tag(name = "Dashboard", description = "대시보드 API")
public class DashboardController {

    private final DashboardService dashboardService;

    @Operation(summary = "교수 대시보드", description = "교수용 대시보드 데이터를 조회합니다")
    @ApiResponses({
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "조회 성공"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "401", description = "인증 실패"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "403", description = "권한 없음")
    })
    @GetMapping("/dashboard")
    @SecurityRequirement(name = "bearer-jwt")
    @PreAuthorize("hasRole('PROFESSOR')")
    public ResponseEntity<ApiResponse<DashboardResponse>> getProfessorDashboard() {
        DashboardResponse response = dashboardService.getProfessorDashboard();
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @Operation(summary = "학생 대시보드", description = "학생용 대시보드 데이터를 조회합니다")
    @ApiResponses({
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "조회 성공"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "401", description = "인증 실패")
    })
    @GetMapping("/student/dashboard")
    @SecurityRequirement(name = "bearer-jwt")
    public ResponseEntity<ApiResponse<StudentDashboardResponse>> getStudentDashboard() {
        StudentDashboardResponse response = dashboardService.getStudentDashboard();
        return ResponseEntity.ok(ApiResponse.success(response));
    }
}
