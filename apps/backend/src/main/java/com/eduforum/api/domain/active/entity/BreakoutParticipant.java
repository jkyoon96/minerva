package com.eduforum.api.domain.active.entity;

import com.eduforum.api.domain.auth.entity.User;
import com.eduforum.api.domain.common.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.*;

import java.time.OffsetDateTime;

/**
 * Breakout participant entity - represents a participant in a breakout room
 */
@Entity
@Table(schema = "active", name = "breakout_participants")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BreakoutParticipant extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "breakout_room_id", nullable = false)
    private BreakoutRoom breakoutRoom;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(name = "joined_at")
    private OffsetDateTime joinedAt;

    @Column(name = "left_at")
    private OffsetDateTime leftAt;

    // Helper methods
    public void join() {
        this.joinedAt = OffsetDateTime.now();
    }

    public void leave() {
        this.leftAt = OffsetDateTime.now();
    }

    public boolean isActive() {
        return joinedAt != null && leftAt == null;
    }
}
