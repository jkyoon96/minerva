package com.eduforum.api.domain.active.entity;

import com.eduforum.api.domain.common.entity.BaseEntity;
import com.eduforum.api.domain.seminar.entity.SeminarRoom;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Breakout room entity - represents a breakout room in a seminar
 */
@Entity
@Table(schema = "active", name = "breakout_rooms")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BreakoutRoom extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "seminar_room_id", nullable = false)
    private SeminarRoom seminarRoom;

    @Column(nullable = false, length = 100)
    private String name;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, columnDefinition = "breakout_status")
    @Builder.Default
    private BreakoutStatus status = BreakoutStatus.WAITING;

    @Enumerated(EnumType.STRING)
    @Column(name = "assignment_method", nullable = false, columnDefinition = "assignment_method")
    private AssignmentMethod assignmentMethod;

    @Column(name = "max_participants")
    private Integer maxParticipants;

    @Column(name = "duration_minutes")
    private Integer durationMinutes;

    @Column(name = "started_at")
    private OffsetDateTime startedAt;

    @Column(name = "ends_at")
    private OffsetDateTime endsAt;

    @Column(name = "meeting_url", length = 500)
    private String meetingUrl;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(columnDefinition = "jsonb")
    @Builder.Default
    private Map<String, Object> settings = Map.of();

    @OneToMany(mappedBy = "breakoutRoom", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<BreakoutParticipant> participants = new ArrayList<>();

    // Helper methods
    public void start() {
        this.status = BreakoutStatus.ACTIVE;
        this.startedAt = OffsetDateTime.now();
        if (durationMinutes != null) {
            this.endsAt = startedAt.plusMinutes(durationMinutes);
        }
    }

    public void close() {
        this.status = BreakoutStatus.CLOSED;
    }

    public boolean isActive() {
        return status == BreakoutStatus.ACTIVE;
    }

    public boolean isClosed() {
        return status == BreakoutStatus.CLOSED;
    }

    public void addParticipant(BreakoutParticipant participant) {
        participants.add(participant);
        participant.setBreakoutRoom(this);
    }
}
