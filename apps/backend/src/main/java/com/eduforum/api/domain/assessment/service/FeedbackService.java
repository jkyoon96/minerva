package com.eduforum.api.domain.assessment.service;

import com.eduforum.api.common.exception.BusinessException;
import com.eduforum.api.common.exception.ErrorCode;
import com.eduforum.api.domain.assessment.dto.feedback.FeedbackResponse;
import com.eduforum.api.domain.assessment.dto.feedback.LearningResourceResponse;
import com.eduforum.api.domain.assessment.entity.*;
import com.eduforum.api.domain.assessment.repository.FeedbackRepository;
import com.eduforum.api.domain.assessment.repository.GradingResultRepository;
import com.eduforum.api.domain.assessment.repository.LearningResourceRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class FeedbackService {

    private final FeedbackRepository feedbackRepository;
    private final LearningResourceRepository learningResourceRepository;
    private final GradingResultRepository gradingResultRepository;

    @Transactional
    public FeedbackResponse generateFeedbackForSubmission(Long submissionId) {
        log.info("Generating AI feedback for submission: {}", submissionId);

        // Simulated AI feedback generation
        Feedback feedback = Feedback.builder()
            .submissionId(submissionId)
            .studentId(1L) // Simulated
            .courseId(1L) // Simulated
            .feedbackType(FeedbackType.PERFORMANCE)
            .title("Feedback on Your Submission")
            .content(generateAiFeedbackContent())
            .isAiGenerated(true)
            .metadata(new HashMap<>())
            .build();

        feedback = feedbackRepository.save(feedback);
        return toResponse(feedback);
    }

    @Transactional
    public FeedbackResponse saveFeedback(Long studentId, Long courseId, Long submissionId, FeedbackType feedbackType, String title, String content, Long generatedBy) {
        Feedback feedback = Feedback.builder()
            .studentId(studentId)
            .courseId(courseId)
            .submissionId(submissionId)
            .feedbackType(feedbackType)
            .title(title)
            .content(content)
            .isAiGenerated(false)
            .generatedBy(generatedBy)
            .metadata(new HashMap<>())
            .build();

        feedback = feedbackRepository.save(feedback);
        return toResponse(feedback);
    }

    @Transactional(readOnly = true)
    public FeedbackResponse getFeedback(Long feedbackId) {
        Feedback feedback = feedbackRepository.findById(feedbackId)
            .orElseThrow(() -> new BusinessException(ErrorCode.ENTITY_NOT_FOUND, "Feedback not found"));

        return toResponse(feedback);
    }

    @Transactional(readOnly = true)
    public List<FeedbackResponse> getStudentFeedbacks(Long studentId, Long courseId) {
        List<Feedback> feedbacks;
        if (courseId != null) {
            feedbacks = feedbackRepository.findByStudentIdAndCourseId(studentId, courseId);
        } else {
            feedbacks = feedbackRepository.findByStudentId(studentId);
        }

        return feedbacks.stream()
            .map(this::toResponse)
            .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<LearningResourceResponse> getRecommendations(Long studentId, Long courseId) {
        List<LearningResource> resources;
        if (courseId != null) {
            resources = learningResourceRepository.findByStudentIdAndCourseId(studentId, courseId);
        } else {
            resources = learningResourceRepository.findByStudentId(studentId);
        }

        if (resources.isEmpty()) {
            // Generate recommendations
            resources = generateRecommendations(studentId, courseId);
        }

        return resources.stream()
            .map(this::toResourceResponse)
            .collect(Collectors.toList());
    }

    private List<LearningResource> generateRecommendations(Long studentId, Long courseId) {
        // Simulated resource recommendation based on student performance
        List<LearningResource> resources = List.of(
            createResource(studentId, courseId, "Data Structures", "VIDEO", "Introduction to Trees and Graphs", "INTERMEDIATE"),
            createResource(studentId, courseId, "Algorithms", "ARTICLE", "Understanding Dynamic Programming", "ADVANCED"),
            createResource(studentId, courseId, "Problem Solving", "EXERCISE", "Practice Problems - Recursion", "BEGINNER")
        );

        return resources.stream()
            .map(learningResourceRepository::save)
            .collect(Collectors.toList());
    }

    private LearningResource createResource(Long studentId, Long courseId, String topic, String type, String title, String level) {
        return LearningResource.builder()
            .studentId(studentId)
            .courseId(courseId)
            .topic(topic)
            .resourceType(type)
            .title(title)
            .url("https://example.com/resource")
            .description("Recommended learning resource")
            .difficultyLevel(level)
            .estimatedDurationMinutes(30)
            .relevanceScore(85)
            .tags(new HashMap<>())
            .build();
    }

    private String generateAiFeedbackContent() {
        return "Your submission shows good understanding of the core concepts. " +
               "Consider reviewing the following areas for improvement: " +
               "1) Edge case handling, 2) Code optimization, 3) Documentation. " +
               "Keep up the great work!";
    }

    private FeedbackResponse toResponse(Feedback feedback) {
        return FeedbackResponse.builder()
            .id(feedback.getId())
            .studentId(feedback.getStudentId())
            .courseId(feedback.getCourseId())
            .submissionId(feedback.getSubmissionId())
            .feedbackType(feedback.getFeedbackType())
            .title(feedback.getTitle())
            .content(feedback.getContent())
            .isAiGenerated(feedback.getIsAiGenerated())
            .generatedBy(feedback.getGeneratedBy())
            .metadata(feedback.getMetadata())
            .isRead(feedback.getIsRead())
            .readAt(feedback.getReadAt())
            .sentAt(feedback.getSentAt())
            .build();
    }

    private LearningResourceResponse toResourceResponse(LearningResource resource) {
        return LearningResourceResponse.builder()
            .id(resource.getId())
            .topic(resource.getTopic())
            .resourceType(resource.getResourceType())
            .title(resource.getTitle())
            .url(resource.getUrl())
            .description(resource.getDescription())
            .difficultyLevel(resource.getDifficultyLevel())
            .estimatedDurationMinutes(resource.getEstimatedDurationMinutes())
            .relevanceScore(resource.getRelevanceScore())
            .tags(resource.getTags())
            .isCompleted(resource.getIsCompleted())
            .isBookmarked(resource.getIsBookmarked())
            .build();
    }
}
