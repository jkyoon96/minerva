package com.eduforum.api.domain.course.entity;

import com.eduforum.api.domain.auth.entity.User;
import com.eduforum.api.domain.common.entity.BaseEntity;
import com.vladmihalcea.hibernate.type.json.JsonBinaryType;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.Type;

import java.time.OffsetDateTime;
import java.util.HashMap;
import java.util.Map;

/**
 * Course TA entity (maps to course.course_tas table)
 */
@Entity
@Table(schema = "course", name = "course_tas")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CourseTA extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "course_id", nullable = false)
    private Course course;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ta_user_id", nullable = false)
    private User taUser;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "assigned_by", nullable = false)
    private User assignedBy;

    @Column(name = "assigned_at", nullable = false)
    private OffsetDateTime assignedAt;

    @Type(JsonBinaryType.class)
    @Column(columnDefinition = "jsonb", nullable = false)
    @Builder.Default
    private Map<String, Object> permissions = new HashMap<>();

    @PrePersist
    protected void onCreate() {
        if (assignedAt == null) {
            assignedAt = OffsetDateTime.now();
        }
        if (permissions == null || permissions.isEmpty()) {
            permissions = getDefaultPermissions();
        }
    }

    /**
     * Get default TA permissions
     */
    public static Map<String, Object> getDefaultPermissions() {
        Map<String, Object> defaults = new HashMap<>();
        defaults.put("canGrade", true);
        defaults.put("canManageStudents", true);
        defaults.put("canManageSessions", false);
        defaults.put("canManageAssignments", true);
        defaults.put("canViewAnalytics", true);
        defaults.put("canModerateDiscussions", true);
        return defaults;
    }

    /**
     * Check if TA has specific permission
     */
    public boolean hasPermission(String permission) {
        Object value = permissions.get(permission);
        return value instanceof Boolean && (Boolean) value;
    }

    /**
     * Set permission
     */
    public void setPermission(String permission, boolean value) {
        permissions.put(permission, value);
    }
}
