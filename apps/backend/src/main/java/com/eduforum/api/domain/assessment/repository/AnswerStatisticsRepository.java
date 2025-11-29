package com.eduforum.api.domain.assessment.repository;

import com.eduforum.api.domain.assessment.entity.AnswerStatistics;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface AnswerStatisticsRepository extends JpaRepository<AnswerStatistics, Long> {

    Optional<AnswerStatistics> findByQuizIdAndQuestionId(Long quizId, Long questionId);

    List<AnswerStatistics> findByQuizId(Long quizId);

    List<AnswerStatistics> findByQuestionId(Long questionId);
}
