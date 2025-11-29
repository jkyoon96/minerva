package com.eduforum.api.domain.assessment.repository;

import com.eduforum.api.domain.assessment.entity.EventType;
import com.eduforum.api.domain.assessment.entity.ParticipationWeight;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ParticipationWeightRepository extends JpaRepository<ParticipationWeight, Long> {

    List<ParticipationWeight> findByCourseId(Long courseId);

    Optional<ParticipationWeight> findByCourseIdAndEventType(Long courseId, EventType eventType);

    List<ParticipationWeight> findByCourseIdAndIsEnabled(Long courseId, Boolean isEnabled);
}
