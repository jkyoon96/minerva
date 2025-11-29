package com.eduforum.api.domain.analytics.controller;

import com.eduforum.api.common.dto.ApiResponse;
import com.eduforum.api.domain.analytics.dto.report.*;
import com.eduforum.api.domain.analytics.entity.ReportPeriod;
import com.eduforum.api.domain.analytics.service.*;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/v1/analytics/reports")
@RequiredArgsConstructor
@Tag(name = "Reports", description = "Learning reports and exports APIs")
public class ReportController {

    private final StudentReportService studentReportService;
    private final CourseReportService courseReportService;
    private final ExportService exportService;

    @GetMapping("/student/{studentId}")
    @Operation(summary = "Get student report", description = "Get learning report for a student")
    public ResponseEntity<ApiResponse<StudentReportResponse>> getStudentReport(
            @PathVariable Long studentId,
            @RequestParam Long courseId) {
        StudentReportResponse report = studentReportService.getStudentReport(studentId, courseId);
        return ResponseEntity.ok(ApiResponse.success(report));
    }

    @GetMapping("/course/{courseId}")
    @Operation(summary = "Get course report", description = "Get course-level report")
    public ResponseEntity<ApiResponse<List<StudentReportResponse>>> getCourseReports(@PathVariable Long courseId) {
        List<StudentReportResponse> reports = studentReportService.getCourseReports(courseId);
        return ResponseEntity.ok(ApiResponse.success(reports));
    }

    @PostMapping("/generate")
    @Operation(summary = "Generate report", description = "Generate new student report")
    public ResponseEntity<ApiResponse<StudentReportResponse>> generateReport(@Valid @RequestBody StudentReportRequest request) {
        StudentReportResponse report = studentReportService.generateReport(request);
        return ResponseEntity.ok(ApiResponse.success("Report generated successfully", report));
    }

    @PostMapping("/course/generate/{courseId}")
    @Operation(summary = "Generate course report", description = "Generate course-level report")
    public ResponseEntity<ApiResponse<CourseReportResponse>> generateCourseReport(
            @PathVariable Long courseId,
            @RequestParam ReportPeriod period) {
        CourseReportResponse report = courseReportService.generateCourseReport(courseId, period);
        return ResponseEntity.ok(ApiResponse.success("Course report generated", report));
    }

    @GetMapping("/export/excel/{courseId}")
    @Operation(summary = "Export to Excel", description = "Export course data to Excel")
    public ResponseEntity<Resource> exportToExcel(
            @PathVariable Long courseId,
            @Valid @ModelAttribute ExportRequest request) {
        Resource resource = exportService.exportToExcel(request);
        return ResponseEntity.ok()
            .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=course_" + courseId + "_report.csv")
            .contentType(MediaType.parseMediaType("application/vnd.ms-excel"))
            .body(resource);
    }

    @GetMapping("/{reportId}/download")
    @Operation(summary = "Download report PDF", description = "Download report as PDF")
    public ResponseEntity<Resource> downloadReport(@PathVariable Long reportId) {
        Resource resource = exportService.exportToPdf(reportId);
        return ResponseEntity.ok()
            .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=report_" + reportId + ".pdf")
            .contentType(MediaType.APPLICATION_PDF)
            .body(resource);
    }
}
