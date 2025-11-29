package com.eduforum.api.domain.file.repository;

import com.eduforum.api.domain.file.entity.StoredFile;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * StoredFile Repository
 */
@Repository
public interface StoredFileRepository extends JpaRepository<StoredFile, Long> {

    /**
     * 파일 ID로 삭제되지 않은 파일 조회
     */
    Optional<StoredFile> findByIdAndIsDeletedFalse(Long id);

    /**
     * 코스별 파일 목록 조회
     */
    @Query("SELECT f FROM StoredFile f WHERE f.course.id = :courseId AND f.isDeleted = false")
    Page<StoredFile> findByCourseId(@Param("courseId") Long courseId, Pageable pageable);

    /**
     * 폴더별 파일 목록 조회
     */
    @Query("SELECT f FROM StoredFile f WHERE f.folder.id = :folderId AND f.isDeleted = false")
    List<StoredFile> findByFolderId(@Param("folderId") Long folderId);

    /**
     * 코스에서 폴더가 없는 최상위 파일 목록
     */
    @Query("SELECT f FROM StoredFile f WHERE f.course.id = :courseId AND f.folder IS NULL AND f.isDeleted = false")
    List<StoredFile> findRootFilesByCourseId(@Param("courseId") Long courseId);

    /**
     * 사용자가 업로드한 파일 목록
     */
    @Query("SELECT f FROM StoredFile f WHERE f.uploadedBy.id = :userId AND f.isDeleted = false")
    Page<StoredFile> findByUploadedById(@Param("userId") Long userId, Pageable pageable);

    /**
     * 파일명으로 검색
     */
    @Query("SELECT f FROM StoredFile f WHERE f.course.id = :courseId " +
           "AND LOWER(f.originalName) LIKE LOWER(CONCAT('%', :keyword, '%')) " +
           "AND f.isDeleted = false")
    Page<StoredFile> searchByFileName(@Param("courseId") Long courseId,
                                      @Param("keyword") String keyword,
                                      Pageable pageable);

    /**
     * MIME 타입으로 필터링
     */
    @Query("SELECT f FROM StoredFile f WHERE f.course.id = :courseId " +
           "AND f.mimeType LIKE :mimeTypePattern " +
           "AND f.isDeleted = false")
    Page<StoredFile> findByMimeType(@Param("courseId") Long courseId,
                                    @Param("mimeTypePattern") String mimeTypePattern,
                                    Pageable pageable);

    /**
     * 공개 파일 조회
     */
    @Query("SELECT f FROM StoredFile f WHERE f.course.id = :courseId " +
           "AND f.isPublic = true AND f.isDeleted = false")
    List<StoredFile> findPublicFilesByCourseId(@Param("courseId") Long courseId);

    /**
     * 코스의 전체 파일 크기 합계
     */
    @Query("SELECT COALESCE(SUM(f.size), 0) FROM StoredFile f " +
           "WHERE f.course.id = :courseId AND f.isDeleted = false")
    Long calculateTotalSizeByCourseId(@Param("courseId") Long courseId);

    /**
     * 사용자별 전체 파일 크기 합계
     */
    @Query("SELECT COALESCE(SUM(f.size), 0) FROM StoredFile f " +
           "WHERE f.uploadedBy.id = :userId AND f.isDeleted = false")
    Long calculateTotalSizeByUserId(@Param("userId") Long userId);

    /**
     * 복합 검색 (파일명, 설명)
     */
    @Query("SELECT f FROM StoredFile f WHERE f.course.id = :courseId " +
           "AND (LOWER(f.originalName) LIKE LOWER(CONCAT('%', :keyword, '%')) " +
           "OR LOWER(f.description) LIKE LOWER(CONCAT('%', :keyword, '%'))) " +
           "AND f.isDeleted = false")
    Page<StoredFile> searchFiles(@Param("courseId") Long courseId,
                                 @Param("keyword") String keyword,
                                 Pageable pageable);
}
