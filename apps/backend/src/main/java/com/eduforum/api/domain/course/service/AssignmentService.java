package com.eduforum.api.domain.course.service;

import com.eduforum.api.common.exception.BusinessException;
import com.eduforum.api.common.exception.ErrorCode;
import com.eduforum.api.domain.auth.entity.User;
import com.eduforum.api.domain.auth.repository.UserRepository;
import com.eduforum.api.domain.course.dto.*;
import com.eduforum.api.domain.course.entity.Assignment;
import com.eduforum.api.domain.course.entity.AssignmentSubmission;
import com.eduforum.api.domain.course.entity.Course;
import com.eduforum.api.domain.course.repository.AssignmentRepository;
import com.eduforum.api.domain.course.repository.AssignmentSubmissionRepository;
import com.eduforum.api.domain.course.repository.CourseRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class AssignmentService {

    private final AssignmentRepository assignmentRepository;
    private final AssignmentSubmissionRepository submissionRepository;
    private final CourseRepository courseRepository;
    private final UserRepository userRepository;

    @Transactional
    public AssignmentResponse createAssignment(Long courseId, AssignmentCreateRequest request) {
        log.info("Creating assignment for course: {}", courseId);

        Course course = findCourseById(courseId);
        validateProfessorAccess(course);

        Assignment assignment = Assignment.builder()
            .course(course)
            .title(request.getTitle())
            .description(request.getDescription())
            .dueDate(request.getDueDate())
            .maxScore(request.getMaxScore())
            .allowLate(request.getAllowLate())
            .latePenaltyPercent(request.getLatePenaltyPercent())
            .maxAttempts(request.getMaxAttempts())
            .attachments(request.getAttachments())
            .build();

        assignment = assignmentRepository.save(assignment);
        log.info("Assignment created successfully: {}", assignment.getId());

        return mapToAssignmentResponse(assignment, null);
    }

    @Transactional(readOnly = true)
    public List<AssignmentResponse> getCourseAssignments(Long courseId) {
        Course course = findCourseById(courseId);
        User currentUser = getCurrentUser();

        List<Assignment> assignments = assignmentRepository.findByCourseOrderByDueDateAsc(course);

        return assignments.stream()
            .map(assignment -> {
                // Include submission info if student
                AssignmentSubmission submission = null;
                if (!course.getProfessor().getId().equals(currentUser.getId())) {
                    submission = submissionRepository.findLatestSubmission(assignment, currentUser)
                        .orElse(null);
                }
                return mapToAssignmentResponse(assignment, submission);
            })
            .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public AssignmentResponse getAssignment(Long assignmentId) {
        Assignment assignment = findAssignmentById(assignmentId);
        User currentUser = getCurrentUser();

        AssignmentSubmission submission = null;
        if (!assignment.getCourse().getProfessor().getId().equals(currentUser.getId())) {
            submission = submissionRepository.findLatestSubmission(assignment, currentUser)
                .orElse(null);
        }

        return mapToAssignmentResponse(assignment, submission);
    }

    @Transactional
    public AssignmentSubmissionResponse submitAssignment(Long assignmentId, AssignmentSubmissionRequest request) {
        log.info("Submitting assignment: {}", assignmentId);

        Assignment assignment = findAssignmentById(assignmentId);
        User currentUser = getCurrentUser();

        // Check if assignment is published
        if (!assignment.isPublished()) {
            throw new BusinessException(ErrorCode.INVALID_INPUT_VALUE, "아직 게시되지 않은 과제입니다");
        }

        // Check if past due (if late submission not allowed)
        if (assignment.isPastDue() && !assignment.getAllowLate()) {
            throw new BusinessException(ErrorCode.SUBMISSION_DEADLINE_PASSED);
        }

        // Check attempt number
        List<AssignmentSubmission> previousSubmissions =
            submissionRepository.findByAssignmentAndStudent(assignment, currentUser);

        if (previousSubmissions.size() >= assignment.getMaxAttempts()) {
            throw new BusinessException(ErrorCode.INVALID_INPUT_VALUE,
                "최대 제출 횟수를 초과했습니다");
        }

        int attemptNumber = previousSubmissions.size() + 1;
        boolean isLate = assignment.isPastDue();

        AssignmentSubmission submission = AssignmentSubmission.builder()
            .assignment(assignment)
            .student(currentUser)
            .attemptNumber(attemptNumber)
            .content(request.getContent())
            .attachments(request.getAttachments())
            .isLate(isLate)
            .build();

        submission = submissionRepository.save(submission);
        log.info("Assignment submitted successfully: {}", submission.getId());

        return mapToSubmissionResponse(submission);
    }

    // Helper methods

    private Assignment findAssignmentById(Long assignmentId) {
        return assignmentRepository.findById(assignmentId)
            .filter(a -> !a.isDeleted())
            .orElseThrow(() -> new BusinessException(ErrorCode.ASSIGNMENT_NOT_FOUND));
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

    private void validateProfessorAccess(Course course) {
        User currentUser = getCurrentUser();
        if (!course.getProfessor().getId().equals(currentUser.getId())) {
            throw new BusinessException(ErrorCode.ACCESS_DENIED);
        }
    }

    private AssignmentResponse mapToAssignmentResponse(Assignment assignment, AssignmentSubmission submission) {
        AssignmentResponse.AssignmentResponseBuilder builder = AssignmentResponse.builder()
            .id(assignment.getId())
            .courseId(assignment.getCourse().getId())
            .title(assignment.getTitle())
            .description(assignment.getDescription())
            .dueDate(assignment.getDueDate())
            .maxScore(assignment.getMaxScore())
            .allowLate(assignment.getAllowLate())
            .latePenaltyPercent(assignment.getLatePenaltyPercent())
            .maxAttempts(assignment.getMaxAttempts())
            .attachments(assignment.getAttachments())
            .status(assignment.getStatus().name())
            .publishedAt(assignment.getPublishedAt())
            .createdAt(assignment.getCreatedAt())
            .updatedAt(assignment.getUpdatedAt());

        if (submission != null) {
            builder.submission(AssignmentResponse.SubmissionInfo.builder()
                .isSubmitted(true)
                .submittedAt(submission.getSubmittedAt())
                .score(submission.getScore())
                .isLate(submission.getIsLate())
                .build());
        }

        return builder.build();
    }

    private AssignmentSubmissionResponse mapToSubmissionResponse(AssignmentSubmission submission) {
        return AssignmentSubmissionResponse.builder()
            .id(submission.getId())
            .assignmentId(submission.getAssignment().getId())
            .studentId(submission.getStudent().getId())
            .studentName(submission.getStudent().getFullName())
            .attemptNumber(submission.getAttemptNumber())
            .content(submission.getContent())
            .attachments(submission.getAttachments())
            .submittedAt(submission.getSubmittedAt())
            .score(submission.getScore())
            .feedback(submission.getFeedback())
            .gradedByName(submission.getGradedBy() != null ? submission.getGradedBy().getFullName() : null)
            .gradedAt(submission.getGradedAt())
            .isLate(submission.getIsLate())
            .build();
    }
}
