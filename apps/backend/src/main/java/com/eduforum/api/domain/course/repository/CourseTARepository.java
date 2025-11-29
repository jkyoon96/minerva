package com.eduforum.api.domain.course.repository;

import com.eduforum.api.domain.auth.entity.User;
import com.eduforum.api.domain.course.entity.Course;
import com.eduforum.api.domain.course.entity.CourseTA;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Repository for course TAs
 */
@Repository
public interface CourseTARepository extends JpaRepository<CourseTA, Long> {

    /**
     * Find all TAs for a course (not deleted)
     */
    @Query("SELECT ct FROM CourseTA ct WHERE ct.course = :course " +
           "AND ct.deletedAt IS NULL ORDER BY ct.assignedAt ASC")
    List<CourseTA> findByCourseOrderByAssignedAt(@Param("course") Course course);

    /**
     * Find all TAs for a course by course ID
     */
    @Query("SELECT ct FROM CourseTA ct WHERE ct.course.id = :courseId " +
           "AND ct.deletedAt IS NULL ORDER BY ct.assignedAt ASC")
    List<CourseTA> findByCourseIdOrderByAssignedAt(@Param("courseId") Long courseId);

    /**
     * Find all courses where user is a TA
     */
    @Query("SELECT ct FROM CourseTA ct WHERE ct.taUser = :user " +
           "AND ct.deletedAt IS NULL ORDER BY ct.assignedAt DESC")
    List<CourseTA> findByTaUser(@Param("user") User user);

    /**
     * Find TA assignment by course and user
     */
    @Query("SELECT ct FROM CourseTA ct WHERE ct.course = :course " +
           "AND ct.taUser = :user AND ct.deletedAt IS NULL")
    Optional<CourseTA> findByCourseAndTaUser(
        @Param("course") Course course,
        @Param("user") User user
    );

    /**
     * Check if user is TA for a course
     */
    @Query("SELECT CASE WHEN COUNT(ct) > 0 THEN true ELSE false END " +
           "FROM CourseTA ct WHERE ct.course.id = :courseId " +
           "AND ct.taUser.id = :userId AND ct.deletedAt IS NULL")
    boolean existsByCourseIdAndTaUserId(
        @Param("courseId") Long courseId,
        @Param("userId") Long userId
    );

    /**
     * Count TAs for a course
     */
    @Query("SELECT COUNT(ct) FROM CourseTA ct WHERE ct.course.id = :courseId " +
           "AND ct.deletedAt IS NULL")
    Long countByCourseId(@Param("courseId") Long courseId);
}
