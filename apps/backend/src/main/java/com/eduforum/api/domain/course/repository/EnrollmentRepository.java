package com.eduforum.api.domain.course.repository;

import com.eduforum.api.domain.auth.entity.User;
import com.eduforum.api.domain.course.entity.Course;
import com.eduforum.api.domain.course.entity.Enrollment;
import com.eduforum.api.domain.course.entity.EnrollmentRole;
import com.eduforum.api.domain.course.entity.EnrollmentStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Repository for Enrollment entity
 */
@Repository
public interface EnrollmentRepository extends JpaRepository<Enrollment, Long> {

    /**
     * Find enrollment by user and course
     */
    Optional<Enrollment> findByUserAndCourse(User user, Course course);

    /**
     * Find enrollments by course
     */
    List<Enrollment> findByCourse(Course course);

    /**
     * Find active enrollments by course
     */
    @Query("SELECT e FROM Enrollment e WHERE e.course = :course AND e.status = 'ACTIVE'")
    List<Enrollment> findActiveEnrollmentsByCourse(@Param("course") Course course);

    /**
     * Find enrollments by user
     */
    List<Enrollment> findByUser(User user);

    /**
     * Find active enrollments by user
     */
    @Query("SELECT e FROM Enrollment e WHERE e.user = :user AND e.status = 'ACTIVE'")
    List<Enrollment> findActiveEnrollmentsByUser(@Param("user") User user);

    /**
     * Find enrollments by course and role
     */
    List<Enrollment> findByCourseAndRole(Course course, EnrollmentRole role);

    /**
     * Find enrollments by course, role, and status
     */
    List<Enrollment> findByCourseAndRoleAndStatus(
        Course course,
        EnrollmentRole role,
        EnrollmentStatus status
    );

    /**
     * Count active students in a course
     */
    @Query("SELECT COUNT(e) FROM Enrollment e WHERE e.course = :course " +
           "AND e.role = 'STUDENT' AND e.status = 'ACTIVE'")
    Long countActiveStudents(@Param("course") Course course);

    /**
     * Check if user is enrolled in course
     */
    boolean existsByUserAndCourse(User user, Course course);

    /**
     * Check if user is actively enrolled in course
     */
    @Query("SELECT CASE WHEN COUNT(e) > 0 THEN true ELSE false END FROM Enrollment e " +
           "WHERE e.user = :user AND e.course = :course AND e.status = 'ACTIVE'")
    boolean isActivelyEnrolled(@Param("user") User user, @Param("course") Course course);
}
