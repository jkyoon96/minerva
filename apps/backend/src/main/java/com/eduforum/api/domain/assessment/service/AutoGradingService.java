package com.eduforum.api.domain.assessment.service;

import com.eduforum.api.common.exception.BusinessException;
import com.eduforum.api.common.exception.ErrorCode;
import com.eduforum.api.domain.assessment.dto.grading.AnswerStatisticsResponse;
import com.eduforum.api.domain.assessment.dto.grading.GradingResultResponse;
import com.eduforum.api.domain.assessment.entity.*;
import com.eduforum.api.domain.assessment.repository.AnswerStatisticsRepository;
import com.eduforum.api.domain.assessment.repository.GradingResultRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class AutoGradingService {

    private final GradingResultRepository gradingResultRepository;
    private final AnswerStatisticsRepository answerStatisticsRepository;

    @Transactional
    public List<GradingResultResponse> gradeQuizSession(Long quizSessionId, Boolean includeStatistics) {
        log.info("Auto grading quiz session: {}", quizSessionId);

        // Simulated: In real implementation, fetch quiz responses and grade them
        // For now, create mock grading results
        List<GradingResult> results = createMockGradingResults(quizSessionId);

        if (includeStatistics) {
            updateAnswerStatistics(quizSessionId);
        }

        return results.stream()
            .map(this::toResponse)
            .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public GradingResultResponse getGradingResult(Long resultId) {
        GradingResult result = gradingResultRepository.findById(resultId)
            .orElseThrow(() -> new BusinessException(ErrorCode.ENTITY_NOT_FOUND, "Grading result not found"));

        return toResponse(result);
    }

    @Transactional(readOnly = true)
    public List<GradingResultResponse> getAssignmentGrades(Long assignmentId) {
        List<GradingResult> results = gradingResultRepository.findByAssignmentId(assignmentId);

        return results.stream()
            .map(this::toResponse)
            .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public AnswerStatisticsResponse getAnswerStatistics(Long quizId, Long questionId) {
        AnswerStatistics statistics = answerStatisticsRepository.findByQuizIdAndQuestionId(quizId, questionId)
            .orElseThrow(() -> new BusinessException(ErrorCode.ENTITY_NOT_FOUND, "Answer statistics not found"));

        return toStatisticsResponse(statistics);
    }

    @Transactional(readOnly = true)
    public List<AnswerStatisticsResponse> getQuizStatistics(Long quizId) {
        List<AnswerStatistics> statisticsList = answerStatisticsRepository.findByQuizId(quizId);

        return statisticsList.stream()
            .map(this::toStatisticsResponse)
            .collect(Collectors.toList());
    }

    private List<GradingResult> createMockGradingResults(Long quizSessionId) {
        // Simulated grading - in real implementation, this would process actual quiz responses
        // For demonstration, creating sample results
        return List.of(); // Implementation placeholder
    }

    private void updateAnswerStatistics(Long quizSessionId) {
        // Simulated statistics update
        log.info("Updating answer statistics for quiz session: {}", quizSessionId);
    }

    private GradingResultResponse toResponse(GradingResult result) {
        return GradingResultResponse.builder()
            .id(result.getId())
            .assignmentId(result.getAssignmentId())
            .submissionId(result.getSubmissionId())
            .studentId(result.getStudentId())
            .graderId(result.getGraderId())
            .gradingType(result.getGradingType())
            .status(result.getStatus())
            .score(result.getScore())
            .maxScore(result.getMaxScore())
            .aiConfidence(result.getAiConfidence())
            .feedback(result.getFeedback())
            .gradingDetails(result.getGradingDetails())
            .gradedAt(result.getGradedAt())
            .reviewedAt(result.getReviewedAt())
            .createdAt(result.getCreatedAt())
            .build();
    }

    private AnswerStatisticsResponse toStatisticsResponse(AnswerStatistics statistics) {
        return AnswerStatisticsResponse.builder()
            .id(statistics.getId())
            .quizId(statistics.getQuizId())
            .questionId(statistics.getQuestionId())
            .totalResponses(statistics.getTotalResponses())
            .correctResponses(statistics.getCorrectResponses())
            .incorrectResponses(statistics.getIncorrectResponses())
            .accuracyRate(statistics.getAccuracyRate())
            .optionDistribution(statistics.getOptionDistribution())
            .responseTimeStats(statistics.getResponseTimeStats())
            .build();
    }
}
