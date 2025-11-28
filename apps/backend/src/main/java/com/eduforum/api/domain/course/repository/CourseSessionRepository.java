package com.eduforum.api.domain.course.repository;

import com.eduforum.api.domain.course.entity.Course;
import com.eduforum.api.domain.course.entity.CourseSession;
import com.eduforum.api.domain.course.entity.SessionStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.OffsetDateTime;
import java.util.List;

/**
 * Repository for CourseSession entity
 */
@Repository
public interface CourseSessionRepository extends JpaRepository<CourseSession, Long> {

    /**
     * Find sessions by course
     */
    List<CourseSession> findByCourse(Course course);

    /**
     * Find sessions by course ordered by scheduled date
     */
    List<CourseSession> findByCourseOrderByScheduledAtDesc(Course course);

    /**
     * Find sessions by status
     */
    List<CourseSession> findByStatus(SessionStatus status);

    /**
     * Find sessions by course and status
     */
    List<CourseSession> findByCourseAndStatus(Course course, SessionStatus status);

    /**
     * Find upcoming sessions for a course
     */
    @Query("SELECT s FROM CourseSession s WHERE s.course = :course " +
           "AND s.scheduledAt > :now AND s.status = 'SCHEDULED' " +
           "ORDER BY s.scheduledAt ASC")
    List<CourseSession> findUpcomingSessions(
        @Param("course") Course course,
        @Param("now") OffsetDateTime now
    );

    /**
     * Find live sessions
     */
    @Query("SELECT s FROM CourseSession s WHERE s.status = 'LIVE'")
    List<CourseSession> findLiveSessions();

    /**
     * Find sessions scheduled between dates
     */
    @Query("SELECT s FROM CourseSession s WHERE s.scheduledAt BETWEEN :start AND :end " +
           "ORDER BY s.scheduledAt ASC")
    List<CourseSession> findSessionsBetween(
        @Param("start") OffsetDateTime start,
        @Param("end") OffsetDateTime end
    );
}
