package com.eduforum.api.domain.analytics.entity;

import com.eduforum.api.domain.common.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.time.OffsetDateTime;
import java.util.Map;

/**
 * Interaction log - records student-student interactions
 */
@Entity
@Table(schema = "analytics", name = "interaction_logs")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class InteractionLog extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "course_id", nullable = false)
    private Long courseId;

    @Column(name = "session_id")
    private Long sessionId;

    @Column(name = "from_student_id", nullable = false)
    private Long fromStudentId;

    @Column(name = "to_student_id", nullable = false)
    private Long toStudentId;

    @Enumerated(EnumType.STRING)
    @Column(name = "interaction_type", nullable = false)
    private InteractionType interactionType;

    @Column(name = "interaction_time", nullable = false)
    private OffsetDateTime interactionTime;

    @Column(name = "weight")
    @Builder.Default
    private Integer weight = 1;

    @Column(name = "context", length = 500)
    private String context;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(columnDefinition = "jsonb")
    @Builder.Default
    private Map<String, Object> metadata = Map.of();

    // Helper methods
    public boolean isBidirectional() {
        return interactionType == InteractionType.COLLABORATION ||
               interactionType == InteractionType.DISCUSSION;
    }

    public boolean isRecent(int hours) {
        OffsetDateTime threshold = OffsetDateTime.now().minusHours(hours);
        return interactionTime.isAfter(threshold);
    }
}
