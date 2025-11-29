package com.eduforum.api.domain.assessment.service;

import com.eduforum.api.common.exception.BusinessException;
import com.eduforum.api.common.exception.ErrorCode;
import com.eduforum.api.domain.assessment.dto.grading.GradingResultResponse;
import com.eduforum.api.domain.assessment.entity.GradingResult;
import com.eduforum.api.domain.assessment.entity.GradingStatus;
import com.eduforum.api.domain.assessment.entity.GradingType;
import com.eduforum.api.domain.assessment.repository.GradingResultRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class AiGradingService {

    private final GradingResultRepository gradingResultRepository;
    private final FeedbackService feedbackService;
    private final Random random = new Random();

    @Transactional
    public GradingResultResponse gradeSubmission(Long submissionId, Boolean generateFeedback, String aiModel) {
        log.info("AI grading submission: {} with model: {}", submissionId, aiModel);

        // Simulated AI grading
        BigDecimal score = simulateAiGrading(submissionId);
        BigDecimal confidence = BigDecimal.valueOf(85 + random.nextInt(15)); // 85-100%

        GradingResult result = GradingResult.builder()
            .submissionId(submissionId)
            .assignmentId(1L) // Simulated
            .studentId(1L) // Simulated
            .gradingType(GradingType.AI)
            .status(GradingStatus.GRADED)
            .score(score)
            .maxScore(BigDecimal.valueOf(100))
            .aiConfidence(confidence)
            .feedback(generateAiFeedback(score))
            .gradingDetails(createGradingDetails(aiModel, confidence))
            .build();

        result.markAsGraded();
        result = gradingResultRepository.save(result);

        if (generateFeedback) {
            feedbackService.generateFeedbackForSubmission(submissionId);
        }

        return toResponse(result);
    }

    @Transactional
    public GradingResultResponse updateGrade(Long resultId, Long userId, BigDecimal score, String feedback) {
        GradingResult result = gradingResultRepository.findById(resultId)
            .orElseThrow(() -> new BusinessException(ErrorCode.ENTITY_NOT_FOUND, "Grading result not found"));

        result.setScore(score);
        result.setFeedback(feedback);
        result.markAsReviewed(userId);

        result = gradingResultRepository.save(result);
        return toResponse(result);
    }

    @Transactional(readOnly = true)
    public List<GradingResultResponse> getPendingAiGrades(Long assignmentId) {
        List<GradingResult> results = gradingResultRepository.findByAssignmentIdAndStatus(
            assignmentId, GradingStatus.PENDING
        );

        return results.stream()
            .map(this::toResponse)
            .collect(Collectors.toList());
    }

    @Transactional
    public List<GradingResultResponse> batchGrade(List<Long> submissionIds, Boolean generateFeedback) {
        log.info("Batch AI grading {} submissions", submissionIds.size());

        return submissionIds.stream()
            .map(submissionId -> gradeSubmission(submissionId, generateFeedback, "gpt-4"))
            .collect(Collectors.toList());
    }

    private BigDecimal simulateAiGrading(Long submissionId) {
        // Simulated AI scoring - random score between 60-100
        return BigDecimal.valueOf(60 + random.nextInt(41));
    }

    private String generateAiFeedback(BigDecimal score) {
        int scoreValue = score.intValue();
        if (scoreValue >= 90) {
            return "Excellent work! Your answer demonstrates comprehensive understanding.";
        } else if (scoreValue >= 75) {
            return "Good effort. Your answer shows solid understanding with room for improvement.";
        } else if (scoreValue >= 60) {
            return "Fair work. Consider reviewing key concepts and providing more detail.";
        } else {
            return "Needs improvement. Please review the material and seek additional help.";
        }
    }

    private Map<String, Object> createGradingDetails(String aiModel, BigDecimal confidence) {
        Map<String, Object> details = new HashMap<>();
        details.put("ai_model", aiModel != null ? aiModel : "gpt-4");
        details.put("confidence", confidence);
        details.put("grading_timestamp", System.currentTimeMillis());
        return details;
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
}
