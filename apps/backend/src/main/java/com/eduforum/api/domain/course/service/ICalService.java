package com.eduforum.api.domain.course.service;

import com.eduforum.api.common.exception.BusinessException;
import com.eduforum.api.common.exception.ErrorCode;
import com.eduforum.api.domain.auth.entity.User;
import com.eduforum.api.domain.auth.repository.UserRepository;
import com.eduforum.api.domain.course.entity.Course;
import com.eduforum.api.domain.course.entity.CourseSession;
import com.eduforum.api.domain.course.entity.Enrollment;
import com.eduforum.api.domain.course.repository.CourseRepository;
import com.eduforum.api.domain.course.repository.CourseSessionRepository;
import com.eduforum.api.domain.course.repository.EnrollmentRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.OffsetDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.UUID;

/**
 * Service for generating iCal format calendar exports
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class ICalService {

    private final CourseRepository courseRepository;
    private final CourseSessionRepository courseSessionRepository;
    private final EnrollmentRepository enrollmentRepository;
    private final UserRepository userRepository;

    private static final DateTimeFormatter ICAL_DATE_FORMAT =
        DateTimeFormatter.ofPattern("yyyyMMdd'T'HHmmss'Z'");

    /**
     * Generate iCal for a specific course
     */
    @Transactional(readOnly = true)
    public String generateICalForCourse(Long courseId) {
        log.info("Generating iCal for course: {}", courseId);

        Course course = findCourseById(courseId);
        List<CourseSession> sessions = courseSessionRepository.findActiveByCourse(course);

        StringBuilder ical = new StringBuilder();
        ical.append(getICalHeader());

        for (CourseSession session : sessions) {
            ical.append(formatSessionAsEvent(session, course));
        }

        ical.append(getICalFooter());

        log.info("Generated iCal with {} events for course: {}", sessions.size(), courseId);
        return ical.toString();
    }

    /**
     * Generate iCal for all courses a student is enrolled in
     */
    @Transactional(readOnly = true)
    public String generateICalForStudent() {
        log.info("Generating iCal for current user");

        User currentUser = getCurrentUser();
        List<Enrollment> enrollments = enrollmentRepository.findActiveEnrollmentsByUser(currentUser);

        StringBuilder ical = new StringBuilder();
        ical.append(getICalHeader());

        int eventCount = 0;
        for (Enrollment enrollment : enrollments) {
            Course course = enrollment.getCourse();
            List<CourseSession> sessions = courseSessionRepository.findActiveByCourse(course);

            for (CourseSession session : sessions) {
                ical.append(formatSessionAsEvent(session, course));
                eventCount++;
            }
        }

        ical.append(getICalFooter());

        log.info("Generated iCal with {} events for user: {}", eventCount, currentUser.getEmail());
        return ical.toString();
    }

    /**
     * Generate iCal for all courses a professor teaches
     */
    @Transactional(readOnly = true)
    public String generateICalForProfessor() {
        log.info("Generating iCal for professor");

        User currentUser = getCurrentUser();
        List<Course> courses = courseRepository.findActiveCoursesByProfessor(currentUser);

        StringBuilder ical = new StringBuilder();
        ical.append(getICalHeader());

        int eventCount = 0;
        for (Course course : courses) {
            List<CourseSession> sessions = courseSessionRepository.findActiveByCourse(course);

            for (CourseSession session : sessions) {
                ical.append(formatSessionAsEvent(session, course));
                eventCount++;
            }
        }

        ical.append(getICalFooter());

        log.info("Generated iCal with {} events for professor: {}", eventCount, currentUser.getEmail());
        return ical.toString();
    }

    // Helper methods

    /**
     * Get iCal header (RFC 5545 compliant)
     */
    private String getICalHeader() {
        return "BEGIN:VCALENDAR\r\n" +
               "VERSION:2.0\r\n" +
               "PRODID:-//EduForum//Course Calendar//EN\r\n" +
               "CALSCALE:GREGORIAN\r\n" +
               "METHOD:PUBLISH\r\n" +
               "X-WR-CALNAME:EduForum Course Schedule\r\n" +
               "X-WR-TIMEZONE:UTC\r\n";
    }

    /**
     * Get iCal footer
     */
    private String getICalFooter() {
        return "END:VCALENDAR\r\n";
    }

    /**
     * Format a course session as an iCal event
     */
    private String formatSessionAsEvent(CourseSession session, Course course) {
        StringBuilder event = new StringBuilder();

        String uid = generateUID(session);
        String summary = escapeiCalText(course.getCode() + ": " + session.getTitle());
        String description = escapeiCalText(
            session.getDescription() != null ? session.getDescription() : ""
        );
        String location = escapeiCalText(
            session.getLocation() != null ? session.getLocation() : "Online"
        );

        OffsetDateTime now = OffsetDateTime.now();
        String dtStamp = formatDateTime(now);
        String dtStart = formatDateTime(session.getScheduledAt());
        String dtEnd = formatDateTime(
            session.getScheduledEndAt() != null ?
                session.getScheduledEndAt() :
                session.getScheduledAt().plusHours(2)
        );

        event.append("BEGIN:VEVENT\r\n");
        event.append("UID:").append(uid).append("\r\n");
        event.append("DTSTAMP:").append(dtStamp).append("\r\n");
        event.append("DTSTART:").append(dtStart).append("\r\n");
        event.append("DTEND:").append(dtEnd).append("\r\n");
        event.append("SUMMARY:").append(summary).append("\r\n");

        if (!description.isEmpty()) {
            event.append("DESCRIPTION:").append(description).append("\r\n");
        }

        event.append("LOCATION:").append(location).append("\r\n");
        event.append("STATUS:").append(getEventStatus(session)).append("\r\n");
        event.append("SEQUENCE:0\r\n");
        event.append("TRANSP:OPAQUE\r\n");

        // Add course information as categories
        event.append("CATEGORIES:").append(escapeiCalText(course.getTitle())).append("\r\n");

        // Add organizer (professor)
        String organizerEmail = course.getProfessor().getEmail();
        String organizerName = escapeiCalText(course.getProfessor().getFullName());
        event.append("ORGANIZER;CN=").append(organizerName)
             .append(":mailto:").append(organizerEmail).append("\r\n");

        event.append("END:VEVENT\r\n");

        return event.toString();
    }

    /**
     * Generate unique ID for event
     */
    private String generateUID(CourseSession session) {
        return "session-" + session.getId() + "@eduforum.com";
    }

    /**
     * Format datetime for iCal (UTC)
     */
    private String formatDateTime(OffsetDateTime dateTime) {
        return dateTime.withOffsetSameInstant(java.time.ZoneOffset.UTC)
            .format(ICAL_DATE_FORMAT);
    }

    /**
     * Get event status based on session status
     */
    private String getEventStatus(CourseSession session) {
        return switch (session.getStatus()) {
            case SCHEDULED -> "CONFIRMED";
            case IN_PROGRESS -> "CONFIRMED";
            case COMPLETED -> "CONFIRMED";
            case CANCELLED -> "CANCELLED";
            default -> "TENTATIVE";
        };
    }

    /**
     * Escape text for iCal format
     */
    private String escapeiCalText(String text) {
        if (text == null) {
            return "";
        }
        return text.replace("\\", "\\\\")
                   .replace(";", "\\;")
                   .replace(",", "\\,")
                   .replace("\n", "\\n")
                   .replace("\r", "");
    }

    private Course findCourseById(Long courseId) {
        return courseRepository.findById(courseId)
            .filter(c -> !c.isDeleted())
            .orElseThrow(() -> new BusinessException(ErrorCode.COURSE_NOT_FOUND));
    }

    private User getCurrentUser() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String email = auth.getName();
        return userRepository.findByEmail(email)
            .orElseThrow(() -> new BusinessException(ErrorCode.USER_NOT_FOUND));
    }
}
