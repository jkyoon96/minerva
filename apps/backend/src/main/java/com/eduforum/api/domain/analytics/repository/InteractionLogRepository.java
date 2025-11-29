package com.eduforum.api.domain.analytics.repository;

import com.eduforum.api.domain.analytics.entity.InteractionLog;
import com.eduforum.api.domain.analytics.entity.InteractionType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.OffsetDateTime;
import java.util.List;

@Repository
public interface InteractionLogRepository extends JpaRepository<InteractionLog, Long> {

    List<InteractionLog> findByCourseIdOrderByInteractionTimeDesc(Long courseId);

    List<InteractionLog> findBySessionIdOrderByInteractionTimeDesc(Long sessionId);

    @Query("SELECT i FROM InteractionLog i WHERE i.courseId = :courseId " +
           "AND i.interactionTime >= :since ORDER BY i.interactionTime DESC")
    List<InteractionLog> findByCourseIdSince(
        @Param("courseId") Long courseId,
        @Param("since") OffsetDateTime since
    );

    @Query("SELECT i FROM InteractionLog i WHERE i.courseId = :courseId " +
           "AND (i.fromStudentId = :studentId OR i.toStudentId = :studentId)")
    List<InteractionLog> findByStudentId(
        @Param("courseId") Long courseId,
        @Param("studentId") Long studentId
    );

    @Query("SELECT COUNT(i) FROM InteractionLog i WHERE i.courseId = :courseId " +
           "AND i.fromStudentId = :studentId AND i.interactionTime >= :since")
    Long countStudentInteractionsSince(
        @Param("courseId") Long courseId,
        @Param("studentId") Long studentId,
        @Param("since") OffsetDateTime since
    );

    @Query("SELECT i.interactionType, COUNT(i) FROM InteractionLog i " +
           "WHERE i.courseId = :courseId GROUP BY i.interactionType")
    List<Object[]> countByInteractionType(@Param("courseId") Long courseId);
}
