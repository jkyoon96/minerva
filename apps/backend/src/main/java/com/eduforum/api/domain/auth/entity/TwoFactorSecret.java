package com.eduforum.api.domain.auth.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.OffsetDateTime;

/**
 * TwoFactorSecret entity
 * Stores TOTP secret for two-factor authentication
 */
@Entity
@Table(schema = "auth", name = "two_factor_secrets")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TwoFactorSecret {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false, unique = true)
    private User user;

    @Column(nullable = false, length = 255)
    private String secret;

    @Column(name = "is_enabled", nullable = false)
    @Builder.Default
    private Boolean isEnabled = false;

    @Column(name = "enabled_at")
    private OffsetDateTime enabledAt;

    @Column(name = "created_at", nullable = false, updatable = false)
    private OffsetDateTime createdAt;

    @Column(name = "updated_at")
    private OffsetDateTime updatedAt;

    @PrePersist
    protected void onCreate() {
        createdAt = OffsetDateTime.now();
        updatedAt = OffsetDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = OffsetDateTime.now();
    }

    /**
     * Enable 2FA for this user
     */
    public void enable() {
        this.isEnabled = true;
        this.enabledAt = OffsetDateTime.now();
    }

    /**
     * Disable 2FA for this user
     */
    public void disable() {
        this.isEnabled = false;
        this.enabledAt = null;
    }

    /**
     * Check if 2FA is enabled
     */
    public boolean isEnabled() {
        return Boolean.TRUE.equals(isEnabled);
    }
}
