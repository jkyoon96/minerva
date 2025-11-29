package com.eduforum.api.domain.course.service;

import com.eduforum.api.common.exception.BusinessException;
import com.eduforum.api.common.exception.ErrorCode;
import com.eduforum.api.domain.auth.entity.User;
import com.eduforum.api.domain.auth.repository.UserRepository;
import com.eduforum.api.domain.course.dto.GradingCriteriaRequest;
import com.eduforum.api.domain.course.dto.GradingCriteriaResponse;
import com.eduforum.api.domain.course.dto.RubricItemDto;
import com.eduforum.api.domain.course.entity.Course;
import com.eduforum.api.domain.course.entity.GradingCriteria;
import com.eduforum.api.domain.course.entity.RubricItem;
import com.eduforum.api.domain.course.repository.CourseRepository;
import com.eduforum.api.domain.course.repository.GradingCriteriaRepository;
import com.eduforum.api.domain.course.repository.RubricItemRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Service for managing grading criteria
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class GradingCriteriaService {

    private final GradingCriteriaRepository gradingCriteriaRepository;
    private final RubricItemRepository rubricItemRepository;
    private final CourseRepository courseRepository;
    private final UserRepository userRepository;

    /**
     * Create grading criteria for a course
     */
    @Transactional
    public GradingCriteriaResponse createCriteria(Long courseId, GradingCriteriaRequest request) {
        log.info("Creating grading criteria for course: {}", courseId);

        Course course = findCourseById(courseId);
        validateProfessorAccess(course);

        GradingCriteria criteria = GradingCriteria.builder()
            .course(course)
            .name(request.getName())
            .description(request.getDescription())
            .weight(request.getWeight())
            .maxScore(request.getMaxScore())
            .orderIndex(request.getOrderIndex() != null ? request.getOrderIndex() : 0)
            .build();

        criteria = gradingCriteriaRepository.save(criteria);

        // Add rubric items if provided
        if (request.getRubricItems() != null && !request.getRubricItems().isEmpty()) {
            for (RubricItemDto itemDto : request.getRubricItems()) {
                RubricItem item = RubricItem.builder()
                    .criteria(criteria)
                    .level(itemDto.getLevel())
                    .description(itemDto.getDescription())
                    .score(itemDto.getScore())
                    .orderIndex(itemDto.getOrderIndex() != null ? itemDto.getOrderIndex() : 0)
                    .build();
                criteria.addRubricItem(item);
            }
            criteria = gradingCriteriaRepository.save(criteria);
        }

        log.info("Grading criteria created successfully: {}", criteria.getId());
        return mapToResponse(criteria);
    }

    /**
     * Update grading criteria
     */
    @Transactional
    public GradingCriteriaResponse updateCriteria(Long criteriaId, GradingCriteriaRequest request) {
        log.info("Updating grading criteria: {}", criteriaId);

        GradingCriteria criteria = findCriteriaById(criteriaId);
        validateProfessorAccess(criteria.getCourse());

        criteria.setName(request.getName());
        criteria.setDescription(request.getDescription());
        criteria.setWeight(request.getWeight());
        criteria.setMaxScore(request.getMaxScore());
        if (request.getOrderIndex() != null) {
            criteria.setOrderIndex(request.getOrderIndex());
        }

        // Update rubric items
        if (request.getRubricItems() != null) {
            // Remove old items
            criteria.getRubricItems().clear();

            // Add new items
            for (RubricItemDto itemDto : request.getRubricItems()) {
                RubricItem item = RubricItem.builder()
                    .criteria(criteria)
                    .level(itemDto.getLevel())
                    .description(itemDto.getDescription())
                    .score(itemDto.getScore())
                    .orderIndex(itemDto.getOrderIndex() != null ? itemDto.getOrderIndex() : 0)
                    .build();
                criteria.addRubricItem(item);
            }
        }

        criteria = gradingCriteriaRepository.save(criteria);
        log.info("Grading criteria updated successfully: {}", criteriaId);

        return mapToResponse(criteria);
    }

    /**
     * Delete grading criteria (soft delete)
     */
    @Transactional
    public void deleteCriteria(Long criteriaId) {
        log.info("Deleting grading criteria: {}", criteriaId);

        GradingCriteria criteria = findCriteriaById(criteriaId);
        validateProfessorAccess(criteria.getCourse());

        criteria.delete();
        gradingCriteriaRepository.save(criteria);

        log.info("Grading criteria deleted successfully: {}", criteriaId);
    }

    /**
     * Get grading criteria by ID
     */
    @Transactional(readOnly = true)
    public GradingCriteriaResponse getCriteria(Long criteriaId) {
        GradingCriteria criteria = findCriteriaById(criteriaId);
        return mapToResponse(criteria);
    }

    /**
     * Get all grading criteria for a course
     */
    @Transactional(readOnly = true)
    public List<GradingCriteriaResponse> getCriteriaByCourse(Long courseId) {
        Course course = findCourseById(courseId);

        List<GradingCriteria> criteriaList = gradingCriteriaRepository.findByCourseOrderByOrderIndex(course);
        return criteriaList.stream()
            .map(this::mapToResponse)
            .collect(Collectors.toList());
    }

    // Helper methods

    private GradingCriteria findCriteriaById(Long criteriaId) {
        return gradingCriteriaRepository.findById(criteriaId)
            .filter(c -> !c.isDeleted())
            .orElseThrow(() -> new BusinessException(ErrorCode.ENTITY_NOT_FOUND, "평가 기준을 찾을 수 없습니다"));
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
            throw new BusinessException(ErrorCode.ACCESS_DENIED, "코스 교수만 접근할 수 있습니다");
        }
    }

    private GradingCriteriaResponse mapToResponse(GradingCriteria criteria) {
        List<RubricItemDto> rubricItemDtos = criteria.getRubricItems().stream()
            .filter(item -> !item.isDeleted())
            .map(item -> RubricItemDto.builder()
                .id(item.getId())
                .level(item.getLevel())
                .description(item.getDescription())
                .score(item.getScore())
                .orderIndex(item.getOrderIndex())
                .build())
            .collect(Collectors.toList());

        return GradingCriteriaResponse.builder()
            .id(criteria.getId())
            .courseId(criteria.getCourse().getId())
            .name(criteria.getName())
            .description(criteria.getDescription())
            .weight(criteria.getWeight())
            .maxScore(criteria.getMaxScore())
            .orderIndex(criteria.getOrderIndex())
            .rubricItems(rubricItemDtos)
            .createdAt(criteria.getCreatedAt())
            .updatedAt(criteria.getUpdatedAt())
            .build();
    }
}
