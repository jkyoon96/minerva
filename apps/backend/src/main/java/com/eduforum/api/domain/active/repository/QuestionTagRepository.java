package com.eduforum.api.domain.active.repository;

import com.eduforum.api.domain.active.entity.QuestionTag;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface QuestionTagRepository extends JpaRepository<QuestionTag, Long> {

    List<QuestionTag> findByCourseId(Long courseId);

    Optional<QuestionTag> findByCourseIdAndName(Long courseId, String name);

    boolean existsByCourseIdAndName(Long courseId, String name);
}
