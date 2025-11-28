package com.eduforum.api.domain.course.repository;

import com.eduforum.api.domain.auth.entity.User;
import com.eduforum.api.domain.course.entity.Assignment;
import com.eduforum.api.domain.course.entity.AssignmentSubmission;
import com.eduforum.api.domain.course.entity.Course;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Repository for AssignmentSubmission entity
 */
@Repository
public interface AssignmentSubmissionRepository extends JpaRepository<AssignmentSubmission, Long> {

    /**
     * Find submissions by assignment
     */
    @Query("SELECT s FROM AssignmentSubmission s WHERE s.assignment = :assignment AND s.deletedAt IS NULL")
    List<AssignmentSubmission> findByAssignment(@Param("assignment") Assignment assignment);

    /**
     * Find submissions by assignment and student
     */
    @Query("SELECT s FROM AssignmentSubmission s WHERE s.assignment = :assignment AND s.student = :student AND s.deletedAt IS NULL ORDER BY s.attemptNumber DESC")
    List<AssignmentSubmission> findByAssignmentAndStudent(
        @Param("assignment") Assignment assignment,
        @Param("student") User student
    );

    /**
     * Find latest submission by assignment and student
     */
    @Query("SELECT s FROM AssignmentSubmission s WHERE s.assignment = :assignment AND s.student = :student AND s.deletedAt IS NULL ORDER BY s.attemptNumber DESC LIMIT 1")
    Optional<AssignmentSubmission> findLatestSubmission(
        @Param("assignment") Assignment assignment,
        @Param("student") User student
    );

    /**
     * Find ungraded submissions for a course
     */
    @Query("SELECT s FROM AssignmentSubmission s WHERE s.assignment.course = :course AND s.score IS NULL AND s.deletedAt IS NULL")
    List<AssignmentSubmission> findUngradedSubmissionsByCourse(@Param("course") Course course);

    /**
     * Count ungraded submissions for a course
     */
    @Query("SELECT COUNT(s) FROM AssignmentSubmission s WHERE s.assignment.course = :course AND s.score IS NULL AND s.deletedAt IS NULL")
    Long countUngradedSubmissionsByCourse(@Param("course") Course course);

    /**
     * Find recent graded submissions for a student
     */
    @Query("SELECT s FROM AssignmentSubmission s WHERE s.student = :student AND s.score IS NOT NULL AND s.deletedAt IS NULL ORDER BY s.gradedAt DESC")
    List<AssignmentSubmission> findRecentGradedSubmissionsByStudent(@Param("student") User student);

    /**
     * Count submissions for an assignment
     */
    @Query("SELECT COUNT(s) FROM AssignmentSubmission s WHERE s.assignment = :assignment AND s.deletedAt IS NULL")
    Long countByAssignment(@Param("assignment") Assignment assignment);
}
