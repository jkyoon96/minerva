package com.eduforum.api.domain.seminar.entity;

import com.eduforum.api.domain.auth.entity.User;
import com.eduforum.api.domain.common.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.*;

import java.time.OffsetDateTime;

/**
 * RoomParticipant entity - represents a participant in a seminar room
 */
@Entity
@Table(schema = "seminar", name = "room_participants",
    uniqueConstraints = @UniqueConstraint(columnNames = {"room_id", "user_id"}))
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RoomParticipant extends BaseEntity {

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
    @Column(nullable = false, columnDefinition = "participant_role")
    @Builder.Default
    private ParticipantRole role = ParticipantRole.PARTICIPANT;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, columnDefinition = "participant_status")
    @Builder.Default
    private ParticipantStatus status = ParticipantStatus.WAITING;

    @Column(name = "joined_at")
    private OffsetDateTime joinedAt;

    @Column(name = "left_at")
    private OffsetDateTime leftAt;

    @Column(name = "is_hand_raised", nullable = false)
    @Builder.Default
    private Boolean isHandRaised = false;

    @Column(name = "is_muted", nullable = false)
    @Builder.Default
    private Boolean isMuted = false;

    @Column(name = "is_video_on", nullable = false)
    @Builder.Default
    private Boolean isVideoOn = true;

    @Column(name = "is_screen_sharing", nullable = false)
    @Builder.Default
    private Boolean isScreenSharing = false;

    // Helper methods
    public void join() {
        this.status = ParticipantStatus.JOINED;
        this.joinedAt = OffsetDateTime.now();
    }

    public void leave() {
        this.status = ParticipantStatus.LEFT;
        this.leftAt = OffsetDateTime.now();
        this.isHandRaised = false;
        this.isScreenSharing = false;
    }

    public void raiseHand() {
        this.isHandRaised = true;
    }

    public void lowerHand() {
        this.isHandRaised = false;
    }

    public void toggleMute() {
        this.isMuted = !this.isMuted;
    }

    public void toggleVideo() {
        this.isVideoOn = !this.isVideoOn;
    }

    public void startScreenShare() {
        this.isScreenSharing = true;
    }

    public void stopScreenShare() {
        this.isScreenSharing = false;
    }

    public boolean isHost() {
        return role == ParticipantRole.HOST;
    }

    public boolean isCoHost() {
        return role == ParticipantRole.CO_HOST;
    }

    public boolean isActiveParticipant() {
        return status == ParticipantStatus.JOINED;
    }
}
