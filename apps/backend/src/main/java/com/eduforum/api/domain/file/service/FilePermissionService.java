package com.eduforum.api.domain.file.service;

import com.eduforum.api.common.exception.BusinessException;
import com.eduforum.api.common.exception.ErrorCode;
import com.eduforum.api.domain.auth.entity.User;
import com.eduforum.api.domain.auth.repository.UserRepository;
import com.eduforum.api.domain.file.dto.PermissionRequest;
import com.eduforum.api.domain.file.dto.PermissionResponse;
import com.eduforum.api.domain.file.entity.FilePermission;
import com.eduforum.api.domain.file.entity.StoredFile;
import com.eduforum.api.domain.file.repository.FilePermissionRepository;
import com.eduforum.api.domain.file.repository.StoredFileRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

/**
 * 파일 권한 관리 서비스
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class FilePermissionService {

    private final FilePermissionRepository permissionRepository;
    private final StoredFileRepository fileRepository;
    private final UserRepository userRepository;

    /**
     * 파일 권한 부여
     */
    @Transactional
    public PermissionResponse grantPermission(Long fileId, PermissionRequest request) {
        log.info("Granting permission for file: {} to user: {}", fileId, request.getUserId());

        StoredFile file = findFileById(fileId);
        User user = findUserById(request.getUserId());
        User grantor = getCurrentUser();

        // 권한 부여자가 해당 파일의 소유자인지 확인
        if (!isOwner(file, grantor)) {
            throw new BusinessException(ErrorCode.ACCESS_DENIED, "권한을 부여할 수 있는 권한이 없습니다");
        }

        // 기존 권한 확인
        FilePermission existing = permissionRepository.findByFileIdAndUserId(fileId, request.getUserId())
            .orElse(null);

        FilePermission permission;
        if (existing != null) {
            // 권한 업데이트
            existing.setPermission(request.getPermission());
            permission = permissionRepository.save(existing);
        } else {
            // 새 권한 생성
            permission = FilePermission.builder()
                .file(file)
                .user(user)
                .permission(request.getPermission())
                .grantedBy(grantor)
                .build();
            permission = permissionRepository.save(permission);
        }

        log.info("Permission granted successfully: ID={}", permission.getId());
        return PermissionResponse.from(permission);
    }

    /**
     * 파일 권한 제거
     */
    @Transactional
    public void revokePermission(Long fileId, Long userId) {
        log.info("Revoking permission for file: {} from user: {}", fileId, userId);

        StoredFile file = findFileById(fileId);
        User grantor = getCurrentUser();

        // 권한 부여자가 해당 파일의 소유자인지 확인
        if (!isOwner(file, grantor)) {
            throw new BusinessException(ErrorCode.ACCESS_DENIED, "권한을 제거할 수 있는 권한이 없습니다");
        }

        permissionRepository.deleteByFileIdAndUserId(fileId, userId);
        log.info("Permission revoked successfully");
    }

    /**
     * 파일의 모든 권한 조회
     */
    @Transactional(readOnly = true)
    public List<PermissionResponse> getFilePermissions(Long fileId) {
        StoredFile file = findFileById(fileId);
        User currentUser = getCurrentUser();

        // 소유자만 권한 목록 조회 가능
        if (!isOwner(file, currentUser)) {
            throw new BusinessException(ErrorCode.ACCESS_DENIED, "권한 목록을 조회할 수 있는 권한이 없습니다");
        }

        return permissionRepository.findByFileId(fileId).stream()
            .map(PermissionResponse::from)
            .collect(Collectors.toList());
    }

    /**
     * 사용자의 파일 권한 조회
     */
    @Transactional(readOnly = true)
    public PermissionResponse getUserPermission(Long fileId, Long userId) {
        FilePermission permission = permissionRepository.findByFileIdAndUserId(fileId, userId)
            .orElseThrow(() -> new BusinessException(ErrorCode.ENTITY_NOT_FOUND, "권한을 찾을 수 없습니다"));

        return PermissionResponse.from(permission);
    }

    /**
     * 읽기 권한 확인
     */
    public boolean canRead(StoredFile file, User user) {
        // 공개 파일은 모두 읽기 가능
        if (file.getIsPublic()) {
            return true;
        }

        // 업로더는 항상 읽기 가능
        if (file.getUploadedBy().getId().equals(user.getId())) {
            return true;
        }

        // 코스 교수는 모든 파일 읽기 가능
        if (file.getCourse().getProfessor().getId().equals(user.getId())) {
            return true;
        }

        // 권한 확인
        return permissionRepository.findByFileIdAndUserId(file.getId(), user.getId())
            .map(FilePermission::canRead)
            .orElse(false);
    }

    /**
     * 쓰기 권한 확인
     */
    public boolean canWrite(StoredFile file, User user) {
        // 업로더는 항상 쓰기 가능
        if (file.getUploadedBy().getId().equals(user.getId())) {
            return true;
        }

        // 코스 교수는 모든 파일 쓰기 가능
        if (file.getCourse().getProfessor().getId().equals(user.getId())) {
            return true;
        }

        // 권한 확인
        return permissionRepository.findByFileIdAndUserId(file.getId(), user.getId())
            .map(FilePermission::canWrite)
            .orElse(false);
    }

    /**
     * 삭제 권한 확인
     */
    public boolean canDelete(StoredFile file, User user) {
        // 업로더는 항상 삭제 가능
        if (file.getUploadedBy().getId().equals(user.getId())) {
            return true;
        }

        // 코스 교수는 모든 파일 삭제 가능
        if (file.getCourse().getProfessor().getId().equals(user.getId())) {
            return true;
        }

        // 권한 확인
        return permissionRepository.findByFileIdAndUserId(file.getId(), user.getId())
            .map(FilePermission::canDelete)
            .orElse(false);
    }

    /**
     * 소유자 여부 확인
     */
    private boolean isOwner(StoredFile file, User user) {
        // 업로더는 소유자
        if (file.getUploadedBy().getId().equals(user.getId())) {
            return true;
        }

        // 코스 교수는 소유자
        if (file.getCourse().getProfessor().getId().equals(user.getId())) {
            return true;
        }

        // OWNER 권한 확인
        return permissionRepository.findByFileIdAndUserId(file.getId(), user.getId())
            .map(FilePermission::isOwner)
            .orElse(false);
    }

    // Helper methods

    private StoredFile findFileById(Long fileId) {
        return fileRepository.findByIdAndIsDeletedFalse(fileId)
            .orElseThrow(() -> new BusinessException(ErrorCode.ENTITY_NOT_FOUND, "파일을 찾을 수 없습니다"));
    }

    private User findUserById(Long userId) {
        return userRepository.findById(userId)
            .orElseThrow(() -> new BusinessException(ErrorCode.USER_NOT_FOUND));
    }

    private User getCurrentUser() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String email = auth.getName();
        return userRepository.findByEmail(email)
            .orElseThrow(() -> new BusinessException(ErrorCode.USER_NOT_FOUND));
    }
}
