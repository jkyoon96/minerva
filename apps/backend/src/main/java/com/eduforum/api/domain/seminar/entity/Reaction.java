package com.eduforum.api.domain.seminar.entity;

import com.eduforum.api.domain.auth.entity.User;
import com.eduforum.api.domain.common.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.*;

/**
 * Reaction entity - represents a real-time reaction in a seminar room
 */
@Entity
@Table(schema = "seminar", name = "reactions",
    indexes = {
        @Index(name = "idx_reaction_room_created", columnList = "room_id, created_at DESC")
    })
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Reaction extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "room_id", nullable = false)
    private SeminarRoom room;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Enumerated(EnumType.STRING)
    @Column(name = "reaction_type", nullable = false, columnDefinition = "reaction_type")
    private ReactionType reactionType;

    // Helper methods
    public String getEmoji() {
        return switch (reactionType) {
            case THUMBS_UP -> "👍";
            case CLAP -> "👏";
            case HEART -> "❤️";
            case LAUGH -> "😂";
            case SURPRISE -> "😮";
        };
    }
}
