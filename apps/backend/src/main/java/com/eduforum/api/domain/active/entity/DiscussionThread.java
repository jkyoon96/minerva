package com.eduforum.api.domain.active.entity;

import com.eduforum.api.domain.auth.entity.User;
import com.eduforum.api.domain.common.entity.BaseEntity;
import com.eduforum.api.domain.seminar.entity.SeminarRoom;
import jakarta.persistence.*;
import lombok.*;

/**
 * Discussion thread entity - represents a discussion thread in a seminar
 */
@Entity
@Table(schema = "active", name = "discussion_threads")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DiscussionThread extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "room_id", nullable = false)
    private SeminarRoom room;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(nullable = false, length = 200)
    private String title;

    @Column(columnDefinition = "TEXT")
    private String content;

    @Column(name = "is_resolved")
    @Builder.Default
    private Boolean isResolved = false;

    @Column(name = "upvote_count")
    @Builder.Default
    private Integer upvoteCount = 0;

    // Helper methods
    public void resolve() {
        this.isResolved = true;
    }

    public void incrementUpvote() {
        this.upvoteCount++;
    }

    public void decrementUpvote() {
        if (this.upvoteCount > 0) {
            this.upvoteCount--;
        }
    }
}
