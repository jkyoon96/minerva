package com.eduforum.api.domain.assessment.repository;

import com.eduforum.api.domain.assessment.entity.PlagiarismReport;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;

@Repository
public interface PlagiarismReportRepository extends JpaRepository<PlagiarismReport, Long> {

    List<PlagiarismReport> findByAssignmentId(Long assignmentId);

    List<PlagiarismReport> findByAssignmentIdAndIsFlagged(Long assignmentId, Boolean isFlagged);

    @Query("SELECT p FROM PlagiarismReport p WHERE p.assignmentId = :assignmentId AND p.similarityScore >= :threshold ORDER BY p.similarityScore DESC")
    List<PlagiarismReport> findByAssignmentIdAndSimilarityScoreGreaterThanEqual(@Param("assignmentId") Long assignmentId, @Param("threshold") BigDecimal threshold);

    List<PlagiarismReport> findBySubmissionId1OrSubmissionId2(Long submissionId1, Long submissionId2);
}
