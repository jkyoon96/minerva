package com.eduforum.api.domain.assessment.service;

import com.eduforum.api.common.exception.BusinessException;
import com.eduforum.api.common.exception.ErrorCode;
import com.eduforum.api.domain.assessment.dto.peer.*;
import com.eduforum.api.domain.assessment.entity.PeerReview;
import com.eduforum.api.domain.assessment.entity.PeerReviewAssignment;
import com.eduforum.api.domain.assessment.repository.PeerReviewAssignmentRepository;
import com.eduforum.api.domain.assessment.repository.PeerReviewRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.OffsetDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class PeerReviewService {

    private final PeerReviewRepository peerReviewRepository;
    private final PeerReviewAssignmentRepository peerReviewAssignmentRepository;

    @Transactional
    public void setupPeerReview(Long assignmentId, SetupPeerReviewRequest request) {
        log.info("Setting up peer review for assignment: {}", assignmentId);

        PeerReviewAssignment assignment = peerReviewAssignmentRepository.findByAssignmentId(assignmentId)
            .orElse(new PeerReviewAssignment());

        assignment.setAssignmentId(assignmentId);
        assignment.setReviewsPerSubmission(request.getReviewsPerSubmission());
        assignment.setIsAnonymous(request.getIsAnonymous());
        assignment.setIsAutoAssigned(request.getIsAutoAssigned());
        assignment.setReviewDeadline(request.getReviewDeadline());
        assignment.setMinScore(request.getMinScore());
        assignment.setMaxScore(request.getMaxScore());
        assignment.setRubric(request.getRubric());
        assignment.setRemoveOutliers(request.getRemoveOutliers());
        assignment.activate();

        peerReviewAssignmentRepository.save(assignment);

        if (request.getIsAutoAssigned()) {
            autoAssignReviewers(assignmentId, request.getReviewsPerSubmission());
        }
    }

    @Transactional(readOnly = true)
    public List<PeerReviewAssignmentResponse> getReviewAssignments(Long assignmentId, Long reviewerId) {
        List<PeerReview> reviews = peerReviewRepository.findByReviewerId(reviewerId);

        return reviews.stream()
            .filter(r -> r.getAssignmentId().equals(assignmentId))
            .map(r -> PeerReviewAssignmentResponse.builder()
                .assignmentId(r.getAssignmentId())
                .submissionId(r.getSubmissionId())
                .submissionNumber(1)
                .totalAssignments(reviews.size())
                .isCompleted(r.getIsSubmitted())
                .build())
            .collect(Collectors.toList());
    }

    @Transactional
    public PeerReviewResponse submitReview(Long reviewerId, Long submissionId, BigDecimal score, String comments, Map<String, Object> rubricScores) {
        PeerReview review = peerReviewRepository.findBySubmissionId(submissionId).stream()
            .filter(r -> r.getReviewerId().equals(reviewerId))
            .findFirst()
            .orElseThrow(() -> new BusinessException(ErrorCode.ENTITY_NOT_FOUND, "Review assignment not found"));

        review.setScore(score);
        review.setComments(comments);
        review.setRubricScores(rubricScores != null ? rubricScores : new HashMap<>());
        review.submit();

        review = peerReviewRepository.save(review);

        // Check for outliers if all reviews are submitted
        checkAndMarkOutliers(submissionId);

        return toResponse(review);
    }

    @Transactional(readOnly = true)
    public List<PeerReviewResponse> getReceivedReviews(Long submissionId) {
        List<PeerReview> reviews = peerReviewRepository.findBySubmissionIdAndIsSubmitted(submissionId, true);

        return reviews.stream()
            .map(this::toResponse)
            .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<PeerReviewResponse> getGivenReviews(Long reviewerId) {
        List<PeerReview> reviews = peerReviewRepository.findByReviewerId(reviewerId);

        return reviews.stream()
            .map(this::toResponse)
            .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public PeerReviewResultResponse getAggregatedResults(Long submissionId) {
        List<PeerReview> reviews = peerReviewRepository.findBySubmissionIdAndIsSubmitted(submissionId, true);

        if (reviews.isEmpty()) {
            throw new BusinessException(ErrorCode.ENTITY_NOT_FOUND, "No reviews found");
        }

        // Filter out outliers
        List<PeerReview> validReviews = reviews.stream()
            .filter(r -> !r.getIsOutlier())
            .collect(Collectors.toList());

        if (validReviews.isEmpty()) {
            validReviews = reviews; // Use all reviews if all marked as outliers
        }

        List<BigDecimal> scores = validReviews.stream()
            .map(PeerReview::getScore)
            .filter(Objects::nonNull)
            .sorted()
            .collect(Collectors.toList());

        return PeerReviewResultResponse.builder()
            .submissionId(submissionId)
            .studentId(reviews.get(0).getRevieweeId())
            .averageScore(calculateAverage(scores))
            .medianScore(calculateMedian(scores))
            .minScore(scores.isEmpty() ? BigDecimal.ZERO : scores.get(0))
            .maxScore(scores.isEmpty() ? BigDecimal.ZERO : scores.get(scores.size() - 1))
            .standardDeviation(calculateStandardDeviation(scores))
            .totalReviews(reviews.size())
            .outliersRemoved(reviews.size() - validReviews.size())
            .reviews(validReviews.stream().map(this::toResponse).collect(Collectors.toList()))
            .build();
    }

    private void autoAssignReviewers(Long assignmentId, Integer reviewsPerSubmission) {
        // Simulated auto-assignment logic
        // In real implementation:
        // 1. Get all submissions for the assignment
        // 2. Randomly assign reviewers ensuring no self-review
        // 3. Create PeerReview records
        log.info("Auto-assigning peer reviewers for assignment: {}", assignmentId);
    }

    private void checkAndMarkOutliers(Long submissionId) {
        List<PeerReview> reviews = peerReviewRepository.findBySubmissionIdAndIsSubmitted(submissionId, true);

        if (reviews.size() < 3) {
            return; // Need at least 3 reviews to detect outliers
        }

        List<BigDecimal> scores = reviews.stream()
            .map(PeerReview::getScore)
            .filter(Objects::nonNull)
            .collect(Collectors.toList());

        BigDecimal mean = calculateAverage(scores);
        BigDecimal stdDev = calculateStandardDeviation(scores);

        // Mark outliers (scores > 2 standard deviations from mean)
        for (PeerReview review : reviews) {
            if (review.getScore() != null) {
                BigDecimal diff = review.getScore().subtract(mean).abs();
                if (diff.compareTo(stdDev.multiply(BigDecimal.valueOf(2))) > 0) {
                    review.markAsOutlier();
                    peerReviewRepository.save(review);
                }
            }
        }
    }

    private BigDecimal calculateAverage(List<BigDecimal> values) {
        if (values.isEmpty()) return BigDecimal.ZERO;
        BigDecimal sum = values.stream().reduce(BigDecimal.ZERO, BigDecimal::add);
        return sum.divide(BigDecimal.valueOf(values.size()), 2, RoundingMode.HALF_UP);
    }

    private BigDecimal calculateMedian(List<BigDecimal> sortedValues) {
        if (sortedValues.isEmpty()) return BigDecimal.ZERO;
        int size = sortedValues.size();
        if (size % 2 == 0) {
            return sortedValues.get(size / 2 - 1).add(sortedValues.get(size / 2))
                .divide(BigDecimal.valueOf(2), 2, RoundingMode.HALF_UP);
        } else {
            return sortedValues.get(size / 2);
        }
    }

    private BigDecimal calculateStandardDeviation(List<BigDecimal> values) {
        if (values.size() < 2) return BigDecimal.ZERO;

        BigDecimal mean = calculateAverage(values);
        BigDecimal variance = values.stream()
            .map(v -> v.subtract(mean).pow(2))
            .reduce(BigDecimal.ZERO, BigDecimal::add)
            .divide(BigDecimal.valueOf(values.size()), 2, RoundingMode.HALF_UP);

        return BigDecimal.valueOf(Math.sqrt(variance.doubleValue()));
    }

    private PeerReviewResponse toResponse(PeerReview review) {
        return PeerReviewResponse.builder()
            .id(review.getId())
            .assignmentId(review.getAssignmentId())
            .submissionId(review.getSubmissionId())
            .reviewerId(review.getIsAnonymous() ? null : review.getReviewerId())
            .revieweeId(review.getRevieweeId())
            .score(review.getScore())
            .maxScore(review.getMaxScore())
            .comments(review.getComments())
            .rubricScores(review.getRubricScores())
            .isSubmitted(review.getIsSubmitted())
            .submittedAt(review.getSubmittedAt())
            .isOutlier(review.getIsOutlier())
            .isAnonymous(review.getIsAnonymous())
            .build();
    }
}
