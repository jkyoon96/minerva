package com.eduforum.api.domain.active.entity;

import com.eduforum.api.domain.common.entity.BaseEntity;
import com.eduforum.api.domain.seminar.entity.SeminarRoom;
import jakarta.persistence.*;
import lombok.*;

import java.time.OffsetDateTime;

/**
 * Poll session entity - represents an active poll session in a seminar
 */
@Entity
@Table(schema = "active", name = "poll_sessions")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PollSession extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "poll_id", nullable = false)
    private Poll poll;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "room_id", nullable = false)
    private SeminarRoom room;

    @Column(name = "started_at")
    private OffsetDateTime startedAt;

    @Column(name = "ended_at")
    private OffsetDateTime endedAt;

    @Column(name = "response_count")
    @Builder.Default
    private Integer responseCount = 0;

    // Helper methods
    public void start() {
        this.startedAt = OffsetDateTime.now();
    }

    public void end() {
        this.endedAt = OffsetDateTime.now();
    }

    public boolean isActive() {
        return startedAt != null && endedAt == null;
    }

    public void incrementResponseCount() {
        this.responseCount++;
    }
}
