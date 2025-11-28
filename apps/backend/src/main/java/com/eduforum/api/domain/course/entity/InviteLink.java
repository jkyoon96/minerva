package com.eduforum.api.domain.course.entity;

import com.eduforum.api.domain.common.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.*;

import java.time.OffsetDateTime;

/**
 * InviteLink entity (maps to course.invite_links table)
 */
@Entity
@Table(schema = "course", name = "invite_links")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class InviteLink extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "course_id", nullable = false)
    private Course course;

    @Column(nullable = false, unique = true, length = 50)
    private String code;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, columnDefinition = "enrollment_role")
    @Builder.Default
    private EnrollmentRole role = EnrollmentRole.STUDENT;

    @Column(name = "max_uses")
    private Integer maxUses;

    @Column(name = "used_count", nullable = false)
    @Builder.Default
    private Integer usedCount = 0;

    @Column(name = "expires_at")
    private OffsetDateTime expiresAt;

    @Column(name = "is_active", nullable = false)
    @Builder.Default
    private Boolean isActive = true;

    // Helper methods
    public boolean isExpired() {
        return expiresAt != null && OffsetDateTime.now().isAfter(expiresAt);
    }

    public boolean canBeUsed() {
        if (!isActive || isExpired()) {
            return false;
        }
        if (maxUses != null && usedCount >= maxUses) {
            return false;
        }
        return true;
    }

    public void incrementUsage() {
        this.usedCount++;
    }

    public void deactivate() {
        this.isActive = false;
    }

    public void activate() {
        this.isActive = true;
    }
}
