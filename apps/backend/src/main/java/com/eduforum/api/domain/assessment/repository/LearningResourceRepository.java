package com.eduforum.api.domain.assessment.repository;

import com.eduforum.api.domain.assessment.entity.LearningResource;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface LearningResourceRepository extends JpaRepository<LearningResource, Long> {

    List<LearningResource> findByStudentId(Long studentId);

    List<LearningResource> findByStudentIdAndCourseId(Long studentId, Long courseId);

    List<LearningResource> findByStudentIdAndTopic(Long studentId, String topic);

    List<LearningResource> findByStudentIdAndIsCompleted(Long studentId, Boolean isCompleted);

    List<LearningResource> findByStudentIdAndIsBookmarked(Long studentId, Boolean isBookmarked);
}
