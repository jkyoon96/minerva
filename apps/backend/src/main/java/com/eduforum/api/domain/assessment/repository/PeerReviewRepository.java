package com.eduforum.api.domain.assessment.repository;

import com.eduforum.api.domain.assessment.entity.PeerReview;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PeerReviewRepository extends JpaRepository<PeerReview, Long> {

    List<PeerReview> findByAssignmentId(Long assignmentId);

    List<PeerReview> findBySubmissionId(Long submissionId);

    List<PeerReview> findByReviewerId(Long reviewerId);

    List<PeerReview> findByRevieweeId(Long revieweeId);

    List<PeerReview> findBySubmissionIdAndIsSubmitted(Long submissionId, Boolean isSubmitted);

    List<PeerReview> findByAssignmentIdAndIsOutlier(Long assignmentId, Boolean isOutlier);
}
