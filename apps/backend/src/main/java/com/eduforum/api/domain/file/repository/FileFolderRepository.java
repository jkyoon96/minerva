package com.eduforum.api.domain.file.repository;

import com.eduforum.api.domain.file.entity.FileFolder;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * FileFolder Repository
 */
@Repository
public interface FileFolderRepository extends JpaRepository<FileFolder, Long> {

    /**
     * 폴더 ID로 삭제되지 않은 폴더 조회
     */
    Optional<FileFolder> findByIdAndIsDeletedFalse(Long id);

    /**
     * 코스의 최상위 폴더 목록 조회
     */
    @Query("SELECT f FROM FileFolder f WHERE f.course.id = :courseId " +
           "AND f.parent IS NULL AND f.isDeleted = false " +
           "ORDER BY f.sortOrder, f.name")
    List<FileFolder> findRootFoldersByCourseId(@Param("courseId") Long courseId);

    /**
     * 부모 폴더의 하위 폴더 목록
     */
    @Query("SELECT f FROM FileFolder f WHERE f.parent.id = :parentId " +
           "AND f.isDeleted = false ORDER BY f.sortOrder, f.name")
    List<FileFolder> findByParentId(@Param("parentId") Long parentId);

    /**
     * 코스의 모든 폴더 조회 (트리 구조 조회용)
     */
    @Query("SELECT f FROM FileFolder f WHERE f.course.id = :courseId " +
           "AND f.isDeleted = false ORDER BY f.id")
    List<FileFolder> findAllByCourseId(@Param("courseId") Long courseId);

    /**
     * 폴더명으로 검색
     */
    @Query("SELECT f FROM FileFolder f WHERE f.course.id = :courseId " +
           "AND LOWER(f.name) LIKE LOWER(CONCAT('%', :keyword, '%')) " +
           "AND f.isDeleted = false")
    List<FileFolder> searchByName(@Param("courseId") Long courseId, @Param("keyword") String keyword);

    /**
     * 코스 내 폴더명 중복 확인 (같은 부모 폴더 내)
     */
    @Query("SELECT COUNT(f) > 0 FROM FileFolder f WHERE f.course.id = :courseId " +
           "AND f.parent.id = :parentId AND f.name = :name AND f.isDeleted = false")
    boolean existsByNameAndParentId(@Param("courseId") Long courseId,
                                   @Param("parentId") Long parentId,
                                   @Param("name") String name);

    /**
     * 최상위 폴더 중복 확인
     */
    @Query("SELECT COUNT(f) > 0 FROM FileFolder f WHERE f.course.id = :courseId " +
           "AND f.parent IS NULL AND f.name = :name AND f.isDeleted = false")
    boolean existsRootFolderByName(@Param("courseId") Long courseId, @Param("name") String name);

    /**
     * 사용자가 생성한 폴더 목록
     */
    @Query("SELECT f FROM FileFolder f WHERE f.createdByUser.id = :userId " +
           "AND f.isDeleted = false")
    List<FileFolder> findByCreatedByUserId(@Param("userId") Long userId);

    /**
     * 공개 폴더 조회
     */
    @Query("SELECT f FROM FileFolder f WHERE f.course.id = :courseId " +
           "AND f.isPublic = true AND f.isDeleted = false")
    List<FileFolder> findPublicFoldersByCourseId(@Param("courseId") Long courseId);
}
