package com.eduforum.api.domain.file.service;

import com.eduforum.api.common.exception.BusinessException;
import com.eduforum.api.common.exception.ErrorCode;
import com.eduforum.api.common.storage.StorageService;
import com.eduforum.api.common.storage.dto.FileInfo;
import com.eduforum.api.common.storage.validator.FileValidator;
import com.eduforum.api.domain.auth.entity.User;
import com.eduforum.api.domain.auth.repository.UserRepository;
import com.eduforum.api.domain.course.entity.Course;
import com.eduforum.api.domain.course.repository.CourseRepository;
import com.eduforum.api.domain.file.dto.FileResponse;
import com.eduforum.api.domain.file.dto.FileUploadRequest;
import com.eduforum.api.domain.file.entity.FileFolder;
import com.eduforum.api.domain.file.entity.StoredFile;
import com.eduforum.api.domain.file.repository.FileFolderRepository;
import com.eduforum.api.domain.file.repository.StoredFileRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.Resource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.stream.Collectors;

/**
 * 파일 관리 서비스
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class FileService {

    private final StoredFileRepository fileRepository;
    private final FileFolderRepository folderRepository;
    private final CourseRepository courseRepository;
    private final UserRepository userRepository;
    private final StorageService storageService;
    private final FileValidator fileValidator;
    private final FilePermissionService permissionService;

    /**
     * 파일 업로드
     */
    @Transactional
    public FileResponse uploadFile(MultipartFile file, FileUploadRequest request) {
        log.info("Uploading file: {} to course: {}", file.getOriginalFilename(), request.getCourseId());

        // 파일 검증
        fileValidator.validate(file);

        // 엔티티 조회
        User currentUser = getCurrentUser();
        Course course = findCourseById(request.getCourseId());
        FileFolder folder = request.getFolderId() != null ?
            findFolderById(request.getFolderId()) : null;

        // 스토리지 경로 생성
        String storagePath = buildStoragePath(course, folder);

        // 파일 저장
        FileInfo fileInfo = storageService.upload(file, storagePath);

        // DB에 메타데이터 저장
        StoredFile storedFile = StoredFile.builder()
            .originalName(fileInfo.getOriginalName())
            .storedName(fileInfo.getStoredName())
            .path(fileInfo.getPath())
            .size(fileInfo.getSize())
            .mimeType(fileInfo.getMimeType())
            .extension(fileInfo.getExtension())
            .url(fileInfo.getUrl())
            .uploadedBy(currentUser)
            .course(course)
            .folder(folder)
            .description(request.getDescription())
            .isPublic(request.getIsPublic())
            .build();

        storedFile = fileRepository.save(storedFile);
        log.info("File uploaded successfully: ID={}", storedFile.getId());

        return FileResponse.from(storedFile);
    }

    /**
     * 파일 조회
     */
    @Transactional(readOnly = true)
    public FileResponse getFile(Long fileId) {
        StoredFile file = findFileById(fileId);
        validateFileAccess(file);
        return FileResponse.from(file);
    }

    /**
     * 파일 다운로드
     */
    @Transactional
    public Resource downloadFile(Long fileId) {
        StoredFile file = findFileById(fileId);
        validateFileAccess(file);

        // 다운로드 카운트 증가
        file.incrementDownloadCount();
        fileRepository.save(file);

        log.info("Downloading file: ID={}, name={}", fileId, file.getOriginalName());
        return storageService.download(file.getStoredName(), file.getPath());
    }

    /**
     * 파일 삭제
     */
    @Transactional
    public void deleteFile(Long fileId) {
        log.info("Deleting file: ID={}", fileId);

        StoredFile file = findFileById(fileId);
        User currentUser = getCurrentUser();

        // 권한 검증
        if (!permissionService.canDelete(file, currentUser)) {
            throw new BusinessException(ErrorCode.ACCESS_DENIED, "파일을 삭제할 권한이 없습니다");
        }

        // 스토리지에서 삭제
        try {
            storageService.delete(file.getStoredName(), file.getPath());
        } catch (Exception e) {
            log.warn("Failed to delete file from storage: {}", e.getMessage());
        }

        // Soft delete
        file.softDelete();
        fileRepository.save(file);

        log.info("File deleted successfully: ID={}", fileId);
    }

    /**
     * 코스별 파일 목록 조회
     */
    @Transactional(readOnly = true)
    public Page<FileResponse> getCourseFiles(Long courseId, Pageable pageable) {
        Course course = findCourseById(courseId);
        validateCourseAccess(course);

        return fileRepository.findByCourseId(courseId, pageable)
            .map(FileResponse::from);
    }

    /**
     * 폴더별 파일 목록 조회
     */
    @Transactional(readOnly = true)
    public List<FileResponse> getFolderFiles(Long folderId) {
        FileFolder folder = findFolderById(folderId);
        validateCourseAccess(folder.getCourse());

        return fileRepository.findByFolderId(folderId).stream()
            .map(FileResponse::from)
            .collect(Collectors.toList());
    }

    /**
     * 파일 공개 설정 변경
     */
    @Transactional
    public FileResponse updateFileVisibility(Long fileId, boolean isPublic) {
        StoredFile file = findFileById(fileId);
        User currentUser = getCurrentUser();

        if (!permissionService.canWrite(file, currentUser)) {
            throw new BusinessException(ErrorCode.ACCESS_DENIED, "파일을 수정할 권한이 없습니다");
        }

        file.setIsPublic(isPublic);
        file = fileRepository.save(file);

        log.info("File visibility updated: ID={}, isPublic={}", fileId, isPublic);
        return FileResponse.from(file);
    }

    /**
     * 파일 설명 수정
     */
    @Transactional
    public FileResponse updateFileDescription(Long fileId, String description) {
        StoredFile file = findFileById(fileId);
        User currentUser = getCurrentUser();

        if (!permissionService.canWrite(file, currentUser)) {
            throw new BusinessException(ErrorCode.ACCESS_DENIED, "파일을 수정할 권한이 없습니다");
        }

        file.setDescription(description);
        file = fileRepository.save(file);

        log.info("File description updated: ID={}", fileId);
        return FileResponse.from(file);
    }

    /**
     * Pre-signed URL 생성
     */
    @Transactional(readOnly = true)
    public String generateDownloadUrl(Long fileId, int expirationMinutes) {
        StoredFile file = findFileById(fileId);
        validateFileAccess(file);

        return storageService.generatePresignedUrl(file.getStoredName(), file.getPath(), expirationMinutes);
    }

    // Helper methods

    private StoredFile findFileById(Long fileId) {
        return fileRepository.findByIdAndIsDeletedFalse(fileId)
            .orElseThrow(() -> new BusinessException(ErrorCode.ENTITY_NOT_FOUND, "파일을 찾을 수 없습니다"));
    }

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

    private void validateFileAccess(StoredFile file) {
        User currentUser = getCurrentUser();

        // 공개 파일은 모두 접근 가능
        if (file.getIsPublic()) {
            return;
        }

        // 업로더는 항상 접근 가능
        if (file.getUploadedBy().getId().equals(currentUser.getId())) {
            return;
        }

        // 권한 확인
        if (!permissionService.canRead(file, currentUser)) {
            throw new BusinessException(ErrorCode.ACCESS_DENIED, "파일에 접근할 권한이 없습니다");
        }
    }

    private void validateCourseAccess(Course course) {
        User currentUser = getCurrentUser();

        // 코스 교수는 항상 접근 가능
        if (course.getProfessor().getId().equals(currentUser.getId())) {
            return;
        }

        // TODO: 수강 여부 확인 (EnrollmentRepository 사용)
        // 임시로 모든 사용자 접근 허용
    }

    private String buildStoragePath(Course course, FileFolder folder) {
        StringBuilder path = new StringBuilder();
        path.append("courses/").append(course.getId());

        if (folder != null) {
            path.append("/folders/").append(folder.getId());
        } else {
            path.append("/root");
        }

        return path.toString();
    }
}
