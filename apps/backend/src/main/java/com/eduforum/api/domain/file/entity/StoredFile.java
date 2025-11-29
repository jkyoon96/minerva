package com.eduforum.api.domain.file.entity;

import com.eduforum.api.common.audit.BaseEntity;
import com.eduforum.api.domain.auth.entity.User;
import com.eduforum.api.domain.course.entity.Course;
import jakarta.persistence.*;
import lombok.*;

/**
 * StoredFile entity (maps to file.stored_files table)
 */
@Entity
@Table(schema = "file", name = "stored_files", indexes = {
    @Index(name = "idx_stored_file_course", columnList = "course_id"),
    @Index(name = "idx_stored_file_folder", columnList = "folder_id"),
    @Index(name = "idx_stored_file_uploader", columnList = "uploaded_by")
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class StoredFile extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "original_name", nullable = false, length = 255)
    private String originalName;

    @Column(name = "stored_name", nullable = false, length = 255)
    private String storedName;

    @Column(nullable = false, length = 500)
    private String path;

    @Column(nullable = false)
    private Long size;

    @Column(name = "mime_type", nullable = false, length = 100)
    private String mimeType;

    @Column(length = 20)
    private String extension;

    @Column(length = 1000)
    private String url;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "uploaded_by", nullable = false)
    private User uploadedBy;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "course_id")
    private Course course;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "folder_id")
    private FileFolder folder;

    @Column(columnDefinition = "TEXT")
    private String description;

    @Column(name = "is_public", nullable = false)
    @Builder.Default
    private Boolean isPublic = false;

    @Column(name = "download_count", nullable = false)
    @Builder.Default
    private Long downloadCount = 0L;

    @Column(name = "is_deleted", nullable = false)
    @Builder.Default
    private Boolean isDeleted = false;

    // Helper methods
    public void incrementDownloadCount() {
        this.downloadCount++;
    }

    public void softDelete() {
        this.isDeleted = true;
    }

    public void restore() {
        this.isDeleted = false;
    }

    public String getFormattedSize() {
        if (size < 1024) {
            return size + " B";
        } else if (size < 1024 * 1024) {
            return String.format("%.2f KB", size / 1024.0);
        } else if (size < 1024 * 1024 * 1024) {
            return String.format("%.2f MB", size / (1024.0 * 1024));
        } else {
            return String.format("%.2f GB", size / (1024.0 * 1024 * 1024));
        }
    }
}
