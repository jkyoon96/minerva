package com.eduforum.api.domain.course.entity;

import com.eduforum.api.domain.common.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.time.OffsetDateTime;
import java.util.Map;

/**
 * CourseSession entity (maps to course.sessions table)
 * Named CourseSession to avoid conflict with HTTP Session
 */
@Entity
@Table(schema = "course", name = "sessions")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CourseSession extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "course_id", nullable = false)
    private Course course;

    @Column(nullable = false, length = 255)
    private String title;

    @Column(columnDefinition = "TEXT")
    private String description;

    @Column(name = "scheduled_at", nullable = false)
    private OffsetDateTime scheduledAt;

    @Column(name = "duration_minutes", nullable = false)
    @Builder.Default
    private Integer durationMinutes = 90;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, columnDefinition = "session_status")
    @Builder.Default
    private SessionStatus status = SessionStatus.SCHEDULED;

    @Column(name = "started_at")
    private OffsetDateTime startedAt;

    @Column(name = "ended_at")
    private OffsetDateTime endedAt;

    @Column(name = "meeting_url", length = 500)
    private String meetingUrl;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(nullable = false, columnDefinition = "jsonb")
    @Builder.Default
    private Map<String, Object> settings = Map.of(
        "enable_waiting_room", false,
        "auto_record", true,
        "allow_chat", true,
        "allow_reactions", true
    );

    // Helper methods
    public void start() {
        this.status = SessionStatus.LIVE;
        this.startedAt = OffsetDateTime.now();
    }

    public void end() {
        this.status = SessionStatus.ENDED;
        this.endedAt = OffsetDateTime.now();
    }

    public void cancel() {
        this.status = SessionStatus.CANCELLED;
    }

    public boolean isLive() {
        return status == SessionStatus.LIVE;
    }

    public boolean hasEnded() {
        return status == SessionStatus.ENDED || status == SessionStatus.CANCELLED;
    }
}
