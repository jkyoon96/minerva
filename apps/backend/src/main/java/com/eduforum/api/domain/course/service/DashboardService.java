package com.eduforum.api.domain.course.service;

import com.eduforum.api.common.exception.BusinessException;
import com.eduforum.api.common.exception.ErrorCode;
import com.eduforum.api.domain.auth.entity.User;
import com.eduforum.api.domain.auth.repository.UserRepository;
import com.eduforum.api.domain.course.dto.DashboardResponse;
import com.eduforum.api.domain.course.dto.StudentDashboardResponse;
import com.eduforum.api.domain.course.entity.*;
import com.eduforum.api.domain.course.repository.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class DashboardService {

    private final CourseRepository courseRepository;
    private final EnrollmentRepository enrollmentRepository;
    private final CourseSessionRepository sessionRepository;
    private final AssignmentRepository assignmentRepository;
    private final AssignmentSubmissionRepository submissionRepository;
    private final UserRepository userRepository;

    @Transactional(readOnly = true)
    public DashboardResponse getProfessorDashboard() {
        User currentUser = getCurrentUser();

        List<Course> courses = courseRepository.findActiveCoursesByProfessor(currentUser);

        List<DashboardResponse.DashboardCourse> dashboardCourses = courses.stream()
            .map(course -> {
                Long studentCount = enrollmentRepository.countActiveStudents(course);
                OffsetDateTime nextSessionAt = sessionRepository.findUpcomingSessions(course, OffsetDateTime.now())
                    .stream()
                    .findFirst()
                    .map(CourseSession::getScheduledAt)
                    .orElse(null);

                return DashboardResponse.DashboardCourse.builder()
                    .id(course.getId())
                    .code(course.getCode())
                    .title(course.getTitle())
                    .semester(course.getSemester())
                    .year(course.getYear())
                    .studentCount(studentCount.intValue())
                    .nextSessionAt(nextSessionAt)
                    .build();
            })
            .collect(Collectors.toList());

        // Get upcoming sessions across all courses
        OffsetDateTime now = OffsetDateTime.now();
        OffsetDateTime weekFromNow = now.plusWeeks(1);
        List<CourseSession> upcomingSessions = sessionRepository.findSessionsBetween(now, weekFromNow)
            .stream()
            .filter(session -> courses.stream()
                .anyMatch(course -> course.getId().equals(session.getCourse().getId())))
            .limit(5)
            .collect(Collectors.toList());

        List<DashboardResponse.UpcomingSession> upcomingSessionsList = upcomingSessions.stream()
            .map(session -> DashboardResponse.UpcomingSession.builder()
                .id(session.getId())
                .courseTitle(session.getCourse().getTitle())
                .sessionTitle(session.getTitle())
                .scheduledAt(session.getScheduledAt())
                .durationMinutes(session.getDurationMinutes())
                .build())
            .collect(Collectors.toList());

        // Count pending grading
        int pendingGradingCount = courses.stream()
            .mapToInt(course -> submissionRepository.countUngradedSubmissionsByCourse(course).intValue())
            .sum();

        // Count total students
        int totalStudents = courses.stream()
            .mapToInt(course -> enrollmentRepository.countActiveStudents(course).intValue())
            .sum();

        return DashboardResponse.builder()
            .courses(dashboardCourses)
            .upcomingSessions(upcomingSessionsList)
            .pendingGradingCount(pendingGradingCount)
            .totalStudents(totalStudents)
            .build();
    }

    @Transactional(readOnly = true)
    public StudentDashboardResponse getStudentDashboard() {
        User currentUser = getCurrentUser();

        List<Enrollment> enrollments = enrollmentRepository.findActiveEnrollmentsByUser(currentUser);

        List<StudentDashboardResponse.StudentCourse> studentCourses = enrollments.stream()
            .map(enrollment -> {
                Course course = enrollment.getCourse();

                OffsetDateTime nextSessionAt = sessionRepository.findUpcomingSessions(course, OffsetDateTime.now())
                    .stream()
                    .findFirst()
                    .map(CourseSession::getScheduledAt)
                    .orElse(null);

                // Count pending assignments
                List<Assignment> publishedAssignments = assignmentRepository.findPublishedAssignmentsByCourse(course);
                int pendingAssignments = (int) publishedAssignments.stream()
                    .filter(assignment -> submissionRepository.findLatestSubmission(assignment, currentUser).isEmpty())
                    .count();

                return StudentDashboardResponse.StudentCourse.builder()
                    .id(course.getId())
                    .code(course.getCode())
                    .title(course.getTitle())
                    .professorName(course.getProfessor().getFullName())
                    .thumbnailUrl(course.getThumbnailUrl())
                    .nextSessionAt(nextSessionAt)
                    .pendingAssignments(pendingAssignments)
                    .build();
            })
            .collect(Collectors.toList());

        // Get upcoming assignments
        OffsetDateTime now = OffsetDateTime.now();
        OffsetDateTime weekFromNow = now.plusWeeks(1);
        List<Assignment> assignments = assignmentRepository.findAssignmentsDueBetween(now, weekFromNow);

        List<StudentDashboardResponse.UpcomingAssignment> upcomingAssignments = assignments.stream()
            .filter(assignment -> enrollments.stream()
                .anyMatch(e -> e.getCourse().getId().equals(assignment.getCourse().getId())))
            .map(assignment -> {
                boolean isSubmitted = submissionRepository.findLatestSubmission(assignment, currentUser).isPresent();

                return StudentDashboardResponse.UpcomingAssignment.builder()
                    .id(assignment.getId())
                    .courseTitle(assignment.getCourse().getTitle())
                    .assignmentTitle(assignment.getTitle())
                    .dueDate(assignment.getDueDate())
                    .isSubmitted(isSubmitted)
                    .build();
            })
            .limit(5)
            .collect(Collectors.toList());

        // Get upcoming sessions
        List<CourseSession> sessions = sessionRepository.findSessionsBetween(now, weekFromNow);
        List<StudentDashboardResponse.UpcomingSession> upcomingSessions = sessions.stream()
            .filter(session -> enrollments.stream()
                .anyMatch(e -> e.getCourse().getId().equals(session.getCourse().getId())))
            .map(session -> StudentDashboardResponse.UpcomingSession.builder()
                .id(session.getId())
                .courseTitle(session.getCourse().getTitle())
                .sessionTitle(session.getTitle())
                .scheduledAt(session.getScheduledAt())
                .durationMinutes(session.getDurationMinutes())
                .build())
            .limit(5)
            .collect(Collectors.toList());

        // Get recent grades
        List<AssignmentSubmission> recentGrades = submissionRepository.findRecentGradedSubmissionsByStudent(currentUser)
            .stream()
            .limit(5)
            .collect(Collectors.toList());

        List<StudentDashboardResponse.RecentGrade> recentGradesList = recentGrades.stream()
            .map(submission -> StudentDashboardResponse.RecentGrade.builder()
                .assignmentId(submission.getAssignment().getId())
                .courseTitle(submission.getAssignment().getCourse().getTitle())
                .assignmentTitle(submission.getAssignment().getTitle())
                .score(submission.getScore())
                .maxScore(submission.getAssignment().getMaxScore())
                .gradedAt(submission.getGradedAt())
                .build())
            .collect(Collectors.toList());

        return StudentDashboardResponse.builder()
            .courses(studentCourses)
            .upcomingAssignments(upcomingAssignments)
            .upcomingSessions(upcomingSessions)
            .recentGrades(recentGradesList)
            .build();
    }

    private User getCurrentUser() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String email = auth.getName();
        return userRepository.findByEmail(email)
            .orElseThrow(() -> new BusinessException(ErrorCode.USER_NOT_FOUND));
    }
}
