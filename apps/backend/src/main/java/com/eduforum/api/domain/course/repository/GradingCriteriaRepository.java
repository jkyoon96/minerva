package com.eduforum.api.domain.course.repository;

import com.eduforum.api.domain.course.entity.Course;
import com.eduforum.api.domain.course.entity.GradingCriteria;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Repository for grading criteria
 */
@Repository
public interface GradingCriteriaRepository extends JpaRepository<GradingCriteria, Long> {

    /**
     * Find all grading criteria by course (not deleted)
     */
    @Query("SELECT gc FROM GradingCriteria gc WHERE gc.course = :course " +
           "AND gc.deletedAt IS NULL ORDER BY gc.orderIndex ASC")
    List<GradingCriteria> findByCourseOrderByOrderIndex(@Param("course") Course course);

    /**
     * Find grading criteria by course ID
     */
    @Query("SELECT gc FROM GradingCriteria gc WHERE gc.course.id = :courseId " +
           "AND gc.deletedAt IS NULL ORDER BY gc.orderIndex ASC")
    List<GradingCriteria> findByCourseIdOrderByOrderIndex(@Param("courseId") Long courseId);

    /**
     * Count grading criteria for a course
     */
    @Query("SELECT COUNT(gc) FROM GradingCriteria gc WHERE gc.course.id = :courseId " +
           "AND gc.deletedAt IS NULL")
    Long countByCourseId(@Param("courseId") Long courseId);
}
