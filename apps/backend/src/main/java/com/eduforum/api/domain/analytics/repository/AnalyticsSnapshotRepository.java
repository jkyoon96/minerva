package com.eduforum.api.domain.analytics.repository;

import com.eduforum.api.domain.analytics.entity.AnalyticsSnapshot;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface AnalyticsSnapshotRepository extends JpaRepository<AnalyticsSnapshot, Long> {

    List<AnalyticsSnapshot> findBySessionIdOrderBySnapshotTimeDesc(Long sessionId);

    List<AnalyticsSnapshot> findByCourseIdOrderBySnapshotTimeDesc(Long courseId);

    Optional<AnalyticsSnapshot> findTopBySessionIdOrderBySnapshotTimeDesc(Long sessionId);

    @Query("SELECT a FROM AnalyticsSnapshot a WHERE a.courseId = :courseId " +
           "AND a.snapshotTime BETWEEN :start AND :end ORDER BY a.snapshotTime DESC")
    List<AnalyticsSnapshot> findByCourseIdAndTimeRange(
        @Param("courseId") Long courseId,
        @Param("start") OffsetDateTime start,
        @Param("end") OffsetDateTime end
    );

    @Query("SELECT AVG(a.avgEngagementScore) FROM AnalyticsSnapshot a " +
           "WHERE a.courseId = :courseId AND a.snapshotTime >= :since")
    Double findAvgEngagementByCourseIdSince(
        @Param("courseId") Long courseId,
        @Param("since") OffsetDateTime since
    );
}
