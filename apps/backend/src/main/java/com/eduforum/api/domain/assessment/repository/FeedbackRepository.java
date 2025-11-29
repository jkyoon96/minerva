package com.eduforum.api.domain.assessment.repository;

import com.eduforum.api.domain.assessment.entity.Feedback;
import com.eduforum.api.domain.assessment.entity.FeedbackType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface FeedbackRepository extends JpaRepository<Feedback, Long> {

    List<Feedback> findByStudentId(Long studentId);

    List<Feedback> findByStudentIdAndCourseId(Long studentId, Long courseId);

    List<Feedback> findByStudentIdAndIsRead(Long studentId, Boolean isRead);

    List<Feedback> findBySubmissionId(Long submissionId);

    List<Feedback> findByGradingResultId(Long gradingResultId);

    List<Feedback> findByStudentIdAndFeedbackType(Long studentId, FeedbackType feedbackType);
}
