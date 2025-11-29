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
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
@Slf4j
public class QuestionBankService {

    private final QuestionRepository questionRepository;
    private final QuestionTagRepository questionTagRepository;
    private final CourseRepository courseRepository;
    private final UserRepository userRepository;
    private final ObjectMapper objectMapper;

    @Transactional
    public QuestionResponse createQuestion(Long userId, QuestionCreateRequest request) {
        log.info("Creating question for course {} by user {}", request.getCourseId(), userId);

        Course course = courseRepository.findById(request.getCourseId())
            .orElseThrow(() -> new BusinessException(ErrorCode.COURSE_NOT_FOUND));

        User creator = userRepository.findById(userId)
            .orElseThrow(() -> new BusinessException(ErrorCode.USER_NOT_FOUND));

        Question question = Question.builder()
            .course(course)
            .creator(creator)
            .type(request.getType())
            .questionText(request.getQuestionText())
            .options(request.getOptions() != null ? request.getOptions() : List.of())
            .correctAnswers(request.getCorrectAnswers() != null ? request.getCorrectAnswers() : List.of())
            .explanation(request.getExplanation())
            .points(request.getPoints() != null ? request.getPoints() : 1)
            .timeLimitSeconds(request.getTimeLimitSeconds())
            .metadata(request.getMetadata() != null ? request.getMetadata() : java.util.Map.of())
            .build();

        // Add tags if provided
        if (request.getTagIds() != null && !request.getTagIds().isEmpty()) {
            List<QuestionTag> tags = questionTagRepository.findAllById(request.getTagIds());
            question.setTags(tags);
        }

        question = questionRepository.save(question);
        log.info("Created question {}", question.getId());
        return mapToResponse(question);
    }

    public QuestionResponse getQuestion(Long questionId) {
        Question question = questionRepository.findByIdAndNotDeleted(questionId)
            .orElseThrow(() -> new BusinessException(ErrorCode.ENTITY_NOT_FOUND));
        return mapToResponse(question);
    }

    public List<QuestionResponse> getQuestionsByCourse(Long courseId, QuestionType type, List<Long> tagIds) {
        List<Question> questions;

        if (type != null && tagIds != null && !tagIds.isEmpty()) {
            questions = questionRepository.findByCourseIdAndTypeAndTagsIn(courseId, type, tagIds);
        } else if (type != null) {
            questions = questionRepository.findByCourseIdAndType(courseId, type);
        } else if (tagIds != null && !tagIds.isEmpty()) {
            questions = questionRepository.findByCourseIdAndTagsIn(courseId, tagIds);
        } else {
            questions = questionRepository.findByCourseIdAndNotDeleted(courseId);
        }

        return questions.stream()
            .map(this::mapToResponse)
            .collect(Collectors.toList());
    }

    @Transactional
    public QuestionResponse updateQuestion(Long userId, Long questionId, QuestionCreateRequest request) {
        Question question = questionRepository.findByIdAndNotDeleted(questionId)
            .orElseThrow(() -> new BusinessException(ErrorCode.ENTITY_NOT_FOUND));

        if (!question.getCreator().getId().equals(userId)) {
            throw new BusinessException(ErrorCode.ACCESS_DENIED);
        }

        question.setType(request.getType());
        question.setQuestionText(request.getQuestionText());
        question.setOptions(request.getOptions() != null ? request.getOptions() : List.of());
        question.setCorrectAnswers(request.getCorrectAnswers() != null ? request.getCorrectAnswers() : List.of());
        question.setExplanation(request.getExplanation());
        question.setPoints(request.getPoints() != null ? request.getPoints() : 1);
        question.setTimeLimitSeconds(request.getTimeLimitSeconds());
        question.setMetadata(request.getMetadata() != null ? request.getMetadata() : java.util.Map.of());

        // Update tags if provided
        if (request.getTagIds() != null) {
            List<QuestionTag> tags = questionTagRepository.findAllById(request.getTagIds());
            question.setTags(tags);
        }

        question = questionRepository.save(question);
        return mapToResponse(question);
    }

    @Transactional
    public void deleteQuestion(Long userId, Long questionId) {
        Question question = questionRepository.findByIdAndNotDeleted(questionId)
            .orElseThrow(() -> new BusinessException(ErrorCode.ENTITY_NOT_FOUND));

        if (!question.getCreator().getId().equals(userId)) {
            throw new BusinessException(ErrorCode.ACCESS_DENIED);
        }

        question.delete();
        questionRepository.save(question);
    }

    public List<QuestionTagResponse> getAllTags(Long courseId) {
        return questionTagRepository.findByCourseId(courseId).stream()
            .map(tag -> QuestionTagResponse.builder()
                .id(tag.getId())
                .name(tag.getName())
                .color(tag.getColor())
                .build())
            .collect(Collectors.toList());
    }

    @Transactional
    public List<QuestionResponse> importQuestions(Long userId, Long courseId, String jsonData) {
        log.info("Importing questions for course {} by user {}", courseId, userId);

        Course course = courseRepository.findById(courseId)
            .orElseThrow(() -> new BusinessException(ErrorCode.COURSE_NOT_FOUND));

        User creator = userRepository.findById(userId)
            .orElseThrow(() -> new BusinessException(ErrorCode.USER_NOT_FOUND));

        try {
            List<QuestionCreateRequest> requests = objectMapper.readValue(
                jsonData,
                new TypeReference<List<QuestionCreateRequest>>() {}
            );

            List<Question> questions = requests.stream()
                .map(req -> {
                    Question q = Question.builder()
                        .course(course)
                        .creator(creator)
                        .type(req.getType())
                        .questionText(req.getQuestionText())
                        .options(req.getOptions() != null ? req.getOptions() : List.of())
                        .correctAnswers(req.getCorrectAnswers() != null ? req.getCorrectAnswers() : List.of())
                        .explanation(req.getExplanation())
                        .points(req.getPoints() != null ? req.getPoints() : 1)
                        .timeLimitSeconds(req.getTimeLimitSeconds())
                        .metadata(req.getMetadata() != null ? req.getMetadata() : java.util.Map.of())
                        .build();

                    if (req.getTagIds() != null && !req.getTagIds().isEmpty()) {
                        List<QuestionTag> tags = questionTagRepository.findAllById(req.getTagIds());
                        q.setTags(tags);
                    }

                    return q;
                })
                .collect(Collectors.toList());

            questions = questionRepository.saveAll(questions);
            log.info("Imported {} questions", questions.size());

            return questions.stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());

        } catch (IOException e) {
            log.error("Failed to import questions", e);
            throw new BusinessException(ErrorCode.INVALID_INPUT_VALUE, "Invalid JSON format");
        }
    }

    public String exportQuestions(Long courseId) {
        List<Question> questions = questionRepository.findByCourseIdAndNotDeleted(courseId);

        List<QuestionCreateRequest> exportData = questions.stream()
            .map(q -> QuestionCreateRequest.builder()
                .courseId(courseId)
                .type(q.getType())
                .questionText(q.getQuestionText())
                .options(q.getOptions())
                .correctAnswers(q.getCorrectAnswers())
                .explanation(q.getExplanation())
                .points(q.getPoints())
                .timeLimitSeconds(q.getTimeLimitSeconds())
                .tagIds(q.getTags().stream().map(QuestionTag::getId).collect(Collectors.toList()))
                .metadata(q.getMetadata())
                .build())
            .collect(Collectors.toList());

        try {
            return objectMapper.writeValueAsString(exportData);
        } catch (IOException e) {
            log.error("Failed to export questions", e);
            throw new BusinessException(ErrorCode.INTERNAL_SERVER_ERROR, "Failed to export questions");
        }
    }

    private QuestionResponse mapToResponse(Question question) {
        return QuestionResponse.builder()
            .id(question.getId())
            .courseId(question.getCourse().getId())
            .creatorId(question.getCreator().getId())
            .creatorName(question.getCreator().getName())
            .type(question.getType())
            .questionText(question.getQuestionText())
            .options(question.getOptions())
            .correctAnswers(question.getCorrectAnswers())
            .explanation(question.getExplanation())
            .points(question.getPoints())
            .timeLimitSeconds(question.getTimeLimitSeconds())
            .tags(question.getTags().stream()
                .map(tag -> QuestionTagResponse.builder()
                    .id(tag.getId())
                    .name(tag.getName())
                    .color(tag.getColor())
                    .build())
                .collect(Collectors.toList()))
            .metadata(question.getMetadata())
            .createdAt(question.getCreatedAt())
            .updatedAt(question.getUpdatedAt())
            .build();
    }
}
