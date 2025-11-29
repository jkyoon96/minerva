package com.eduforum.api.domain.assessment.service;

import com.eduforum.api.common.exception.BusinessException;
import com.eduforum.api.common.exception.ErrorCode;
import com.eduforum.api.domain.assessment.dto.code.PlagiarismReportResponse;
import com.eduforum.api.domain.assessment.entity.CodeSubmission;
import com.eduforum.api.domain.assessment.entity.PlagiarismReport;
import com.eduforum.api.domain.assessment.repository.CodeSubmissionRepository;
import com.eduforum.api.domain.assessment.repository.PlagiarismReportRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class PlagiarismService {

    private final CodeSubmissionRepository codeSubmissionRepository;
    private final PlagiarismReportRepository plagiarismReportRepository;

    @Transactional
    public List<PlagiarismReportResponse> checkPlagiarism(Long assignmentId, BigDecimal threshold, String algorithm) {
        log.info("Running plagiarism check for assignment {} with threshold {}", assignmentId, threshold);

        List<CodeSubmission> submissions = codeSubmissionRepository.findByAssignmentId(assignmentId);
        List<PlagiarismReport> reports = new ArrayList<>();

        // Compare all pairs of submissions
        for (int i = 0; i < submissions.size(); i++) {
            for (int j = i + 1; j < submissions.size(); j++) {
                CodeSubmission submission1 = submissions.get(i);
                CodeSubmission submission2 = submissions.get(j);

                BigDecimal similarity = calculateSimilarity(submission1.getCode(), submission2.getCode(), algorithm);

                if (similarity.compareTo(threshold) >= 0) {
                    PlagiarismReport report = createPlagiarismReport(
                        assignmentId, submission1, submission2, similarity, algorithm
                    );
                    reports.add(plagiarismReportRepository.save(report));
                }
            }
        }

        return reports.stream()
            .map(this::toResponse)
            .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public PlagiarismReportResponse getReport(Long reportId) {
        PlagiarismReport report = plagiarismReportRepository.findById(reportId)
            .orElseThrow(() -> new BusinessException(ErrorCode.ENTITY_NOT_FOUND, "Plagiarism report not found"));

        return toResponse(report);
    }

    @Transactional(readOnly = true)
    public List<PlagiarismReportResponse> getAssignmentReports(Long assignmentId, Boolean flaggedOnly) {
        List<PlagiarismReport> reports;

        if (flaggedOnly != null && flaggedOnly) {
            reports = plagiarismReportRepository.findByAssignmentIdAndIsFlagged(assignmentId, true);
        } else {
            reports = plagiarismReportRepository.findByAssignmentId(assignmentId);
        }

        return reports.stream()
            .map(this::toResponse)
            .collect(Collectors.toList());
    }

    private BigDecimal calculateSimilarity(String code1, String code2, String algorithm) {
        // Simulated similarity calculation
        // In real implementation, use algorithms like:
        // - Levenshtein distance
        // - Jaccard similarity
        // - Token-based comparison
        // - AST (Abstract Syntax Tree) comparison

        if (code1 == null || code2 == null) {
            return BigDecimal.ZERO;
        }

        // Simple simulation: random similarity between 0-100
        double similarity = Math.random() * 100;
        return BigDecimal.valueOf(Math.round(similarity * 100.0) / 100.0);
    }

    private PlagiarismReport createPlagiarismReport(
        Long assignmentId,
        CodeSubmission submission1,
        CodeSubmission submission2,
        BigDecimal similarity,
        String algorithm
    ) {
        Map<String, Object> matchedSegments = new HashMap<>();
        matchedSegments.put("total_matches", 5);
        matchedSegments.put("longest_match_length", 50);

        PlagiarismReport report = PlagiarismReport.builder()
            .assignmentId(assignmentId)
            .submissionId1(submission1.getId())
            .submissionId2(submission2.getId())
            .studentId1(submission1.getStudentId())
            .studentId2(submission2.getStudentId())
            .similarityScore(similarity)
            .algorithm(algorithm)
            .matchedSegments(matchedSegments)
            .analysisDetails(new HashMap<>())
            .isFlagged(similarity.compareTo(BigDecimal.valueOf(70)) > 0)
            .build();

        return report;
    }

    private PlagiarismReportResponse toResponse(PlagiarismReport report) {
        return PlagiarismReportResponse.builder()
            .id(report.getId())
            .assignmentId(report.getAssignmentId())
            .submissionId1(report.getSubmissionId1())
            .submissionId2(report.getSubmissionId2())
            .studentId1(report.getStudentId1())
            .studentId2(report.getStudentId2())
            .similarityScore(report.getSimilarityScore())
            .algorithm(report.getAlgorithm())
            .matchedSegments(report.getMatchedSegments())
            .isFlagged(report.getIsFlagged())
            .reviewedBy(report.getReviewedBy())
            .reviewedAt(report.getReviewedAt())
            .reviewNotes(report.getReviewNotes())
            .checkedAt(report.getCheckedAt())
            .build();
    }
}
