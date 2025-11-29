package com.eduforum.api.domain.analytics.entity;

import com.eduforum.api.domain.common.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.time.OffsetDateTime;
import java.util.Map;

/**
 * Analytics snapshot - point-in-time analytics data
 */
@Entity
@Table(schema = "analytics", name = "analytics_snapshots")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AnalyticsSnapshot extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "session_id")
    private Long sessionId;

    @Column(name = "course_id", nullable = false)
    private Long courseId;

    @Column(name = "snapshot_time", nullable = false)
    private OffsetDateTime snapshotTime;

    @Column(name = "total_participants")
    @Builder.Default
    private Integer totalParticipants = 0;

    @Column(name = "active_participants")
    @Builder.Default
    private Integer activeParticipants = 0;

    @Column(name = "avg_engagement_score")
    private Double avgEngagementScore;

    @Column(name = "total_interactions")
    @Builder.Default
    private Integer totalInteractions = 0;

    @Column(name = "poll_responses")
    @Builder.Default
    private Integer pollResponses = 0;

    @Column(name = "quiz_attempts")
    @Builder.Default
    private Integer quizAttempts = 0;

    @Column(name = "chat_messages")
    @Builder.Default
    private Integer chatMessages = 0;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(columnDefinition = "jsonb")
    @Builder.Default
    private Map<String, Object> metricsData = Map.of();

    // Helper methods
    public void incrementInteractions() {
        this.totalInteractions++;
    }

    public void incrementPollResponses() {
        this.pollResponses++;
    }

    public void incrementQuizAttempts() {
        this.quizAttempts++;
    }

    public void incrementChatMessages() {
        this.chatMessages++;
    }
}
