package com.eduforum.api.domain.course.entity;

import com.eduforum.api.domain.auth.entity.User;
import com.eduforum.api.domain.common.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.*;

import java.time.OffsetDateTime;

/**
 * Enrollment entity (maps to course.enrollments table)
 */
@Entity
@Table(schema = "course", name = "enrollments",
    uniqueConstraints = @UniqueConstraint(columnNames = {"user_id", "course_id"}))
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Enrollment extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "course_id", nullable = false)
    private Course course;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, columnDefinition = "enrollment_role")
    @Builder.Default
    private EnrollmentRole role = EnrollmentRole.STUDENT;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, columnDefinition = "enrollment_status")
    @Builder.Default
    private EnrollmentStatus status = EnrollmentStatus.ACTIVE;

    @Column(name = "joined_at", nullable = false)
    private OffsetDateTime joinedAt;

    @PrePersist
    protected void onCreate() {
        if (joinedAt == null) {
            joinedAt = OffsetDateTime.now();
        }
    }

    // Helper methods
    public void drop() {
        this.status = EnrollmentStatus.DROPPED;
    }

    public void complete() {
        this.status = EnrollmentStatus.COMPLETED;
    }

    public void reactivate() {
        this.status = EnrollmentStatus.ACTIVE;
    }

    public boolean isActive() {
        return status == EnrollmentStatus.ACTIVE;
    }
}
