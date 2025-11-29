package com.eduforum.api.domain.analytics.repository;

import com.eduforum.api.domain.analytics.entity.RiskIndicator;
import com.eduforum.api.domain.analytics.entity.RiskLevel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface RiskIndicatorRepository extends JpaRepository<RiskIndicator, Long> {

    Optional<RiskIndicator> findByStudentIdAndCourseId(Long studentId, Long courseId);

    List<RiskIndicator> findByCourseId(Long courseId);

    List<RiskIndicator> findByCourseIdAndRiskLevel(Long courseId, RiskLevel riskLevel);

    @Query("SELECT r FROM RiskIndicator r WHERE r.courseId = :courseId " +
           "AND r.riskLevel IN ('HIGH', 'CRITICAL') ORDER BY r.riskScore DESC")
    List<RiskIndicator> findAtRiskStudents(@Param("courseId") Long courseId);

    @Query("SELECT COUNT(r) FROM RiskIndicator r WHERE r.courseId = :courseId " +
           "AND r.riskLevel IN ('HIGH', 'CRITICAL')")
    Long countAtRiskStudents(@Param("courseId") Long courseId);

    @Query("SELECT r FROM RiskIndicator r WHERE r.courseId = :courseId " +
           "AND r.daysInactive >= :days ORDER BY r.daysInactive DESC")
    List<RiskIndicator> findInactiveStudents(
        @Param("courseId") Long courseId,
        @Param("days") Integer days
    );
}
