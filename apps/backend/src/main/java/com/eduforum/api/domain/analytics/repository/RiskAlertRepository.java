package com.eduforum.api.domain.analytics.repository;

import com.eduforum.api.domain.analytics.entity.AlertStatus;
import com.eduforum.api.domain.analytics.entity.RiskAlert;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface RiskAlertRepository extends JpaRepository<RiskAlert, Long> {

    List<RiskAlert> findByStudentIdOrderByCreatedAtDesc(Long studentId);

    List<RiskAlert> findByCourseIdOrderByCreatedAtDesc(Long courseId);

    List<RiskAlert> findByCourseIdAndStatus(Long courseId, AlertStatus status);

    List<RiskAlert> findByInstructorIdAndStatus(Long instructorId, AlertStatus status);

    @Query("SELECT a FROM RiskAlert a WHERE a.courseId = :courseId " +
           "AND a.status IN ('PENDING', 'SENT') ORDER BY a.createdAt DESC")
    List<RiskAlert> findPendingAlerts(@Param("courseId") Long courseId);

    @Query("SELECT COUNT(a) FROM RiskAlert a WHERE a.courseId = :courseId " +
           "AND a.status = :status")
    Long countByStatusAndCourse(
        @Param("courseId") Long courseId,
        @Param("status") AlertStatus status
    );
}
