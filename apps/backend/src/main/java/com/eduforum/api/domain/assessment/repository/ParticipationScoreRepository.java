package com.eduforum.api.domain.assessment.repository;

import com.eduforum.api.domain.assessment.entity.ParticipationScore;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ParticipationScoreRepository extends JpaRepository<ParticipationScore, Long> {

    Optional<ParticipationScore> findByStudentIdAndCourseId(Long studentId, Long courseId);

    List<ParticipationScore> findByCourseId(Long courseId);

    List<ParticipationScore> findByStudentId(Long studentId);
}
