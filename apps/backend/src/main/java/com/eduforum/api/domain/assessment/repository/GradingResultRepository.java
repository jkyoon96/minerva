package com.eduforum.api.domain.assessment.repository;

import com.eduforum.api.domain.assessment.entity.GradingResult;
import com.eduforum.api.domain.assessment.entity.GradingStatus;
import com.eduforum.api.domain.assessment.entity.GradingType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface GradingResultRepository extends JpaRepository<GradingResult, Long> {

    List<GradingResult> findByAssignmentId(Long assignmentId);

    List<GradingResult> findByStudentId(Long studentId);

    Optional<GradingResult> findBySubmissionId(Long submissionId);

    List<GradingResult> findByAssignmentIdAndStatus(Long assignmentId, GradingStatus status);

    List<GradingResult> findByAssignmentIdAndGradingType(Long assignmentId, GradingType gradingType);

    List<GradingResult> findByStudentIdAndAssignmentId(Long studentId, Long assignmentId);
}
