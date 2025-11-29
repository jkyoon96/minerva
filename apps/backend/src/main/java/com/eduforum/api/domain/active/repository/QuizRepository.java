package com.eduforum.api.domain.active.repository;

import com.eduforum.api.domain.active.entity.Quiz;
import com.eduforum.api.domain.active.entity.QuizStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface QuizRepository extends JpaRepository<Quiz, Long> {

    List<Quiz> findByCourseId(Long courseId);

    List<Quiz> findByCourseIdAndStatus(Long courseId, QuizStatus status);

    List<Quiz> findByCreatorId(Long creatorId);

    @Query("SELECT q FROM Quiz q WHERE q.deletedAt IS NULL AND q.id = :id")
    Optional<Quiz> findByIdAndNotDeleted(Long id);
}
