package com.eduforum.api.domain.active.service;

import com.eduforum.api.common.exception.BusinessException;
import com.eduforum.api.common.exception.ErrorCode;
import com.eduforum.api.domain.active.dto.poll.*;
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

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
@Slf4j
public class PollService {

    private final PollRepository pollRepository;
    private final PollOptionRepository pollOptionRepository;
    private final PollResponseRepository pollResponseRepository;
    private final CourseRepository courseRepository;
    private final UserRepository userRepository;

    @Transactional
    public PollResponse createPoll(Long userId, PollCreateRequest request) {
        log.info("Creating poll for course {} by user {}", request.getCourseId(), userId);

        Course course = courseRepository.findById(request.getCourseId())
            .orElseThrow(() -> new BusinessException(ErrorCode.COURSE_NOT_FOUND));

        User creator = userRepository.findById(userId)
            .orElseThrow(() -> new BusinessException(ErrorCode.USER_NOT_FOUND));

        Poll poll = Poll.builder()
            .course(course)
            .creator(creator)
            .question(request.getQuestion())
            .type(request.getType())
            .allowMultiple(request.getAllowMultiple() != null ? request.getAllowMultiple() : false)
            .showResults(request.getShowResults() != null ? request.getShowResults() : true)
            .anonymous(request.getAnonymous() != null ? request.getAnonymous() : false)
            .settings(request.getSettings() != null ? request.getSettings() : Map.of())
            .build();

        poll = pollRepository.save(poll);

        // Create options
        if (request.getOptions() != null) {
            for (int i = 0; i < request.getOptions().size(); i++) {
                PollOptionRequest optReq = request.getOptions().get(i);
                PollOption option = PollOption.builder()
                    .poll(poll)
                    .text(optReq.getText())
                    .order(optReq.getOrder() != null ? optReq.getOrder() : i)
                    .isCorrect(optReq.getIsCorrect())
                    .build();
                poll.addOption(option);
            }
            pollRepository.save(poll);
        }

        log.info("Created poll {}", poll.getId());
        return mapToResponse(poll);
    }

    public PollResponse getPoll(Long pollId) {
        Poll poll = pollRepository.findByIdAndNotDeleted(pollId)
            .orElseThrow(() -> new BusinessException(ErrorCode.ENTITY_NOT_FOUND));
        return mapToResponse(poll);
    }

    public List<PollResponse> getPollsByCourse(Long courseId) {
        return pollRepository.findActiveByCourseId(courseId).stream()
            .map(this::mapToResponse)
            .collect(Collectors.toList());
    }

    @Transactional
    public PollResponse updatePoll(Long userId, Long pollId, PollCreateRequest request) {
        Poll poll = pollRepository.findByIdAndNotDeleted(pollId)
            .orElseThrow(() -> new BusinessException(ErrorCode.ENTITY_NOT_FOUND));

        if (!poll.getCreator().getId().equals(userId)) {
            throw new BusinessException(ErrorCode.ACCESS_DENIED);
        }

        poll.setQuestion(request.getQuestion());
        poll.setType(request.getType());
        poll.setAllowMultiple(request.getAllowMultiple());
        poll.setShowResults(request.getShowResults());
        poll.setAnonymous(request.getAnonymous());
        poll.setSettings(request.getSettings());

        poll = pollRepository.save(poll);
        return mapToResponse(poll);
    }

    @Transactional
    public void deletePoll(Long userId, Long pollId) {
        Poll poll = pollRepository.findByIdAndNotDeleted(pollId)
            .orElseThrow(() -> new BusinessException(ErrorCode.ENTITY_NOT_FOUND));

        if (!poll.getCreator().getId().equals(userId)) {
            throw new BusinessException(ErrorCode.ACCESS_DENIED);
        }

        poll.delete();
        pollRepository.save(poll);
    }

    @Transactional
    public PollResponse activatePoll(Long userId, Long pollId) {
        Poll poll = pollRepository.findByIdAndNotDeleted(pollId)
            .orElseThrow(() -> new BusinessException(ErrorCode.ENTITY_NOT_FOUND));

        if (!poll.getCreator().getId().equals(userId)) {
            throw new BusinessException(ErrorCode.ACCESS_DENIED);
        }

        poll.activate();
        poll = pollRepository.save(poll);
        return mapToResponse(poll);
    }

    @Transactional
    public PollResponse closePoll(Long userId, Long pollId) {
        Poll poll = pollRepository.findByIdAndNotDeleted(pollId)
            .orElseThrow(() -> new BusinessException(ErrorCode.ENTITY_NOT_FOUND));

        if (!poll.getCreator().getId().equals(userId)) {
            throw new BusinessException(ErrorCode.ACCESS_DENIED);
        }

        poll.close();
        poll = pollRepository.save(poll);
        return mapToResponse(poll);
    }

    @Transactional
    public void submitResponse(Long userId, Long pollId, PollSubmitRequest request) {
        Poll poll = pollRepository.findByIdAndNotDeleted(pollId)
            .orElseThrow(() -> new BusinessException(ErrorCode.ENTITY_NOT_FOUND));

        if (!poll.isActive()) {
            throw new BusinessException(ErrorCode.INVALID_INPUT_VALUE, "Poll is not active");
        }

        User user = userRepository.findById(userId)
            .orElseThrow(() -> new BusinessException(ErrorCode.USER_NOT_FOUND));

        // Check if already responded
        if (pollResponseRepository.existsByPollIdAndUserId(pollId, userId)) {
            throw new BusinessException(ErrorCode.INVALID_INPUT_VALUE, "Already responded to this poll");
        }

        PollResponse response = PollResponse.builder()
            .poll(poll)
            .user(user)
            .selectedOptionIds(request.getSelectedOptionIds())
            .textResponse(request.getTextResponse())
            .build();

        pollResponseRepository.save(response);
    }

    public PollResultsResponse getResults(Long pollId) {
        Poll poll = pollRepository.findByIdAndNotDeleted(pollId)
            .orElseThrow(() -> new BusinessException(ErrorCode.ENTITY_NOT_FOUND));

        List<PollResponse> responses = pollResponseRepository.findByPollId(pollId);
        Long totalResponses = (long) responses.size();

        List<PollResultsResponse.OptionResult> optionResults = poll.getOptions().stream()
            .map(option -> {
                long count = responses.stream()
                    .filter(r -> r.getSelectedOptionIds() != null && r.getSelectedOptionIds().contains(option.getId()))
                    .count();
                double percentage = totalResponses > 0 ? (count * 100.0 / totalResponses) : 0;

                return PollResultsResponse.OptionResult.builder()
                    .optionId(option.getId())
                    .text(option.getText())
                    .count(count)
                    .percentage(percentage)
                    .build();
            })
            .collect(Collectors.toList());

        List<String> textResponses = responses.stream()
            .filter(r -> r.getTextResponse() != null)
            .map(PollResponse::getTextResponse)
            .collect(Collectors.toList());

        return PollResultsResponse.builder()
            .pollId(poll.getId())
            .question(poll.getQuestion())
            .totalResponses(totalResponses)
            .optionResults(optionResults)
            .textResponses(textResponses)
            .build();
    }

    private PollResponse mapToResponse(Poll poll) {
        Long responseCount = pollResponseRepository.countByPollId(poll.getId());

        List<PollOptionResponse> options = poll.getOptions().stream()
            .map(opt -> {
                Long optResponseCount = pollResponseRepository.findByPollId(poll.getId()).stream()
                    .filter(r -> r.getSelectedOptionIds() != null && r.getSelectedOptionIds().contains(opt.getId()))
                    .count();

                return PollOptionResponse.builder()
                    .id(opt.getId())
                    .text(opt.getText())
                    .order(opt.getOrder())
                    .isCorrect(opt.getIsCorrect())
                    .responseCount(optResponseCount)
                    .build();
            })
            .collect(Collectors.toList());

        return PollResponse.builder()
            .id(poll.getId())
            .courseId(poll.getCourse().getId())
            .creatorId(poll.getCreator().getId())
            .creatorName(poll.getCreator().getName())
            .question(poll.getQuestion())
            .type(poll.getType())
            .status(poll.getStatus())
            .allowMultiple(poll.getAllowMultiple())
            .showResults(poll.getShowResults())
            .anonymous(poll.getAnonymous())
            .options(options)
            .responseCount(responseCount)
            .settings(poll.getSettings())
            .createdAt(poll.getCreatedAt())
            .updatedAt(poll.getUpdatedAt())
            .build();
    }
}
