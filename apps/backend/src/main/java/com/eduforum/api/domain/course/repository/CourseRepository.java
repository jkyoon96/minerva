package com.eduforum.api.domain.course.repository;

import com.eduforum.api.domain.auth.entity.User;
import com.eduforum.api.domain.course.entity.Course;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Repository for Course entity
 */
@Repository
public interface CourseRepository extends JpaRepository<Course, Long> {

    /**
     * Find course by invite code
     */
    Optional<Course> findByInviteCode(String inviteCode);

    /**
     * Find courses by professor
     */
    List<Course> findByProfessor(User professor);

    /**
     * Find active courses by professor (excluding soft-deleted)
     */
    @Query("SELECT c FROM Course c WHERE c.professor = :professor AND c.deletedAt IS NULL")
    List<Course> findActiveCoursesByProfessor(@Param("professor") User professor);

    /**
     * Find courses by semester and year
     */
    List<Course> findBySemesterAndYear(String semester, Integer year);

    /**
     * Find active courses by semester and year (excluding soft-deleted)
     */
    @Query("SELECT c FROM Course c WHERE c.semester = :semester AND c.year = :year AND c.deletedAt IS NULL")
    List<Course> findActiveCoursesBySemesterAndYear(
        @Param("semester") String semester,
        @Param("year") Integer year
    );

    /**
     * Find published courses
     */
    @Query("SELECT c FROM Course c WHERE c.isPublished = true AND c.deletedAt IS NULL")
    List<Course> findPublishedCourses();

    /**
     * Find courses by code
     */
    List<Course> findByCode(String code);

    /**
     * Find all active courses (not soft-deleted)
     */
    @Query("SELECT c FROM Course c WHERE c.deletedAt IS NULL")
    List<Course> findAllActive();

    /**
     * Check if course code exists for semester and year
     */
    boolean existsByCodeAndSemesterAndYear(String code, String semester, Integer year);
}
