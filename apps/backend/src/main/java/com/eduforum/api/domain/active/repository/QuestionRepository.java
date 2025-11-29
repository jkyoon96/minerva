package com.eduforum.api.domain.active.repository;

import com.eduforum.api.domain.active.entity.Question;
import com.eduforum.api.domain.active.entity.QuestionType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface QuestionRepository extends JpaRepository<Question, Long> {

    List<Question> findByCourseId(Long courseId);

    List<Question> findByCourseIdAndType(Long courseId, QuestionType type);

    @Query("SELECT q FROM Question q JOIN q.tags t WHERE q.course.id = :courseId AND t.id IN :tagIds AND q.deletedAt IS NULL")
    List<Question> findByCourseIdAndTagIds(@Param("courseId") Long courseId, @Param("tagIds") List<Long> tagIds);

    @Query("SELECT q FROM Question q WHERE q.deletedAt IS NULL AND q.id = :id")
    Optional<Question> findByIdAndNotDeleted(Long id);

    @Query("SELECT q FROM Question q WHERE q.id IN :ids AND q.deletedAt IS NULL")
    List<Question> findByIdInAndNotDeleted(List<Long> ids);
}
