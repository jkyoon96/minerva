package com.eduforum.api.domain.active.service;

import com.eduforum.api.common.exception.BusinessException;
import com.eduforum.api.common.exception.ErrorCode;
import com.eduforum.api.domain.active.dto.quiz.*;
import com.eduforum.api.domain.active.entity.*;
import com.eduforum.api.domain.active.repository.*;
import com.eduforum.api.domain.auth.entity.User;
import com.eduforum.api.domain.auth.repository.UserRepository;
import com.eduforum.api.domain.course.entity.Course;
import com.eduforum.api.domain.course.repository.CourseRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.OffsetDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
@Slf4j
public class QuizService {

    private final QuizRepository quizRepository;
    private final QuizSessionRepository quizSessionRepository;
    private final QuizAnswerRepository quizAnswerRepository;
    private final QuestionRepository questionRepository;
    private final CourseRepository courseRepository;
    private final UserRepository userRepository;

    @Transactional
    public QuizResponse createQuiz(Long userId, QuizCreateRequest request) {
        log.info("Creating quiz for course {} by user {}", request.getCourseId(), userId);

        Course course = courseRepository.findById(request.getCourseId())
            .orElseThrow(() -> new BusinessException(ErrorCode.COURSE_NOT_FOUND));

        User creator = userRepository.findById(userId)
            .orElseThrow(() -> new BusinessException(ErrorCode.USER_NOT_FOUND));

        // Validate all question IDs exist
        List<Question> questions = questionRepository.findAllById(request.getQuestionIds());
        if (questions.size() != request.getQuestionIds().size()) {
            throw new BusinessException(ErrorCode.INVALID_INPUT_VALUE, "Some question IDs are invalid");
        }

        Quiz quiz = Quiz.builder()
            .course(course)
            .creator(creator)
            .title(request.getTitle())
            .description(request.getDescription())
            .questionIds(request.getQuestionIds())
            .timeLimitMinutes(request.getTimeLimitMinutes())
            .passingScore(request.getPassingScore())
            .shuffleQuestions(request.getShuffleQuestions() != null ? request.getShuffleQuestions() : false)
            .showCorrectAnswers(request.getShowCorrectAnswers() != null ? request.getShowCorrectAnswers() : true)
            .settings(request.getSettings() != null ? request.getSettings() : Map.of())
            .build();

        quiz = quizRepository.save(quiz);
        log.info("Created quiz {}", quiz.getId());
        return mapToResponse(quiz);
    }

    public QuizResponse getQuiz(Long quizId) {
        Quiz quiz = quizRepository.findByIdAndNotDeleted(quizId)
            .orElseThrow(() -> new BusinessException(ErrorCode.ENTITY_NOT_FOUND));
        return mapToResponse(quiz);
    }

    public List<QuizResponse> getQuizzesByCourse(Long courseId) {
        return quizRepository.findByCourseIdAndNotDeleted(courseId).stream()
            .map(this::mapToResponse)
            .collect(Collectors.toList());
    }

    @Transactional
    public QuizResponse updateQuiz(Long userId, Long quizId, QuizCreateRequest request) {
        Quiz quiz = quizRepository.findByIdAndNotDeleted(quizId)
            .orElseThrow(() -> new BusinessException(ErrorCode.ENTITY_NOT_FOUND));

        if (!quiz.getCreator().getId().equals(userId)) {
            throw new BusinessException(ErrorCode.ACCESS_DENIED);
        }

        // Validate all question IDs exist
        List<Question> questions = questionRepository.findAllById(request.getQuestionIds());
        if (questions.size() != request.getQuestionIds().size()) {
            throw new BusinessException(ErrorCode.INVALID_INPUT_VALUE, "Some question IDs are invalid");
        }

        quiz.setTitle(request.getTitle());
        quiz.setDescription(request.getDescription());
        quiz.setQuestionIds(request.getQuestionIds());
        quiz.setTimeLimitMinutes(request.getTimeLimitMinutes());
        quiz.setPassingScore(request.getPassingScore());
        quiz.setShuffleQuestions(request.getShuffleQuestions());
        quiz.setShowCorrectAnswers(request.getShowCorrectAnswers());
        quiz.setSettings(request.getSettings());

        quiz = quizRepository.save(quiz);
        return mapToResponse(quiz);
    }

    @Transactional
    public void deleteQuiz(Long userId, Long quizId) {
        Quiz quiz = quizRepository.findByIdAndNotDeleted(quizId)
            .orElseThrow(() -> new BusinessException(ErrorCode.ENTITY_NOT_FOUND));

        if (!quiz.getCreator().getId().equals(userId)) {
            throw new BusinessException(ErrorCode.ACCESS_DENIED);
        }

        quiz.delete();
        quizRepository.save(quiz);
    }

    @Transactional
    public QuizResponse startQuiz(Long userId, Long quizId) {
        Quiz quiz = quizRepository.findByIdAndNotDeleted(quizId)
            .orElseThrow(() -> new BusinessException(ErrorCode.ENTITY_NOT_FOUND));

        if (!quiz.getCreator().getId().equals(userId)) {
            throw new BusinessException(ErrorCode.ACCESS_DENIED);
        }

        quiz.activate();
        quiz = quizRepository.save(quiz);

        log.info("Started quiz {}", quiz.getId());
        return mapToResponse(quiz);
    }

    @Transactional
    public QuizResponse stopQuiz(Long userId, Long quizId) {
        Quiz quiz = quizRepository.findByIdAndNotDeleted(quizId)
            .orElseThrow(() -> new BusinessException(ErrorCode.ENTITY_NOT_FOUND));

        if (!quiz.getCreator().getId().equals(userId)) {
            throw new BusinessException(ErrorCode.ACCESS_DENIED);
        }

        quiz.complete();
        quiz = quizRepository.save(quiz);

        log.info("Stopped quiz {}", quiz.getId());
        return mapToResponse(quiz);
    }

    @Transactional
    public void submitQuizAnswer(Long userId, Long quizId, QuizSubmitRequest request) {
        Quiz quiz = quizRepository.findByIdAndNotDeleted(quizId)
            .orElseThrow(() -> new BusinessException(ErrorCode.ENTITY_NOT_FOUND));

        if (!quiz.isActive()) {
            throw new BusinessException(ErrorCode.INVALID_INPUT_VALUE, "Quiz is not active");
        }

        User user = userRepository.findById(userId)
            .orElseThrow(() -> new BusinessException(ErrorCode.USER_NOT_FOUND));

        // Check if user has already started a session
        QuizSession session = quizSessionRepository.findByQuizIdAndUserId(quizId, userId)
            .orElseGet(() -> {
                QuizSession newSession = QuizSession.builder()
                    .quiz(quiz)
                    .user(user)
                    .startedAt(OffsetDateTime.now())
                    .build();
                return quizSessionRepository.save(newSession);
            });

        // Check time limit
        if (quiz.getTimeLimitMinutes() != null) {
            OffsetDateTime deadline = session.getStartedAt().plusMinutes(quiz.getTimeLimitMinutes());
            if (OffsetDateTime.now().isAfter(deadline)) {
                throw new BusinessException(ErrorCode.INVALID_INPUT_VALUE, "Quiz time limit exceeded");
            }
        }

        // Save answers
        for (QuizSubmitRequest.QuestionAnswer qa : request.getAnswers()) {
            Question question = questionRepository.findById(qa.getQuestionId())
                .orElseThrow(() -> new BusinessException(ErrorCode.ENTITY_NOT_FOUND));

            // Calculate if answer is correct
            boolean isCorrect = false;
            int pointsEarned = 0;

            if (question.getCorrectAnswers() != null && !question.getCorrectAnswers().isEmpty()) {
                Set<String> correctSet = new HashSet<>(question.getCorrectAnswers());
                Set<String> answerSet = new HashSet<>(qa.getAnswers());
                isCorrect = correctSet.equals(answerSet);
                if (isCorrect) {
                    pointsEarned = question.getPoints();
                }
            }

            QuizAnswer answer = QuizAnswer.builder()
                .session(session)
                .question(question)
                .answers(qa.getAnswers())
                .isCorrect(isCorrect)
                .pointsEarned(pointsEarned)
                .build();

            quizAnswerRepository.save(answer);
        }

        // Update session
        session.setSubmittedAt(OffsetDateTime.now());
        session.calculateScore();
        quizSessionRepository.save(session);

        log.info("User {} submitted quiz {}", userId, quizId);
    }

    public QuizResultResponse getQuizResults(Long quizId, Long userId) {
        Quiz quiz = quizRepository.findByIdAndNotDeleted(quizId)
            .orElseThrow(() -> new BusinessException(ErrorCode.ENTITY_NOT_FOUND));

        QuizSession session = quizSessionRepository.findByQuizIdAndUserId(quizId, userId)
            .orElseThrow(() -> new BusinessException(ErrorCode.ENTITY_NOT_FOUND, "No quiz session found"));

        List<QuizAnswer> answers = quizAnswerRepository.findBySessionId(session.getId());

        List<QuizResultResponse.QuestionResult> questionResults = answers.stream()
            .map(answer -> {
                Question question = answer.getQuestion();
                return QuizResultResponse.QuestionResult.builder()
                    .questionId(question.getId())
                    .questionText(question.getQuestionText())
                    .userAnswers(answer.getAnswers())
                    .correctAnswers(quiz.getShowCorrectAnswers() ? question.getCorrectAnswers() : null)
                    .isCorrect(answer.getIsCorrect())
                    .pointsEarned(answer.getPointsEarned())
                    .pointsPossible(question.getPoints())
                    .explanation(quiz.getShowCorrectAnswers() ? question.getExplanation() : null)
                    .build();
            })
            .collect(Collectors.toList());

        int totalPoints = answers.stream().mapToInt(QuizAnswer::getPointsEarned).sum();
        int maxPoints = questionResults.stream().mapToInt(QuizResultResponse.QuestionResult::getPointsPossible).sum();
        double percentage = maxPoints > 0 ? (totalPoints * 100.0 / maxPoints) : 0;
        boolean passed = quiz.getPassingScore() != null && percentage >= quiz.getPassingScore();

        return QuizResultResponse.builder()
            .quizId(quiz.getId())
            .title(quiz.getTitle())
            .totalScore(totalPoints)
            .maxScore(maxPoints)
            .percentage(percentage)
            .passed(passed)
            .startedAt(session.getStartedAt())
            .submittedAt(session.getSubmittedAt())
            .questionResults(questionResults)
            .build();
    }

    private QuizResponse mapToResponse(Quiz quiz) {
        Long sessionCount = quizSessionRepository.countByQuizId(quiz.getId());

        return QuizResponse.builder()
            .id(quiz.getId())
            .courseId(quiz.getCourse().getId())
            .creatorId(quiz.getCreator().getId())
            .creatorName(quiz.getCreator().getName())
            .title(quiz.getTitle())
            .description(quiz.getDescription())
            .status(quiz.getStatus())
            .questionIds(quiz.getQuestionIds())
            .timeLimitMinutes(quiz.getTimeLimitMinutes())
            .passingScore(quiz.getPassingScore())
            .shuffleQuestions(quiz.getShuffleQuestions())
            .showCorrectAnswers(quiz.getShowCorrectAnswers())
            .sessionCount(sessionCount)
            .settings(quiz.getSettings())
            .createdAt(quiz.getCreatedAt())
            .updatedAt(quiz.getUpdatedAt())
            .build();
    }
}
