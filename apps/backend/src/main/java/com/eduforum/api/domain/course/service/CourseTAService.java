package com.eduforum.api.domain.course.service;

import com.eduforum.api.common.exception.BusinessException;
import com.eduforum.api.common.exception.ErrorCode;
import com.eduforum.api.domain.auth.entity.User;
import com.eduforum.api.domain.auth.repository.UserRepository;
import com.eduforum.api.domain.course.dto.AssignTARequest;
import com.eduforum.api.domain.course.dto.CourseTAResponse;
import com.eduforum.api.domain.course.dto.TAPermissions;
import com.eduforum.api.domain.course.entity.Course;
import com.eduforum.api.domain.course.entity.CourseTA;
import com.eduforum.api.domain.course.repository.CourseRepository;
import com.eduforum.api.domain.course.repository.CourseTARepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.OffsetDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Service for managing course TAs
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class CourseTAService {

    private final CourseTARepository courseTARepository;
    private final CourseRepository courseRepository;
    private final UserRepository userRepository;

    /**
     * Assign TA to course
     */
    @Transactional
    public CourseTAResponse assignTA(Long courseId, AssignTARequest request) {
        log.info("Assigning TA to course: {}, TA user ID: {}", courseId, request.getTaUserId());

        Course course = findCourseById(courseId);
        User professor = getCurrentUser();
        validateProfessorAccess(course, professor);

        User taUser = userRepository.findById(request.getTaUserId())
            .orElseThrow(() -> new BusinessException(ErrorCode.USER_NOT_FOUND, "TA 사용자를 찾을 수 없습니다"));

        // Check if already assigned
        if (courseTARepository.existsByCourseIdAndTaUserId(courseId, request.getTaUserId())) {
            throw new BusinessException(ErrorCode.INVALID_INPUT_VALUE, "이미 TA로 배정된 사용자입니다");
        }

        // Create TA assignment
        Map<String, Object> permissions = convertPermissionsToMap(request.getPermissions());

        CourseTA courseTA = CourseTA.builder()
            .course(course)
            .taUser(taUser)
            .assignedBy(professor)
            .assignedAt(OffsetDateTime.now())
            .permissions(permissions)
            .build();

        courseTA = courseTARepository.save(courseTA);
        log.info("TA assigned successfully: {} to course: {}", request.getTaUserId(), courseId);

        return mapToResponse(courseTA);
    }

    /**
     * Remove TA from course
     */
    @Transactional
    public void removeTA(Long courseId, Long taId) {
        log.info("Removing TA: {} from course: {}", taId, courseId);

        CourseTA courseTA = courseTARepository.findById(taId)
            .filter(ct -> !ct.isDeleted())
            .orElseThrow(() -> new BusinessException(ErrorCode.ENTITY_NOT_FOUND, "TA 배정을 찾을 수 없습니다"));

        if (!courseTA.getCourse().getId().equals(courseId)) {
            throw new BusinessException(ErrorCode.INVALID_INPUT_VALUE, "잘못된 요청입니다");
        }

        validateProfessorAccess(courseTA.getCourse(), getCurrentUser());

        courseTA.delete();
        courseTARepository.save(courseTA);

        log.info("TA removed successfully: {} from course: {}", taId, courseId);
    }

    /**
     * Update TA permissions
     */
    @Transactional
    public CourseTAResponse updateTAPermissions(Long courseId, Long taId, TAPermissions permissions) {
        log.info("Updating TA permissions: {} for course: {}", taId, courseId);

        CourseTA courseTA = courseTARepository.findById(taId)
            .filter(ct -> !ct.isDeleted())
            .orElseThrow(() -> new BusinessException(ErrorCode.ENTITY_NOT_FOUND, "TA 배정을 찾을 수 없습니다"));

        if (!courseTA.getCourse().getId().equals(courseId)) {
            throw new BusinessException(ErrorCode.INVALID_INPUT_VALUE, "잘못된 요청입니다");
        }

        validateProfessorAccess(courseTA.getCourse(), getCurrentUser());

        Map<String, Object> permissionsMap = convertPermissionsToMap(permissions);
        courseTA.setPermissions(permissionsMap);

        courseTA = courseTARepository.save(courseTA);
        log.info("TA permissions updated successfully: {} for course: {}", taId, courseId);

        return mapToResponse(courseTA);
    }

    /**
     * Get all TAs for a course
     */
    @Transactional(readOnly = true)
    public List<CourseTAResponse> getTAsByCourse(Long courseId) {
        Course course = findCourseById(courseId);

        List<CourseTA> tas = courseTARepository.findByCourseOrderByAssignedAt(course);
        return tas.stream()
            .map(this::mapToResponse)
            .collect(Collectors.toList());
    }

    /**
     * Get TA assignment by ID
     */
    @Transactional(readOnly = true)
    public CourseTAResponse getTAById(Long taId) {
        CourseTA courseTA = courseTARepository.findById(taId)
            .filter(ct -> !ct.isDeleted())
            .orElseThrow(() -> new BusinessException(ErrorCode.ENTITY_NOT_FOUND, "TA 배정을 찾을 수 없습니다"));

        return mapToResponse(courseTA);
    }

    /**
     * Check if user is TA for a course
     */
    @Transactional(readOnly = true)
    public boolean isTAForCourse(Long courseId, Long userId) {
        return courseTARepository.existsByCourseIdAndTaUserId(courseId, userId);
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

    private void validateProfessorAccess(Course course, User user) {
        if (!course.getProfessor().getId().equals(user.getId())) {
            throw new BusinessException(ErrorCode.ACCESS_DENIED, "코스 교수만 접근할 수 있습니다");
        }
    }

    private Map<String, Object> convertPermissionsToMap(TAPermissions permissions) {
        if (permissions == null) {
            return CourseTA.getDefaultPermissions();
        }

        Map<String, Object> map = new HashMap<>();
        map.put("canGrade", permissions.getCanGrade() != null ? permissions.getCanGrade() : true);
        map.put("canManageStudents", permissions.getCanManageStudents() != null ? permissions.getCanManageStudents() : true);
        map.put("canManageSessions", permissions.getCanManageSessions() != null ? permissions.getCanManageSessions() : false);
        map.put("canManageAssignments", permissions.getCanManageAssignments() != null ? permissions.getCanManageAssignments() : true);
        map.put("canViewAnalytics", permissions.getCanViewAnalytics() != null ? permissions.getCanViewAnalytics() : true);
        map.put("canModerateDiscussions", permissions.getCanModerateDiscussions() != null ? permissions.getCanModerateDiscussions() : true);
        return map;
    }

    private TAPermissions convertMapToPermissions(Map<String, Object> map) {
        return TAPermissions.builder()
            .canGrade(getBoolean(map, "canGrade", true))
            .canManageStudents(getBoolean(map, "canManageStudents", true))
            .canManageSessions(getBoolean(map, "canManageSessions", false))
            .canManageAssignments(getBoolean(map, "canManageAssignments", true))
            .canViewAnalytics(getBoolean(map, "canViewAnalytics", true))
            .canModerateDiscussions(getBoolean(map, "canModerateDiscussions", true))
            .build();
    }

    private Boolean getBoolean(Map<String, Object> map, String key, boolean defaultValue) {
        Object value = map.get(key);
        if (value instanceof Boolean) {
            return (Boolean) value;
        }
        return defaultValue;
    }

    private CourseTAResponse mapToResponse(CourseTA courseTA) {
        return CourseTAResponse.builder()
            .id(courseTA.getId())
            .courseId(courseTA.getCourse().getId())
            .taUser(CourseTAResponse.TAUserInfo.builder()
                .id(courseTA.getTaUser().getId())
                .email(courseTA.getTaUser().getEmail())
                .name(courseTA.getTaUser().getFullName())
                .profileImageUrl(courseTA.getTaUser().getProfileImageUrl())
                .build())
            .assignedBy(CourseTAResponse.AssignedByInfo.builder()
                .id(courseTA.getAssignedBy().getId())
                .email(courseTA.getAssignedBy().getEmail())
                .name(courseTA.getAssignedBy().getFullName())
                .build())
            .assignedAt(courseTA.getAssignedAt())
            .permissions(convertMapToPermissions(courseTA.getPermissions()))
            .createdAt(courseTA.getCreatedAt())
            .build();
    }
}
