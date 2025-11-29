package com.eduforum.api.domain.active.entity;

import com.eduforum.api.domain.auth.entity.User;
import com.eduforum.api.domain.common.entity.BaseEntity;
import com.eduforum.api.domain.seminar.entity.SeminarRoom;
import jakarta.persistence.*;
import lombok.*;

import java.time.OffsetDateTime;

/**
 * Speaking queue entity - represents a speaking queue entry
 */
@Entity
@Table(schema = "active", name = "speaking_queue")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SpeakingQueue extends BaseEntity {

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
    @Column(nullable = false, columnDefinition = "speaking_status")
    @Builder.Default
    private SpeakingStatus status = SpeakingStatus.WAITING;

    @Column(name = "queue_position")
    private Integer queuePosition;

    @Column(name = "granted_at")
    private OffsetDateTime grantedAt;

    @Column(name = "finished_at")
    private OffsetDateTime finishedAt;

    @Column(name = "speaking_duration_seconds")
    private Integer speakingDurationSeconds;

    // Helper methods
    public void grant() {
        this.status = SpeakingStatus.SPEAKING;
        this.grantedAt = OffsetDateTime.now();
    }

    public void finish() {
        this.status = SpeakingStatus.FINISHED;
        this.finishedAt = OffsetDateTime.now();
        if (grantedAt != null) {
            long seconds = java.time.Duration.between(grantedAt, finishedAt).getSeconds();
            this.speakingDurationSeconds = (int) seconds;
        }
    }

    public boolean isWaiting() {
        return status == SpeakingStatus.WAITING;
    }

    public boolean isSpeaking() {
        return status == SpeakingStatus.SPEAKING;
    }
}
