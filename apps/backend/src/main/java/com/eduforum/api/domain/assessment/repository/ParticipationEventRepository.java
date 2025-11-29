package com.eduforum.api.domain.assessment.repository;

import com.eduforum.api.domain.assessment.entity.EventType;
import com.eduforum.api.domain.assessment.entity.ParticipationEvent;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.OffsetDateTime;
import java.util.List;

@Repository
public interface ParticipationEventRepository extends JpaRepository<ParticipationEvent, Long> {

    List<ParticipationEvent> findByStudentIdAndCourseId(Long studentId, Long courseId);

    List<ParticipationEvent> findByCourseId(Long courseId);

    List<ParticipationEvent> findByStudentIdAndCourseIdAndEventType(Long studentId, Long courseId, EventType eventType);

    List<ParticipationEvent> findByStudentIdAndCourseIdAndIsCounted(Long studentId, Long courseId, Boolean isCounted);

    @Query("SELECT p FROM ParticipationEvent p WHERE p.studentId = :studentId AND p.courseId = :courseId AND p.eventTime BETWEEN :startTime AND :endTime")
    List<ParticipationEvent> findByStudentIdAndCourseIdAndEventTimeBetween(
        @Param("studentId") Long studentId,
        @Param("courseId") Long courseId,
        @Param("startTime") OffsetDateTime startTime,
        @Param("endTime") OffsetDateTime endTime
    );
}
