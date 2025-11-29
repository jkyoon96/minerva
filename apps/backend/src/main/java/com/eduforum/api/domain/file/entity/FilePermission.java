package com.eduforum.api.domain.file.entity;

import com.eduforum.api.common.audit.BaseEntity;
import com.eduforum.api.domain.auth.entity.User;
import jakarta.persistence.*;
import lombok.*;

/**
 * FilePermission entity (maps to file.file_permissions table)
 */
@Entity
@Table(schema = "file", name = "file_permissions",
    uniqueConstraints = @UniqueConstraint(columnNames = {"file_id", "user_id"}),
    indexes = {
        @Index(name = "idx_permission_file", columnList = "file_id"),
        @Index(name = "idx_permission_user", columnList = "user_id")
    }
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class FilePermission extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "file_id", nullable = false)
    private StoredFile file;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private PermissionType permission;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "granted_by")
    private User grantedBy;

    /**
     * 권한 타입
     */
    public enum PermissionType {
        /**
         * 읽기 권한
         */
        READ,

        /**
         * 쓰기 권한 (수정)
         */
        WRITE,

        /**
         * 삭제 권한
         */
        DELETE,

        /**
         * 모든 권한 (소유자)
         */
        OWNER
    }

    // Helper methods
    public boolean canRead() {
        return true; // 모든 권한은 읽기 가능
    }

    public boolean canWrite() {
        return permission == PermissionType.WRITE ||
               permission == PermissionType.DELETE ||
               permission == PermissionType.OWNER;
    }

    public boolean canDelete() {
        return permission == PermissionType.DELETE ||
               permission == PermissionType.OWNER;
    }

    public boolean isOwner() {
        return permission == PermissionType.OWNER;
    }
}
