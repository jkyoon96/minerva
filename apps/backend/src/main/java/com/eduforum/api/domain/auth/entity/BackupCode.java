package com.eduforum.api.domain.auth.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.OffsetDateTime;

/**
 * BackupCode entity
 * Stores backup codes for two-factor authentication recovery
 */
@Entity
@Table(schema = "auth", name = "backup_codes")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BackupCode {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(name = "code_hash", nullable = false, length = 255)
    private String codeHash;

    @Column(name = "is_used", nullable = false)
    @Builder.Default
    private Boolean isUsed = false;

    @Column(name = "used_at")
    private OffsetDateTime usedAt;

    @Column(name = "created_at", nullable = false, updatable = false)
    private OffsetDateTime createdAt;

    @PrePersist
    protected void onCreate() {
        createdAt = OffsetDateTime.now();
    }

    /**
     * Mark this backup code as used
     */
    public void markAsUsed() {
        this.isUsed = true;
        this.usedAt = OffsetDateTime.now();
    }

    /**
     * Check if this backup code has been used
     */
    public boolean isUsed() {
        return Boolean.TRUE.equals(isUsed);
    }
}
