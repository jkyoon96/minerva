package com.eduforum.api.domain.file.service;

import com.eduforum.api.common.exception.BusinessException;
import com.eduforum.api.common.exception.ErrorCode;
import com.eduforum.api.domain.auth.entity.User;
import com.eduforum.api.domain.auth.repository.UserRepository;
import com.eduforum.api.domain.course.entity.Course;
import com.eduforum.api.domain.course.repository.CourseRepository;
import com.eduforum.api.domain.file.dto.FolderRequest;
import com.eduforum.api.domain.file.dto.FolderResponse;
import com.eduforum.api.domain.file.entity.FileFolder;
import com.eduforum.api.domain.file.repository.FileFolderRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 폴더 관리 서비스
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class FolderService {

    private final FileFolderRepository folderRepository;
    private final CourseRepository courseRepository;
    private final UserRepository userRepository;

    /**
     * 폴더 생성
     */
    @Transactional
    public FolderResponse createFolder(FolderRequest request) {
        log.info("Creating folder: {} in course: {}", request.getName(), request.getCourseId());

        User currentUser = getCurrentUser();
        Course course = findCourseById(request.getCourseId());
        FileFolder parent = request.getParentId() != null ?
            findFolderById(request.getParentId()) : null;

        // 중복 확인
        if (parent != null) {
            if (folderRepository.existsByNameAndParentId(course.getId(), parent.getId(), request.getName())) {
                throw new BusinessException(ErrorCode.INVALID_INPUT_VALUE, "같은 이름의 폴더가 이미 존재합니다");
            }
        } else {
            if (folderRepository.existsRootFolderByName(course.getId(), request.getName())) {
                throw new BusinessException(ErrorCode.INVALID_INPUT_VALUE, "같은 이름의 폴더가 이미 존재합니다");
            }
        }

        FileFolder folder = FileFolder.builder()
            .name(request.getName())
            .description(request.getDescription())
            .parent(parent)
            .course(course)
            .createdByUser(currentUser)
            .isPublic(request.getIsPublic())
            .sortOrder(request.getSortOrder())
            .build();

        folder = folderRepository.save(folder);
        log.info("Folder created successfully: ID={}", folder.getId());

        return FolderResponse.from(folder);
    }

    /**
     * 폴더 조회
     */
    @Transactional(readOnly = true)
    public FolderResponse getFolder(Long folderId, boolean includeChildren, boolean includeFiles) {
        FileFolder folder = findFolderById(folderId);
        validateCourseAccess(folder.getCourse());

        return FolderResponse.from(folder, includeChildren, includeFiles);
    }

    /**
     * 폴더 수정
     */
    @Transactional
    public FolderResponse updateFolder(Long folderId, FolderRequest request) {
        log.info("Updating folder: ID={}", folderId);

        FileFolder folder = findFolderById(folderId);
        User currentUser = getCurrentUser();

        // 권한 확인
        if (!folder.getCreatedByUser().getId().equals(currentUser.getId()) &&
            !folder.getCourse().getProfessor().getId().equals(currentUser.getId())) {
            throw new BusinessException(ErrorCode.ACCESS_DENIED, "폴더를 수정할 권한이 없습니다");
        }

        if (request.getName() != null) {
            folder.setName(request.getName());
        }
        if (request.getDescription() != null) {
            folder.setDescription(request.getDescription());
        }
        if (request.getIsPublic() != null) {
            folder.setIsPublic(request.getIsPublic());
        }
        if (request.getSortOrder() != null) {
            folder.setSortOrder(request.getSortOrder());
        }

        folder = folderRepository.save(folder);
        log.info("Folder updated successfully: ID={}", folderId);

        return FolderResponse.from(folder);
    }

    /**
     * 폴더 삭제
     */
    @Transactional
    public void deleteFolder(Long folderId) {
        log.info("Deleting folder: ID={}", folderId);

        FileFolder folder = findFolderById(folderId);
        User currentUser = getCurrentUser();

        // 권한 확인
        if (!folder.getCreatedByUser().getId().equals(currentUser.getId()) &&
            !folder.getCourse().getProfessor().getId().equals(currentUser.getId())) {
            throw new BusinessException(ErrorCode.ACCESS_DENIED, "폴더를 삭제할 권한이 없습니다");
        }

        // Soft delete (하위 폴더/파일도 함께 삭제)
        folder.softDelete();
        folderRepository.save(folder);

        log.info("Folder deleted successfully: ID={}", folderId);
    }

    /**
     * 코스의 폴더 트리 조회
     */
    @Transactional(readOnly = true)
    public List<FolderResponse> getCourseFolderTree(Long courseId) {
        Course course = findCourseById(courseId);
        validateCourseAccess(course);

        List<FileFolder> allFolders = folderRepository.findAllByCourseId(courseId);
        return buildFolderTree(allFolders);
    }

    /**
     * 최상위 폴더 목록 조회
     */
    @Transactional(readOnly = true)
    public List<FolderResponse> getRootFolders(Long courseId) {
        Course course = findCourseById(courseId);
        validateCourseAccess(course);

        return folderRepository.findRootFoldersByCourseId(courseId).stream()
            .map(FolderResponse::from)
            .collect(Collectors.toList());
    }

    /**
     * 하위 폴더 목록 조회
     */
    @Transactional(readOnly = true)
    public List<FolderResponse> getSubFolders(Long folderId) {
        FileFolder folder = findFolderById(folderId);
        validateCourseAccess(folder.getCourse());

        return folderRepository.findByParentId(folderId).stream()
            .map(FolderResponse::from)
            .collect(Collectors.toList());
    }

    /**
     * 폴더 검색
     */
    @Transactional(readOnly = true)
    public List<FolderResponse> searchFolders(Long courseId, String keyword) {
        Course course = findCourseById(courseId);
        validateCourseAccess(course);

        return folderRepository.searchByName(courseId, keyword).stream()
            .map(FolderResponse::from)
            .collect(Collectors.toList());
    }

    // Helper methods

    private FileFolder findFolderById(Long folderId) {
        return folderRepository.findByIdAndIsDeletedFalse(folderId)
            .orElseThrow(() -> new BusinessException(ErrorCode.ENTITY_NOT_FOUND, "폴더를 찾을 수 없습니다"));
    }

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

    /**
     * 폴더 트리 구조 생성
     */
    private List<FolderResponse> buildFolderTree(List<FileFolder> allFolders) {
        Map<Long, FolderResponse> folderMap = new HashMap<>();
        List<FolderResponse> rootFolders = new ArrayList<>();

        // 모든 폴더를 FolderResponse로 변환
        for (FileFolder folder : allFolders) {
            FolderResponse response = FolderResponse.from(folder, false, false);
            folderMap.put(folder.getId(), response);
        }

        // 트리 구조 생성
        for (FileFolder folder : allFolders) {
            FolderResponse response = folderMap.get(folder.getId());

            if (folder.getParent() == null) {
                // 최상위 폴더
                rootFolders.add(response);
            } else {
                // 하위 폴더
                FolderResponse parent = folderMap.get(folder.getParent().getId());
                if (parent != null) {
                    if (parent.getChildren() == null) {
                        parent = FolderResponse.builder()
                            .id(parent.getId())
                            .name(parent.getName())
                            .description(parent.getDescription())
                            .courseId(parent.getCourseId())
                            .parentId(parent.getParentId())
                            .parentName(parent.getParentName())
                            .fullPath(parent.getFullPath())
                            .depth(parent.getDepth())
                            .isPublic(parent.getIsPublic())
                            .sortOrder(parent.getSortOrder())
                            .childFolderCount(parent.getChildFolderCount())
                            .fileCount(parent.getFileCount())
                            .children(new ArrayList<>())
                            .createdById(parent.getCreatedById())
                            .createdByName(parent.getCreatedByName())
                            .createdAt(parent.getCreatedAt())
                            .updatedAt(parent.getUpdatedAt())
                            .build();
                        folderMap.put(parent.getId(), parent);
                    }
                    parent.getChildren().add(response);
                }
            }
        }

        return rootFolders;
    }
}
