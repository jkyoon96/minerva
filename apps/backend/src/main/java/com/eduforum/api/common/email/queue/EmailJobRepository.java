package com.eduforum.api.common.email.queue;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 이메일 작업 리포지토리
 */
@Repository
public interface EmailJobRepository extends JpaRepository<EmailJob, Long> {

    /**
     * 발송 대기 중인 작업 조회 (예약 시간이 현재보다 이전이거나 null인 작업)
     */
    @Query("SELECT e FROM EmailJob e WHERE e.status IN ('PENDING', 'RETRYING') " +
            "AND (e.scheduledAt IS NULL OR e.scheduledAt <= :now) " +
            "ORDER BY e.createdAt ASC")
    List<EmailJob> findPendingJobs(@Param("now") LocalDateTime now, Pageable pageable);

    /**
     * 상태별 작업 조회
     */
    Page<EmailJob> findByStatus(EmailJobStatus status, Pageable pageable);

    /**
     * 수신자별 작업 조회
     */
    Page<EmailJob> findByRecipientEmail(String recipientEmail, Pageable pageable);

    /**
     * 실패한 작업 중 재시도 가능한 작업 조회
     */
    @Query("SELECT e FROM EmailJob e WHERE e.status = 'FAILED' " +
            "AND e.retryCount < e.maxRetries " +
            "ORDER BY e.createdAt ASC")
    List<EmailJob> findRetryableFailedJobs(Pageable pageable);

    /**
     * 오래된 완료/실패 작업 삭제용 조회
     */
    @Query("SELECT e FROM EmailJob e WHERE e.status IN ('SENT', 'FAILED') " +
            "AND e.createdAt < :before")
    List<EmailJob> findOldCompletedJobs(@Param("before") LocalDateTime before);

    /**
     * 상태별 작업 개수
     */
    long countByStatus(EmailJobStatus status);
}
