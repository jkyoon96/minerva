package com.eduforum.api.domain.analytics.controller;

import com.eduforum.api.common.dto.ApiResponse;
import com.eduforum.api.domain.analytics.dto.network.*;
import com.eduforum.api.domain.analytics.service.NetworkAnalysisService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/v1/analytics/network")
@RequiredArgsConstructor
@Tag(name = "Network Analytics", description = "Interaction network analysis APIs")
public class NetworkAnalyticsController {

    private final NetworkAnalysisService networkService;

    @PostMapping("/log")
    @Operation(summary = "Log interaction", description = "Log a student-student interaction")
    public ResponseEntity<ApiResponse<String>> logInteraction(@Valid @RequestBody InteractionLogRequest request) {
        networkService.logInteraction(request);
        return ResponseEntity.ok(ApiResponse.success("Interaction logged successfully"));
    }

    @GetMapping("/{courseId}")
    @Operation(summary = "Get network graph", description = "Get interaction network graph for a course")
    public ResponseEntity<ApiResponse<NetworkGraphResponse>> getNetworkGraph(@PathVariable Long courseId) {
        NetworkGraphResponse graph = networkService.getNetworkGraph(courseId);
        return ResponseEntity.ok(ApiResponse.success(graph));
    }

    @GetMapping("/{courseId}/clusters")
    @Operation(summary = "Get clusters", description = "Get student clusters for a course")
    public ResponseEntity<ApiResponse<List<ClusterResponse>>> getClusters(@PathVariable Long courseId) {
        List<ClusterResponse> clusters = networkService.getClusters(courseId);
        return ResponseEntity.ok(ApiResponse.success(clusters));
    }

    @GetMapping("/student/{studentId}")
    @Operation(summary = "Get student connections", description = "Get connection analysis for a student")
    public ResponseEntity<ApiResponse<StudentConnectionResponse>> getStudentConnections(
            @PathVariable Long studentId,
            @RequestParam Long courseId) {
        StudentConnectionResponse connections = networkService.getStudentConnections(courseId, studentId);
        return ResponseEntity.ok(ApiResponse.success(connections));
    }

    @PostMapping("/analyze/{courseId}")
    @Operation(summary = "Analyze network", description = "Run network analysis algorithms")
    public ResponseEntity<ApiResponse<String>> analyzeNetwork(@PathVariable Long courseId) {
        networkService.analyzeNetwork(courseId);
        return ResponseEntity.ok(ApiResponse.success("Network analysis completed for course " + courseId));
    }
}
