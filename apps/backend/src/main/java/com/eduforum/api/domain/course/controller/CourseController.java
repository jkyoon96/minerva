package com.eduforum.api.domain.course.controller;

import com.eduforum.api.common.dto.ApiResponse;
import com.eduforum.api.domain.course.dto.*;
import com.eduforum.api.domain.course.service.CourseService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
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
@RequestMapping("/v1/courses")
@RequiredArgsConstructor
@Tag(name = "Course", description = "코스 관리 API")
public class CourseController {

    private final CourseService courseService;

    @Operation(summary = "코스 생성", description = "새로운 코스를 생성합니다 (교수 전용)")
    @ApiResponses({
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "생성 성공"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "잘못된 요청"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "401", description = "인증 실패"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "403", description = "권한 없음")
    })
    @PostMapping
    @SecurityRequirement(name = "bearer-jwt")
    @PreAuthorize("hasRole('PROFESSOR')")
    public ResponseEntity<ApiResponse<CourseResponse>> createCourse(
        @Valid @RequestBody CourseCreateRequest request
    ) {
        CourseResponse response = courseService.createCourse(request);
        return ResponseEntity.ok(ApiResponse.success("코스가 생성되었습니다", response));
    }

    @Operation(summary = "코스 목록 조회", description = "현재 사용자의 코스 목록을 조회합니다")
    @ApiResponses({
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "조회 성공"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "401", description = "인증 실패")
    })
    @GetMapping
    @SecurityRequirement(name = "bearer-jwt")
    public ResponseEntity<ApiResponse<List<CourseResponse>>> getMyCourses() {
        List<CourseResponse> response = courseService.getMyCourses();
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @Operation(summary = "코스 상세 조회", description = "코스 상세 정보를 조회합니다")
    @ApiResponses({
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "조회 성공"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "401", description = "인증 실패"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "403", description = "권한 없음"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "코스를 찾을 수 없음")
    })
    @GetMapping("/{courseId}")
    @SecurityRequirement(name = "bearer-jwt")
    public ResponseEntity<ApiResponse<CourseResponse>> getCourse(
        @PathVariable Long courseId
    ) {
        CourseResponse response = courseService.getCourse(courseId);
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @Operation(summary = "코스 수정", description = "코스 정보를 수정합니다 (교수 전용)")
    @ApiResponses({
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "수정 성공"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "잘못된 요청"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "401", description = "인증 실패"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "403", description = "권한 없음"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "코스를 찾을 수 없음")
    })
    @PutMapping("/{courseId}")
    @SecurityRequirement(name = "bearer-jwt")
    @PreAuthorize("hasRole('PROFESSOR')")
    public ResponseEntity<ApiResponse<CourseResponse>> updateCourse(
        @PathVariable Long courseId,
        @Valid @RequestBody CourseUpdateRequest request
    ) {
        CourseResponse response = courseService.updateCourse(courseId, request);
        return ResponseEntity.ok(ApiResponse.success("코스가 수정되었습니다", response));
    }

    @Operation(summary = "코스 삭제", description = "코스를 삭제합니다 (교수 전용)")
    @ApiResponses({
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "삭제 성공"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "401", description = "인증 실패"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "403", description = "권한 없음"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "코스를 찾을 수 없음")
    })
    @DeleteMapping("/{courseId}")
    @SecurityRequirement(name = "bearer-jwt")
    @PreAuthorize("hasRole('PROFESSOR')")
    public ResponseEntity<ApiResponse<Void>> deleteCourse(
        @PathVariable Long courseId
    ) {
        courseService.deleteCourse(courseId);
        return ResponseEntity.ok(ApiResponse.success("코스가 삭제되었습니다"));
    }

    @Operation(summary = "코스 보관", description = "코스를 보관합니다 (교수 전용)")
    @ApiResponses({
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "보관 성공"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "401", description = "인증 실패"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "403", description = "권한 없음"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "코스를 찾을 수 없음")
    })
    @PostMapping("/{courseId}/archive")
    @SecurityRequirement(name = "bearer-jwt")
    @PreAuthorize("hasRole('PROFESSOR')")
    public ResponseEntity<ApiResponse<CourseResponse>> archiveCourse(
        @PathVariable Long courseId
    ) {
        CourseResponse response = courseService.archiveCourse(courseId);
        return ResponseEntity.ok(ApiResponse.success("코스가 보관되었습니다", response));
    }

    @Operation(summary = "초대 링크 생성", description = "코스 초대 링크를 생성합니다 (교수 전용)")
    @ApiResponses({
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "생성 성공"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "잘못된 요청"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "401", description = "인증 실패"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "403", description = "권한 없음")
    })
    @PostMapping("/{courseId}/invite-links")
    @SecurityRequirement(name = "bearer-jwt")
    @PreAuthorize("hasRole('PROFESSOR')")
    public ResponseEntity<ApiResponse<InviteLinkResponse>> createInviteLink(
        @PathVariable Long courseId,
        @Valid @RequestBody InviteLinkCreateRequest request
    ) {
        InviteLinkResponse response = courseService.createInviteLink(courseId, request);
        return ResponseEntity.ok(ApiResponse.success("초대 링크가 생성되었습니다", response));
    }

    @Operation(summary = "초대 링크 목록", description = "코스의 초대 링크 목록을 조회합니다 (교수 전용)")
    @ApiResponses({
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "조회 성공"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "401", description = "인증 실패"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "403", description = "권한 없음")
    })
    @GetMapping("/{courseId}/invite-links")
    @SecurityRequirement(name = "bearer-jwt")
    @PreAuthorize("hasRole('PROFESSOR')")
    public ResponseEntity<ApiResponse<List<InviteLinkResponse>>> getInviteLinks(
        @PathVariable Long courseId
    ) {
        List<InviteLinkResponse> response = courseService.getInviteLinks(courseId);
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @Operation(summary = "초대 링크 삭제", description = "초대 링크를 삭제합니다 (교수 전용)")
    @ApiResponses({
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "삭제 성공"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "401", description = "인증 실패"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "403", description = "권한 없음"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "초대 링크를 찾을 수 없음")
    })
    @DeleteMapping("/{courseId}/invite-links/{linkId}")
    @SecurityRequirement(name = "bearer-jwt")
    @PreAuthorize("hasRole('PROFESSOR')")
    public ResponseEntity<ApiResponse<Void>> deleteInviteLink(
        @PathVariable Long courseId,
        @PathVariable Long linkId
    ) {
        courseService.deleteInviteLink(courseId, linkId);
        return ResponseEntity.ok(ApiResponse.success("초대 링크가 삭제되었습니다"));
    }

    @Operation(summary = "초대 코드 검증", description = "초대 코드를 검증하고 코스 정보를 반환합니다")
    @ApiResponses({
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "검증 성공"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "유효하지 않은 초대 코드")
    })
    @GetMapping("/invite/{code}")
    public ResponseEntity<ApiResponse<CourseResponse>> verifyInviteCode(
        @PathVariable String code
    ) {
        CourseResponse response = courseService.verifyInviteCode(code);
        return ResponseEntity.ok(ApiResponse.success(response));
    }
}
