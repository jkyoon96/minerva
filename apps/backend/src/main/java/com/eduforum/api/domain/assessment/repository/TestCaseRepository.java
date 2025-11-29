package com.eduforum.api.domain.assessment.repository;

import com.eduforum.api.domain.assessment.entity.TestCase;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TestCaseRepository extends JpaRepository<TestCase, Long> {

    List<TestCase> findByAssignmentId(Long assignmentId);

    List<TestCase> findByAssignmentIdAndIsHidden(Long assignmentId, Boolean isHidden);

    List<TestCase> findByAssignmentIdOrderByDisplayOrder(Long assignmentId);
}
