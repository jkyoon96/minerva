package com.eduforum.api.domain.analytics.controller;

import com.eduforum.api.common.dto.ApiResponse;
import com.eduforum.api.domain.analytics.dto.realtime.*;
import com.eduforum.api.domain.analytics.service.RealTimeAnalyticsService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.time.OffsetDateTime;

@RestController
@RequestMapping("/v1/analytics/realtime")
@RequiredArgsConstructor
@Tag(name = "Real-time Analytics", description = "Live analytics and monitoring APIs")
public class RealTimeAnalyticsController {

    private final RealTimeAnalyticsService analyticsService;

    @GetMapping("/{sessionId}")
    @Operation(summary = "Get live session stats", description = "Get real-time statistics for a session")
    public ResponseEntity<ApiResponse<LiveStatsResponse>> getLiveSessionStats(@PathVariable Long sessionId) {
        LiveStatsResponse stats = analyticsService.getLiveSessionStats(sessionId);
        return ResponseEntity.ok(ApiResponse.success(stats));
    }

    @GetMapping("/course/{courseId}")
    @Operation(summary = "Get live course stats", description = "Get real-time statistics for a course")
    public ResponseEntity<ApiResponse<LiveStatsResponse>> getLiveCourseStats(@PathVariable Long courseId) {
        LiveStatsResponse stats = analyticsService.getLiveCourseStats(courseId);
        return ResponseEntity.ok(ApiResponse.success(stats));
    }

    @PostMapping("/snapshot")
    @Operation(summary = "Save snapshot", description = "Save analytics snapshot")
    public ResponseEntity<ApiResponse<SnapshotResponse>> saveSnapshot(@Valid @RequestBody SnapshotRequest request) {
        SnapshotResponse snapshot = analyticsService.saveSnapshot(request);
        return ResponseEntity.ok(ApiResponse.success("Snapshot saved successfully", snapshot));
    }

    @GetMapping("/trends/{courseId}")
    @Operation(summary = "Get trends", description = "Get trend analysis for a course")
    public ResponseEntity<ApiResponse<TrendResponse>> getTrends(
            @PathVariable Long courseId,
            @RequestParam String metric,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) OffsetDateTime start,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) OffsetDateTime end) {
        TrendResponse trends = analyticsService.getTrends(courseId, metric, start, end);
        return ResponseEntity.ok(ApiResponse.success(trends));
    }
}
