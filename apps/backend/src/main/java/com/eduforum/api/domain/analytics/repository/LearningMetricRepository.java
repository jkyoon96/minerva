package com.eduforum.api.domain.analytics.repository;

import com.eduforum.api.domain.analytics.entity.LearningMetric;
import com.eduforum.api.domain.analytics.entity.MetricType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface LearningMetricRepository extends JpaRepository<LearningMetric, Long> {

    List<LearningMetric> findByStudentIdAndCourseId(Long studentId, Long courseId);

    List<LearningMetric> findByCourseId(Long courseId);

    Optional<LearningMetric> findByStudentIdAndCourseIdAndMetricType(
        Long studentId, Long courseId, MetricType metricType
    );

    @Query("SELECT m FROM LearningMetric m WHERE m.studentId = :studentId " +
           "AND m.courseId = :courseId AND m.periodStart >= :start AND m.periodEnd <= :end")
    List<LearningMetric> findByStudentAndPeriod(
        @Param("studentId") Long studentId,
        @Param("courseId") Long courseId,
        @Param("start") OffsetDateTime start,
        @Param("end") OffsetDateTime end
    );

    @Query("SELECT AVG(m.metricValue) FROM LearningMetric m " +
           "WHERE m.courseId = :courseId AND m.metricType = :type")
    Double findAvgMetricValueByCourseAndType(
        @Param("courseId") Long courseId,
        @Param("type") MetricType type
    );
}
