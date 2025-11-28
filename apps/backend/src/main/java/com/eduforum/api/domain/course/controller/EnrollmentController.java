package com.eduforum.api.domain.course.controller;

import com.eduforum.api.common.dto.ApiResponse;
import com.eduforum.api.domain.course.dto.CourseJoinRequest;
import com.eduforum.api.domain.course.dto.EnrollmentResponse;
import com.eduforum.api.domain.course.service.EnrollmentService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/v1")
@RequiredArgsConstructor
@Tag(name = "Enrollment", description = "수강 등록 API")
public class EnrollmentController {

    private final EnrollmentService enrollmentService;

    @Operation(summary = "코스 가입", description = "초대 코드로 코스에 가입합니다")
    @ApiResponses({
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "가입 성공"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "잘못된 요청"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "401", description = "인증 실패"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "유효하지 않은 초대 코드"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "409", description = "이미 등록된 코스")
    })
    @PostMapping("/courses/join")
    @SecurityRequirement(name = "bearer-jwt")
    public ResponseEntity<ApiResponse<EnrollmentResponse>> joinCourse(
        @Valid @RequestBody CourseJoinRequest request
    ) {
        EnrollmentResponse response = enrollmentService.joinCourse(request);
        return ResponseEntity.ok(ApiResponse.success("코스에 가입되었습니다", response));
    }

    @Operation(summary = "내 수강 코스 목록", description = "현재 사용자가 수강 중인 코스 목록을 조회합니다")
    @ApiResponses({
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "조회 성공"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "401", description = "인증 실패")
    })
    @GetMapping("/student/courses")
    @SecurityRequirement(name = "bearer-jwt")
    public ResponseEntity<ApiResponse<List<EnrollmentResponse>>> getMyCourses() {
        List<EnrollmentResponse> response = enrollmentService.getMyCourses();
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @Operation(summary = "CSV 일괄 등록", description = "CSV 파일로 학생들을 일괄 등록합니다 (교수 전용)")
    @ApiResponses({
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "등록 성공"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "잘못된 파일 형식"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "401", description = "인증 실패"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "403", description = "권한 없음")
    })
    @PostMapping(value = "/courses/{courseId}/enrollments/csv", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @SecurityRequirement(name = "bearer-jwt")
    @PreAuthorize("hasRole('PROFESSOR')")
    public ResponseEntity<ApiResponse<List<EnrollmentResponse>>> bulkEnrollFromCsv(
        @PathVariable Long courseId,
        @RequestParam("file") MultipartFile file
    ) {
        List<EnrollmentResponse> response = enrollmentService.bulkEnrollFromCsv(courseId, file);
        return ResponseEntity.ok(ApiResponse.success(
            response.size() + "명의 학생이 등록되었습니다", response));
    }
}
