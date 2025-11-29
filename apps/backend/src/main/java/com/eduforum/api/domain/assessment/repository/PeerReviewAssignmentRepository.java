package com.eduforum.api.domain.assessment.repository;

import com.eduforum.api.domain.assessment.entity.PeerReviewAssignment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface PeerReviewAssignmentRepository extends JpaRepository<PeerReviewAssignment, Long> {

    Optional<PeerReviewAssignment> findByAssignmentId(Long assignmentId);

    boolean existsByAssignmentId(Long assignmentId);
}
