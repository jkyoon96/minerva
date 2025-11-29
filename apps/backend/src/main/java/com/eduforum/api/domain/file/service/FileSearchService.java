package com.eduforum.api.domain.file.service;

import com.eduforum.api.common.exception.BusinessException;
import com.eduforum.api.common.exception.ErrorCode;
import com.eduforum.api.domain.auth.entity.User;
import com.eduforum.api.domain.auth.repository.UserRepository;
import com.eduforum.api.domain.course.entity.Course;
import com.eduforum.api.domain.course.repository.CourseRepository;
import com.eduforum.api.domain.file.dto.FileResponse;
import com.eduforum.api.domain.file.dto.FileSearchRequest;
import com.eduforum.api.domain.file.repository.StoredFileRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

/**
 * 파일 검색 서비스
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class FileSearchService {

    private final StoredFileRepository fileRepository;
    private final CourseRepository courseRepository;
    private final UserRepository userRepository;

    /**
     * 파일 검색
     */
    @Transactional(readOnly = true)
    public Page<FileResponse> searchFiles(Long courseId, FileSearchRequest searchRequest) {
        log.info("Searching files in course: {} with keyword: {}", courseId, searchRequest.getKeyword());

        Course course = findCourseById(courseId);
        validateCourseAccess(course);

        // 페이징 및 정렬 설정
        PageRequest pageRequest = buildPageRequest(searchRequest);

        // 키워드 검색
        if (StringUtils.hasText(searchRequest.getKeyword())) {
            return fileRepository.searchFiles(courseId, searchRequest.getKeyword(), pageRequest)
                .map(FileResponse::from);
        }

        // MIME 타입 필터
        if (StringUtils.hasText(searchRequest.getMimeType())) {
            String mimeTypePattern = searchRequest.getMimeType().replace("*", "%");
            return fileRepository.findByMimeType(courseId, mimeTypePattern, pageRequest)
                .map(FileResponse::from);
        }

        // 기본: 코스의 모든 파일
        return fileRepository.findByCourseId(courseId, pageRequest)
            .map(FileResponse::from);
    }

    /**
     * 파일명으로 검색
     */
    @Transactional(readOnly = true)
    public Page<FileResponse> searchByFileName(Long courseId, String keyword, FileSearchRequest searchRequest) {
        Course course = findCourseById(courseId);
        validateCourseAccess(course);

        PageRequest pageRequest = buildPageRequest(searchRequest);
        return fileRepository.searchByFileName(courseId, keyword, pageRequest)
            .map(FileResponse::from);
    }

    /**
     * 공개 파일 조회
     */
    @Transactional(readOnly = true)
    public Page<FileResponse> searchPublicFiles(Long courseId, FileSearchRequest searchRequest) {
        Course course = findCourseById(courseId);
        validateCourseAccess(course);

        // TODO: 공개 파일 검색 구현
        PageRequest pageRequest = buildPageRequest(searchRequest);
        return fileRepository.findByCourseId(courseId, pageRequest)
            .map(FileResponse::from);
    }

    // Helper methods

    private Course findCourseById(Long courseId) {
        return courseRepository.findById(courseId)
            .orElseThrow(() -> new BusinessException(ErrorCode.ENTITY_NOT_FOUND, "코스를 찾을 수 없습니다"));
    }

    private User getCurrentUser() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String email = auth.getName();
        return userRepository.findByEmail(email)
            .orElseThrow(() -> new BusinessException(ErrorCode.USER_NOT_FOUND));
    }

    private void validateCourseAccess(Course course) {
        User currentUser = getCurrentUser();

        // 코스 교수는 항상 접근 가능
        if (course.getProfessor().getId().equals(currentUser.getId())) {
            return;
        }

        // TODO: 수강 여부 확인
    }

    private PageRequest buildPageRequest(FileSearchRequest searchRequest) {
        // 정렬 방향
        Sort.Direction direction = "asc".equalsIgnoreCase(searchRequest.getSortDirection())
            ? Sort.Direction.ASC
            : Sort.Direction.DESC;

        // 정렬 필드
        String sortField = switch (searchRequest.getSortBy()) {
            case "name" -> "originalName";
            case "size" -> "size";
            case "createdAt" -> "createdAt";
            default -> "createdAt";
        };

        return PageRequest.of(
            searchRequest.getPage(),
            searchRequest.getSize(),
            Sort.by(direction, sortField)
        );
    }
}
