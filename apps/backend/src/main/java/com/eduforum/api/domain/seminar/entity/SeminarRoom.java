package com.eduforum.api.domain.seminar.entity;

import com.eduforum.api.domain.auth.entity.User;
import com.eduforum.api.domain.common.entity.BaseEntity;
import com.eduforum.api.domain.course.entity.CourseSession;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.time.OffsetDateTime;
import java.util.Map;

/**
 * SeminarRoom entity - represents a live seminar/session room
 */
@Entity
@Table(schema = "seminar", name = "rooms")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SeminarRoom extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "session_id", nullable = false)
    private CourseSession session;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "host_id", nullable = false)
    private User host;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, columnDefinition = "room_status")
    @Builder.Default
    private RoomStatus status = RoomStatus.WAITING;

    @Column(name = "max_participants")
    @Builder.Default
    private Integer maxParticipants = 100;

    @Column(name = "started_at")
    private OffsetDateTime startedAt;

    @Column(name = "ended_at")
    private OffsetDateTime endedAt;

    @Column(name = "meeting_url", length = 500)
    private String meetingUrl;

    @Column(name = "recording_url", length = 500)
    private String recordingUrl;

    @Enumerated(EnumType.STRING)
    @Column(name = "layout", nullable = false, columnDefinition = "layout_type")
    @Builder.Default
    private LayoutType layout = LayoutType.GALLERY;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(nullable = false, columnDefinition = "jsonb")
    @Builder.Default
    private Map<String, Object> settings = Map.of(
        "enableWaitingRoom", true,
        "autoRecord", true,
        "allowChat", true,
        "allowReactions", true,
        "allowScreenShare", true,
        "muteOnEntry", false,
        "videoOnEntry", true
    );

    // Helper methods
    public void start() {
        this.status = RoomStatus.ACTIVE;
        this.startedAt = OffsetDateTime.now();
    }

    public void end() {
        this.status = RoomStatus.ENDED;
        this.endedAt = OffsetDateTime.now();
    }

    public boolean isActive() {
        return status == RoomStatus.ACTIVE;
    }

    public boolean hasEnded() {
        return status == RoomStatus.ENDED;
    }

    public boolean isWaiting() {
        return status == RoomStatus.WAITING;
    }

    public void updateLayout(LayoutType newLayout) {
        this.layout = newLayout;
    }
}
