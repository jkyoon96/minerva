package com.eduforum.api.domain.active.repository;

import com.eduforum.api.domain.active.entity.QuizAnswer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface QuizAnswerRepository extends JpaRepository<QuizAnswer, Long> {

    List<QuizAnswer> findBySessionId(Long sessionId);

    Optional<QuizAnswer> findBySessionIdAndUserId(Long sessionId, Long userId);

    @Query("SELECT COUNT(qa) FROM QuizAnswer qa WHERE qa.session.id = :sessionId")
    Long countBySessionId(Long sessionId);

    boolean existsBySessionIdAndUserId(Long sessionId, Long userId);
}
