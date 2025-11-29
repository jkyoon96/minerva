package com.eduforum.api.domain.assessment.repository;

import com.eduforum.api.domain.assessment.entity.CodeSubmission;
import com.eduforum.api.domain.assessment.entity.SubmissionStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CodeSubmissionRepository extends JpaRepository<CodeSubmission, Long> {

    List<CodeSubmission> findByAssignmentId(Long assignmentId);

    List<CodeSubmission> findByStudentId(Long studentId);

    List<CodeSubmission> findByAssignmentIdAndStudentId(Long assignmentId, Long studentId);

    List<CodeSubmission> findByAssignmentIdAndStatus(Long assignmentId, SubmissionStatus status);

    Optional<CodeSubmission> findFirstByAssignmentIdAndStudentIdOrderBySubmittedAtDesc(Long assignmentId, Long studentId);
}
