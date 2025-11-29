package com.eduforum.api.domain.file.repository;

import com.eduforum.api.domain.file.entity.FilePermission;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * FilePermission Repository
 */
@Repository
public interface FilePermissionRepository extends JpaRepository<FilePermission, Long> {

    /**
     * 파일의 모든 권한 조회
     */
    @Query("SELECT p FROM FilePermission p WHERE p.file.id = :fileId")
    List<FilePermission> findByFileId(@Param("fileId") Long fileId);

    /**
     * 사용자의 파일 권한 조회
     */
    @Query("SELECT p FROM FilePermission p WHERE p.file.id = :fileId AND p.user.id = :userId")
    Optional<FilePermission> findByFileIdAndUserId(@Param("fileId") Long fileId,
                                                    @Param("userId") Long userId);

    /**
     * 사용자의 모든 파일 권한 조회
     */
    @Query("SELECT p FROM FilePermission p WHERE p.user.id = :userId")
    List<FilePermission> findByUserId(@Param("userId") Long userId);

    /**
     * 특정 권한 타입을 가진 파일 조회
     */
    @Query("SELECT p FROM FilePermission p WHERE p.file.id = :fileId " +
           "AND p.permission = :permission")
    List<FilePermission> findByFileIdAndPermission(@Param("fileId") Long fileId,
                                                   @Param("permission") FilePermission.PermissionType permission);

    /**
     * 파일의 소유자 조회
     */
    @Query("SELECT p FROM FilePermission p WHERE p.file.id = :fileId " +
           "AND p.permission = 'OWNER'")
    Optional<FilePermission> findOwnerByFileId(@Param("fileId") Long fileId);

    /**
     * 권한 존재 여부 확인
     */
    @Query("SELECT COUNT(p) > 0 FROM FilePermission p WHERE p.file.id = :fileId " +
           "AND p.user.id = :userId")
    boolean existsByFileIdAndUserId(@Param("fileId") Long fileId, @Param("userId") Long userId);

    /**
     * 파일의 모든 권한 삭제
     */
    void deleteByFileId(Long fileId);

    /**
     * 사용자의 파일 권한 삭제
     */
    void deleteByFileIdAndUserId(Long fileId, Long userId);

    /**
     * 사용자가 접근 가능한 파일 ID 목록 조회
     */
    @Query("SELECT p.file.id FROM FilePermission p WHERE p.user.id = :userId")
    List<Long> findAccessibleFileIdsByUserId(@Param("userId") Long userId);

    /**
     * 특정 권한 이상을 가진 사용자 수
     */
    @Query("SELECT COUNT(DISTINCT p.user.id) FROM FilePermission p WHERE p.file.id = :fileId " +
           "AND p.permission IN ('WRITE', 'DELETE', 'OWNER')")
    Long countUsersWithWriteAccess(@Param("fileId") Long fileId);
}
