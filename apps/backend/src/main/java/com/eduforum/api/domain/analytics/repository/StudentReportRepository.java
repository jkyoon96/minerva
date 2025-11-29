package com.eduforum.api.domain.analytics.repository;

import com.eduforum.api.domain.analytics.entity.ReportPeriod;
import com.eduforum.api.domain.analytics.entity.StudentReport;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface StudentReportRepository extends JpaRepository<StudentReport, Long> {

    List<StudentReport> findByStudentIdAndCourseIdOrderByPeriodStartDesc(Long studentId, Long courseId);

    List<StudentReport> findByCourseIdOrderByGeneratedAtDesc(Long courseId);

    Optional<StudentReport> findByStudentIdAndCourseIdAndPeriod(
        Long studentId, Long courseId, ReportPeriod period
    );

    @Query("SELECT r FROM StudentReport r WHERE r.courseId = :courseId " +
           "AND r.periodStart >= :start AND r.periodEnd <= :end")
    List<StudentReport> findByCourseAndPeriodRange(
        @Param("courseId") Long courseId,
        @Param("start") OffsetDateTime start,
        @Param("end") OffsetDateTime end
    );

    @Query("SELECT r FROM StudentReport r WHERE r.courseId = :courseId " +
           "AND r.performanceScore < :threshold ORDER BY r.performanceScore ASC")
    List<StudentReport> findLowPerformingStudents(
        @Param("courseId") Long courseId,
        @Param("threshold") java.math.BigDecimal threshold
    );
}
