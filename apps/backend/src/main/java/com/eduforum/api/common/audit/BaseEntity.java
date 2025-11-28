package com.eduforum.api.common.audit;

import jakarta.persistence.Column;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.MappedSuperclass;
import lombok.Getter;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedBy;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;

/**
 * Auditing 기본 엔티티
 *
 * 모든 엔티티가 상속받아 사용할 수 있는 공통 Audit 필드 제공
 * - createdAt: 생성 일시 (자동)
 * - updatedAt: 수정 일시 (자동)
 * - createdBy: 생성자 (자동)
 * - updatedBy: 수정자 (자동)
 *
 * 사용 예시:
 * @Entity
 * public class User extends BaseEntity {
 *     // 엔티티 필드들...
 * }
 */
@Getter
@MappedSuperclass
@EntityListeners(AuditingEntityListener.class)
public abstract class BaseEntity {

    /**
     * 생성 일시
     */
    @CreatedDate
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    /**
     * 수정 일시
     */
    @LastModifiedDate
    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    /**
     * 생성자 (사용자 ID)
     */
    @CreatedBy
    @Column(name = "created_by", nullable = false, updatable = false, length = 100)
    private String createdBy;

    /**
     * 수정자 (사용자 ID)
     */
    @LastModifiedBy
    @Column(name = "updated_by", nullable = false, length = 100)
    private String updatedBy;
}
