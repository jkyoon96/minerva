package com.eduforum.api.domain.course.repository;

import com.eduforum.api.domain.course.entity.Assignment;
import com.eduforum.api.domain.course.entity.AssignmentStatus;
import com.eduforum.api.domain.course.entity.Course;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.OffsetDateTime;
import java.util.List;

/**
 * Repository for Assignment entity
 */
@Repository
public interface AssignmentRepository extends JpaRepository<Assignment, Long> {

    /**
     * Find assignments by course
     */
    List<Assignment> findByCourse(Course course);

    /**
     * Find assignments by course ordered by due date
     */
    List<Assignment> findByCourseOrderByDueDateAsc(Course course);

    /**
     * Find published assignments by course
     */
    @Query("SELECT a FROM Assignment a WHERE a.course = :course AND a.status = 'PUBLISHED' " +
           "ORDER BY a.dueDate ASC")
    List<Assignment> findPublishedAssignmentsByCourse(@Param("course") Course course);

    /**
     * Find assignments by status
     */
    List<Assignment> findByStatus(AssignmentStatus status);

    /**
     * Find assignments by course and status
     */
    List<Assignment> findByCourseAndStatus(Course course, AssignmentStatus status);

    /**
     * Find upcoming assignments (published and not past due)
     */
    @Query("SELECT a FROM Assignment a WHERE a.course = :course " +
           "AND a.status = 'PUBLISHED' AND a.dueDate > :now " +
           "ORDER BY a.dueDate ASC")
    List<Assignment> findUpcomingAssignments(
        @Param("course") Course course,
        @Param("now") OffsetDateTime now
    );

    /**
     * Find past due assignments
     */
    @Query("SELECT a FROM Assignment a WHERE a.course = :course " +
           "AND a.status = 'PUBLISHED' AND a.dueDate < :now " +
           "ORDER BY a.dueDate DESC")
    List<Assignment> findPastDueAssignments(
        @Param("course") Course course,
        @Param("now") OffsetDateTime now
    );

    /**
     * Find assignments due between dates
     */
    @Query("SELECT a FROM Assignment a WHERE a.dueDate BETWEEN :start AND :end " +
           "AND a.status = 'PUBLISHED' ORDER BY a.dueDate ASC")
    List<Assignment> findAssignmentsDueBetween(
        @Param("start") OffsetDateTime start,
        @Param("end") OffsetDateTime end
    );
}
