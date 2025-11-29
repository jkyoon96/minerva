package com.eduforum.api.domain.active.repository;

import com.eduforum.api.domain.active.entity.QuizSession;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface QuizSessionRepository extends JpaRepository<QuizSession, Long> {

    Optional<QuizSession> findByQuizIdAndUserId(Long quizId, Long userId);

    Long countByQuizId(Long quizId);

    List<QuizSession> findByQuizId(Long quizId);

    @Query("SELECT qs FROM QuizSession qs WHERE qs.quiz.id = :quizId AND (qs.endsAt IS NULL OR qs.endsAt > CURRENT_TIMESTAMP)")
    List<QuizSession> findActiveSessionsByQuizId(Long quizId);
}
