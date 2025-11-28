package com.eduforum.api.domain.course.service;

import com.eduforum.api.common.exception.BusinessException;
import com.eduforum.api.common.exception.ErrorCode;
import com.eduforum.api.domain.auth.entity.User;
import com.eduforum.api.domain.auth.repository.UserRepository;
import com.eduforum.api.domain.course.dto.*;
import com.eduforum.api.domain.course.entity.Course;
import com.eduforum.api.domain.course.entity.Enrollment;
import com.eduforum.api.domain.course.entity.EnrollmentRole;
import com.eduforum.api.domain.course.entity.InviteLink;
import com.eduforum.api.domain.course.repository.CourseRepository;
import com.eduforum.api.domain.course.repository.EnrollmentRepository;
import com.eduforum.api.domain.course.repository.InviteLinkRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class CourseService {

    private final CourseRepository courseRepository;
    private final EnrollmentRepository enrollmentRepository;
    private final InviteLinkRepository inviteLinkRepository;
    private final UserRepository userRepository;

    /**
     * Create a new course
     */
    @Transactional
    public CourseResponse createCourse(CourseCreateRequest request) {
        log.info("Creating course: {}", request.getCode());

        User professor = getCurrentUser();

        // Check if course code already exists for this semester/year
        if (courseRepository.existsByCodeAndSemesterAndYear(request.getCode(), request.getSemester(), request.getYear())) {
            throw new BusinessException(ErrorCode.INVALID_INPUT_VALUE,
                "해당 학기에 이미 같은 코드의 코스가 존재합니다");
        }

        // Generate unique invite code
        String inviteCode = generateUniqueInviteCode();

        Course course = Course.builder()
            .code(request.getCode())
            .title(request.getTitle())
            .description(request.getDescription())
            .semester(request.getSemester())
            .year(request.getYear())
            .thumbnailUrl(request.getThumbnailUrl())
            .maxStudents(request.getMaxStudents())
            .inviteCode(inviteCode)
            .inviteExpiresAt(OffsetDateTime.now().plusMonths(6))
            .professor(professor)
            .settings(request.getSettings())
            .build();

        course = courseRepository.save(course);
        log.info("Course created successfully: {} (ID: {})", course.getCode(), course.getId());

        return mapToCourseResponse(course);
    }

    /**
     * Get course by ID
     */
    @Transactional(readOnly = true)
    public CourseResponse getCourse(Long courseId) {
        Course course = findCourseById(courseId);
        validateCourseAccess(course);
        return mapToCourseResponse(course);
    }

    /**
     * Get all courses for current user
     */
    @Transactional(readOnly = true)
    public List<CourseResponse> getMyCourses() {
        User currentUser = getCurrentUser();

        // If user is a professor, get courses they teach
        List<Course> professorCourses = courseRepository.findActiveCoursesByProfessor(currentUser);

        // If user is a student, get courses they're enrolled in
        List<Enrollment> enrollments = enrollmentRepository.findActiveEnrollmentsByUser(currentUser);
        List<Course> enrolledCourses = enrollments.stream()
            .map(Enrollment::getCourse)
            .collect(Collectors.toList());

        // Combine and deduplicate
        List<Course> allCourses = professorCourses;
        enrolledCourses.stream()
            .filter(c -> !allCourses.contains(c))
            .forEach(allCourses::add);

        return allCourses.stream()
            .map(this::mapToCourseResponse)
            .collect(Collectors.toList());
    }

    /**
     * Update course
     */
    @Transactional
    public CourseResponse updateCourse(Long courseId, CourseUpdateRequest request) {
        log.info("Updating course: {}", courseId);

        Course course = findCourseById(courseId);
        validateProfessorAccess(course);

        if (request.getTitle() != null) {
            course.setTitle(request.getTitle());
        }
        if (request.getDescription() != null) {
            course.setDescription(request.getDescription());
        }
        if (request.getThumbnailUrl() != null) {
            course.setThumbnailUrl(request.getThumbnailUrl());
        }
        if (request.getMaxStudents() != null) {
            course.setMaxStudents(request.getMaxStudents());
        }
        if (request.getSettings() != null) {
            course.setSettings(request.getSettings());
        }
        if (request.getIsPublished() != null) {
            if (request.getIsPublished()) {
                course.publish();
            } else {
                course.unpublish();
            }
        }

        course = courseRepository.save(course);
        log.info("Course updated successfully: {}", courseId);

        return mapToCourseResponse(course);
    }

    /**
     * Delete course (soft delete)
     */
    @Transactional
    public void deleteCourse(Long courseId) {
        log.info("Deleting course: {}", courseId);

        Course course = findCourseById(courseId);
        validateProfessorAccess(course);

        course.delete();
        courseRepository.save(course);

        log.info("Course deleted successfully: {}", courseId);
    }

    /**
     * Archive course
     */
    @Transactional
    public CourseResponse archiveCourse(Long courseId) {
        log.info("Archiving course: {}", courseId);

        Course course = findCourseById(courseId);
        validateProfessorAccess(course);

        course.unpublish();
        course = courseRepository.save(course);

        log.info("Course archived successfully: {}", courseId);
        return mapToCourseResponse(course);
    }

    /**
     * Create invite link for a course
     */
    @Transactional
    public InviteLinkResponse createInviteLink(Long courseId, InviteLinkCreateRequest request) {
        log.info("Creating invite link for course: {}", courseId);

        Course course = findCourseById(courseId);
        validateProfessorAccess(course);

        String code = generateUniqueInviteCode();

        InviteLink inviteLink = InviteLink.builder()
            .course(course)
            .code(code)
            .role(request.getRole())
            .maxUses(request.getMaxUses())
            .expiresAt(request.getExpiresAt())
            .build();

        inviteLink = inviteLinkRepository.save(inviteLink);
        log.info("Invite link created successfully: {}", code);

        return mapToInviteLinkResponse(inviteLink);
    }

    /**
     * Get all invite links for a course
     */
    @Transactional(readOnly = true)
    public List<InviteLinkResponse> getInviteLinks(Long courseId) {
        Course course = findCourseById(courseId);
        validateProfessorAccess(course);

        List<InviteLink> inviteLinks = inviteLinkRepository.findInviteLinksByCourse(course);
        return inviteLinks.stream()
            .map(this::mapToInviteLinkResponse)
            .collect(Collectors.toList());
    }

    /**
     * Delete invite link
     */
    @Transactional
    public void deleteInviteLink(Long courseId, Long linkId) {
        log.info("Deleting invite link: {} for course: {}", linkId, courseId);

        Course course = findCourseById(courseId);
        validateProfessorAccess(course);

        InviteLink inviteLink = inviteLinkRepository.findById(linkId)
            .orElseThrow(() -> new BusinessException(ErrorCode.ENTITY_NOT_FOUND, "초대 링크를 찾을 수 없습니다"));

        if (!inviteLink.getCourse().getId().equals(courseId)) {
            throw new BusinessException(ErrorCode.ACCESS_DENIED);
        }

        inviteLink.delete();
        inviteLinkRepository.save(inviteLink);

        log.info("Invite link deleted successfully: {}", linkId);
    }

    /**
     * Verify invite code
     */
    @Transactional(readOnly = true)
    public CourseResponse verifyInviteCode(String code) {
        InviteLink inviteLink = inviteLinkRepository.findByCode(code)
            .orElseThrow(() -> new BusinessException(ErrorCode.ENTITY_NOT_FOUND, "유효하지 않은 초대 코드입니다"));

        if (!inviteLink.canBeUsed()) {
            throw new BusinessException(ErrorCode.INVALID_INPUT_VALUE,
                "초대 링크가 만료되었거나 사용 가능한 횟수를 초과했습니다");
        }

        return mapToCourseResponse(inviteLink.getCourse());
    }

    // Helper methods

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

    private void validateCourseAccess(Course course) {
        User currentUser = getCurrentUser();

        // Professor can always access their courses
        if (course.getProfessor().getId().equals(currentUser.getId())) {
            return;
        }

        // Check if user is enrolled
        boolean isEnrolled = enrollmentRepository.isActivelyEnrolled(currentUser, course);
        if (!isEnrolled) {
            throw new BusinessException(ErrorCode.COURSE_ACCESS_DENIED);
        }
    }

    private void validateProfessorAccess(Course course) {
        User currentUser = getCurrentUser();

        if (!course.getProfessor().getId().equals(currentUser.getId())) {
            throw new BusinessException(ErrorCode.ACCESS_DENIED, "코스 교수만 접근할 수 있습니다");
        }
    }

    private String generateUniqueInviteCode() {
        String code;
        do {
            code = generateRandomCode(10);
        } while (inviteLinkRepository.existsByCode(code));
        return code;
    }

    private String generateRandomCode(int length) {
        String chars = "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
        Random random = new Random();
        StringBuilder code = new StringBuilder();
        for (int i = 0; i < length; i++) {
            code.append(chars.charAt(random.nextInt(chars.length())));
        }
        return code.toString();
    }

    private CourseResponse mapToCourseResponse(Course course) {
        Long studentCount = enrollmentRepository.countActiveStudents(course);

        return CourseResponse.builder()
            .id(course.getId())
            .code(course.getCode())
            .title(course.getTitle())
            .description(course.getDescription())
            .semester(course.getSemester())
            .year(course.getYear())
            .thumbnailUrl(course.getThumbnailUrl())
            .inviteCode(course.getInviteCode())
            .inviteExpiresAt(course.getInviteExpiresAt())
            .maxStudents(course.getMaxStudents())
            .currentStudents(studentCount.intValue())
            .isPublished(course.getIsPublished())
            .settings(course.getSettings())
            .professor(CourseResponse.ProfessorInfo.builder()
                .id(course.getProfessor().getId())
                .email(course.getProfessor().getEmail())
                .name(course.getProfessor().getFullName())
                .profileImageUrl(course.getProfessor().getProfileImageUrl())
                .build())
            .createdAt(course.getCreatedAt())
            .updatedAt(course.getUpdatedAt())
            .build();
    }

    private InviteLinkResponse mapToInviteLinkResponse(InviteLink inviteLink) {
        return InviteLinkResponse.builder()
            .id(inviteLink.getId())
            .courseId(inviteLink.getCourse().getId())
            .code(inviteLink.getCode())
            .role(inviteLink.getRole().name())
            .maxUses(inviteLink.getMaxUses())
            .usedCount(inviteLink.getUsedCount())
            .expiresAt(inviteLink.getExpiresAt())
            .isActive(inviteLink.getIsActive())
            .createdAt(inviteLink.getCreatedAt())
            .build();
    }
}
