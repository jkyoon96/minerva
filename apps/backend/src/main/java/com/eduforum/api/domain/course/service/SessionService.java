package com.eduforum.api.domain.course.service;

import com.eduforum.api.common.exception.BusinessException;
import com.eduforum.api.common.exception.ErrorCode;
import com.eduforum.api.domain.auth.entity.User;
import com.eduforum.api.domain.auth.repository.UserRepository;
import com.eduforum.api.domain.course.dto.SessionCreateRequest;
import com.eduforum.api.domain.course.dto.SessionResponse;
import com.eduforum.api.domain.course.dto.SessionUpdateRequest;
import com.eduforum.api.domain.course.entity.Course;
import com.eduforum.api.domain.course.entity.CourseSession;
import com.eduforum.api.domain.course.repository.CourseRepository;
import com.eduforum.api.domain.course.repository.CourseSessionRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class SessionService {

    private final CourseSessionRepository sessionRepository;
    private final CourseRepository courseRepository;
    private final UserRepository userRepository;

    @Transactional
    public SessionResponse createSession(Long courseId, SessionCreateRequest request) {
        log.info("Creating session for course: {}", courseId);

        Course course = findCourseById(courseId);
        validateProfessorAccess(course);

        CourseSession session = CourseSession.builder()
            .course(course)
            .title(request.getTitle())
            .description(request.getDescription())
            .scheduledAt(request.getScheduledAt())
            .durationMinutes(request.getDurationMinutes())
            .meetingUrl(request.getMeetingUrl())
            .settings(request.getSettings())
            .build();

        session = sessionRepository.save(session);
        log.info("Session created successfully: {}", session.getId());

        return mapToSessionResponse(session);
    }

    @Transactional(readOnly = true)
    public List<SessionResponse> getCourseSessions(Long courseId) {
        Course course = findCourseById(courseId);

        List<CourseSession> sessions = sessionRepository.findByCourseOrderByScheduledAtDesc(course);
        return sessions.stream()
            .map(this::mapToSessionResponse)
            .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public SessionResponse getSession(Long sessionId) {
        CourseSession session = findSessionById(sessionId);
        return mapToSessionResponse(session);
    }

    @Transactional
    public SessionResponse updateSession(Long sessionId, SessionUpdateRequest request) {
        log.info("Updating session: {}", sessionId);

        CourseSession session = findSessionById(sessionId);
        validateProfessorAccess(session.getCourse());

        if (request.getTitle() != null) {
            session.setTitle(request.getTitle());
        }
        if (request.getDescription() != null) {
            session.setDescription(request.getDescription());
        }
        if (request.getScheduledAt() != null) {
            session.setScheduledAt(request.getScheduledAt());
        }
        if (request.getDurationMinutes() != null) {
            session.setDurationMinutes(request.getDurationMinutes());
        }
        if (request.getMeetingUrl() != null) {
            session.setMeetingUrl(request.getMeetingUrl());
        }
        if (request.getSettings() != null) {
            session.setSettings(request.getSettings());
        }

        session = sessionRepository.save(session);
        log.info("Session updated successfully: {}", sessionId);

        return mapToSessionResponse(session);
    }

    @Transactional
    public void deleteSession(Long sessionId) {
        log.info("Deleting session: {}", sessionId);

        CourseSession session = findSessionById(sessionId);
        validateProfessorAccess(session.getCourse());

        session.delete();
        sessionRepository.save(session);

        log.info("Session deleted successfully: {}", sessionId);
    }

    // Helper methods

    private Course findCourseById(Long courseId) {
        return courseRepository.findById(courseId)
            .filter(c -> !c.isDeleted())
            .orElseThrow(() -> new BusinessException(ErrorCode.COURSE_NOT_FOUND));
    }

    private CourseSession findSessionById(Long sessionId) {
        return sessionRepository.findById(sessionId)
            .filter(s -> !s.isDeleted())
            .orElseThrow(() -> new BusinessException(ErrorCode.SESSION_NOT_FOUND));
    }

    private User getCurrentUser() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String email = auth.getName();
        return userRepository.findByEmail(email)
            .orElseThrow(() -> new BusinessException(ErrorCode.USER_NOT_FOUND));
    }

    private void validateProfessorAccess(Course course) {
        User currentUser = getCurrentUser();
        if (!course.getProfessor().getId().equals(currentUser.getId())) {
            throw new BusinessException(ErrorCode.ACCESS_DENIED);
        }
    }

    private SessionResponse mapToSessionResponse(CourseSession session) {
        return SessionResponse.builder()
            .id(session.getId())
            .courseId(session.getCourse().getId())
            .title(session.getTitle())
            .description(session.getDescription())
            .scheduledAt(session.getScheduledAt())
            .durationMinutes(session.getDurationMinutes())
            .status(session.getStatus().name())
            .startedAt(session.getStartedAt())
            .endedAt(session.getEndedAt())
            .meetingUrl(session.getMeetingUrl())
            .settings(session.getSettings())
            .createdAt(session.getCreatedAt())
            .updatedAt(session.getUpdatedAt())
            .build();
    }
}
