package com.eduforum.api.domain.auth.entity;

import com.eduforum.api.domain.common.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * TwoFactorAuth entity (maps to auth.two_factor_auth table)
 */
@Entity
@Table(schema = "auth", name = "two_factor_auth")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TwoFactorAuth extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false, unique = true)
    private User user;

    @Column(nullable = false, length = 255)
    private String secret;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "backup_codes", nullable = false, columnDefinition = "jsonb")
    @Builder.Default
    private List<String> backupCodes = new ArrayList<>();

    @Column(name = "is_enabled", nullable = false)
    @Builder.Default
    private Boolean isEnabled = false;

    @Column(name = "verified_at")
    private OffsetDateTime verifiedAt;

    // Helper methods
    public void enable() {
        this.isEnabled = true;
        this.verifiedAt = OffsetDateTime.now();
    }

    public void disable() {
        this.isEnabled = false;
    }
}
