package com.eduforum.api.domain.file.entity;

import com.eduforum.api.common.audit.BaseEntity;
import com.eduforum.api.domain.auth.entity.User;
import com.eduforum.api.domain.course.entity.Course;
import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

/**
 * FileFolder entity (maps to file.file_folders table)
 */
@Entity
@Table(schema = "file", name = "file_folders", indexes = {
    @Index(name = "idx_folder_course", columnList = "course_id"),
    @Index(name = "idx_folder_parent", columnList = "parent_id")
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class FileFolder extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 255)
    private String name;

    @Column(columnDefinition = "TEXT")
    private String description;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "parent_id")
    private FileFolder parent;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "course_id", nullable = false)
    private Course course;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "created_by_user", nullable = false)
    private User createdByUser;

    @Column(name = "is_public", nullable = false)
    @Builder.Default
    private Boolean isPublic = false;

    @Column(name = "is_deleted", nullable = false)
    @Builder.Default
    private Boolean isDeleted = false;

    @Column(name = "sort_order")
    @Builder.Default
    private Integer sortOrder = 0;

    // Relationships
    @OneToMany(mappedBy = "parent", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<FileFolder> children = new ArrayList<>();

    @OneToMany(mappedBy = "folder", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<StoredFile> files = new ArrayList<>();

    // Helper methods
    public void softDelete() {
        this.isDeleted = true;
        // Cascade soft delete to children
        for (FileFolder child : children) {
            child.softDelete();
        }
        // Cascade soft delete to files
        for (StoredFile file : files) {
            file.softDelete();
        }
    }

    public void restore() {
        this.isDeleted = false;
    }

    public boolean isRootFolder() {
        return parent == null;
    }

    public String getFullPath() {
        if (parent == null) {
            return name;
        }
        return parent.getFullPath() + "/" + name;
    }

    public int getDepth() {
        int depth = 0;
        FileFolder current = this.parent;
        while (current != null) {
            depth++;
            current = current.getParent();
        }
        return depth;
    }
}
