package com.eduforum.api.domain.analytics.repository;

import com.eduforum.api.domain.analytics.entity.CourseReport;
import com.eduforum.api.domain.analytics.entity.ReportPeriod;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CourseReportRepository extends JpaRepository<CourseReport, Long> {

    List<CourseReport> findByCourseIdOrderByPeriodStartDesc(Long courseId);

    Optional<CourseReport> findByCourseIdAndPeriod(Long courseId, ReportPeriod period);

    Optional<CourseReport> findTopByCourseIdOrderByGeneratedAtDesc(Long courseId);
}
