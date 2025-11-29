package com.eduforum.api.domain.course.controller;

import com.eduforum.api.common.dto.ApiResponse;
import com.eduforum.api.domain.course.dto.SessionCreateRequest;
import com.eduforum.api.domain.course.dto.SessionResponse;
import com.eduforum.api.domain.course.dto.SessionUpdateRequest;
import com.eduforum.api.domain.course.service.ICalService;
import com.eduforum.api.domain.course.service.SessionService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/v1")
@RequiredArgsConstructor
@Tag(name = "Session", description = "세션 관리 API")
public class SessionController {

    private final SessionService sessionService;
    private final ICalService iCalService;

    @Operation(summary = "세션 생성", description = "코스에 새로운 세션을 생성합니다 (교수 전용)")
    @ApiResponses({
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "생성 성공"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "잘못된 요청"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "401", description = "인증 실패"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "403", description = "권한 없음")
    })
    @PostMapping("/courses/{courseId}/sessions")
    @SecurityRequirement(name = "bearer-jwt")
    @PreAuthorize("hasRole('PROFESSOR')")
    public ResponseEntity<ApiResponse<SessionResponse>> createSession(
        @PathVariable Long courseId,
        @Valid @RequestBody SessionCreateRequest request
    ) {
        SessionResponse response = sessionService.createSession(courseId, request);
        return ResponseEntity.ok(ApiResponse.success("세션이 생성되었습니다", response));
    }

    @Operation(summary = "세션 목록 조회", description = "코스의 세션 목록을 조회합니다")
    @ApiResponses({
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "조회 성공"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "401", description = "인증 실패"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "코스를 찾을 수 없음")
    })
    @GetMapping("/courses/{courseId}/sessions")
    @SecurityRequirement(name = "bearer-jwt")
    public ResponseEntity<ApiResponse<List<SessionResponse>>> getCourseSessions(
        @PathVariable Long courseId
    ) {
        List<SessionResponse> response = sessionService.getCourseSessions(courseId);
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @Operation(summary = "세션 상세 조회", description = "세션 상세 정보를 조회합니다")
    @ApiResponses({
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "조회 성공"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "401", description = "인증 실패"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "세션을 찾을 수 없음")
    })
    @GetMapping("/sessions/{sessionId}")
    @SecurityRequirement(name = "bearer-jwt")
    public ResponseEntity<ApiResponse<SessionResponse>> getSession(
        @PathVariable Long sessionId
    ) {
        SessionResponse response = sessionService.getSession(sessionId);
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @Operation(summary = "세션 수정", description = "세션 정보를 수정합니다 (교수 전용)")
    @ApiResponses({
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "수정 성공"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "잘못된 요청"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "401", description = "인증 실패"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "403", description = "권한 없음"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "세션을 찾을 수 없음")
    })
    @PutMapping("/sessions/{sessionId}")
    @SecurityRequirement(name = "bearer-jwt")
    @PreAuthorize("hasRole('PROFESSOR')")
    public ResponseEntity<ApiResponse<SessionResponse>> updateSession(
        @PathVariable Long sessionId,
        @Valid @RequestBody SessionUpdateRequest request
    ) {
        SessionResponse response = sessionService.updateSession(sessionId, request);
        return ResponseEntity.ok(ApiResponse.success("세션이 수정되었습니다", response));
    }

    @Operation(summary = "세션 삭제", description = "세션을 삭제합니다 (교수 전용)")
    @ApiResponses({
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "삭제 성공"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "401", description = "인증 실패"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "403", description = "권한 없음"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "세션을 찾을 수 없음")
    })
    @DeleteMapping("/sessions/{sessionId}")
    @SecurityRequirement(name = "bearer-jwt")
    @PreAuthorize("hasRole('PROFESSOR')")
    public ResponseEntity<ApiResponse<Void>> deleteSession(
        @PathVariable Long sessionId
    ) {
        sessionService.deleteSession(sessionId);
        return ResponseEntity.ok(ApiResponse.success("세션이 삭제되었습니다"));
    }

    @Operation(summary = "코스 일정 iCal 내보내기", description = "코스의 모든 세션을 iCal 형식으로 내보냅니다")
    @ApiResponses({
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "내보내기 성공"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "401", description = "인증 실패"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "코스를 찾을 수 없음")
    })
    @GetMapping("/courses/{courseId}/sessions/ical")
    @SecurityRequirement(name = "bearer-jwt")
    public ResponseEntity<String> exportCourseCalendar(
        @PathVariable Long courseId
    ) {
        String iCalContent = iCalService.generateICalForCourse(courseId);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.parseMediaType("text/calendar"));
        headers.setContentDispositionFormData("attachment", "course-" + courseId + "-calendar.ics");

        return ResponseEntity.ok()
            .headers(headers)
            .body(iCalContent);
    }

    @Operation(summary = "학생 전체 일정 iCal 내보내기",
               description = "학생이 수강 중인 모든 코스의 세션을 iCal 형식으로 내보냅니다")
    @ApiResponses({
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "내보내기 성공"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "401", description = "인증 실패")
    })
    @GetMapping("/student/calendar/ical")
    @SecurityRequirement(name = "bearer-jwt")
    @PreAuthorize("hasRole('STUDENT')")
    public ResponseEntity<String> exportStudentCalendar() {
        String iCalContent = iCalService.generateICalForStudent();

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.parseMediaType("text/calendar"));
        headers.setContentDispositionFormData("attachment", "my-courses-calendar.ics");

        return ResponseEntity.ok()
            .headers(headers)
            .body(iCalContent);
    }

    @Operation(summary = "교수 전체 일정 iCal 내보내기",
               description = "교수가 강의하는 모든 코스의 세션을 iCal 형식으로 내보냅니다")
    @ApiResponses({
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "내보내기 성공"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "401", description = "인증 실패")
    })
    @GetMapping("/professor/calendar/ical")
    @SecurityRequirement(name = "bearer-jwt")
    @PreAuthorize("hasRole('PROFESSOR')")
    public ResponseEntity<String> exportProfessorCalendar() {
        String iCalContent = iCalService.generateICalForProfessor();

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.parseMediaType("text/calendar"));
        headers.setContentDispositionFormData("attachment", "my-courses-calendar.ics");

        return ResponseEntity.ok()
            .headers(headers)
            .body(iCalContent);
    }
}
