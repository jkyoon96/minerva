package com.eduforum.api.domain.assessment.entity;

import com.eduforum.api.domain.common.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.time.OffsetDateTime;
import java.util.Map;

/**
 * Participation event entity
 */
@Entity
@Table(schema = "assessment", name = "participation_events")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ParticipationEvent extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "student_id", nullable = false)
    private Long studentId;

    @Column(name = "course_id", nullable = false)
    private Long courseId;

    @Column(name = "session_id")
    private Long sessionId;

    @Enumerated(EnumType.STRING)
    @Column(name = "event_type", nullable = false, columnDefinition = "event_type")
    private EventType eventType;

    @Column(name = "event_time", nullable = false)
    @Builder.Default
    private OffsetDateTime eventTime = OffsetDateTime.now();

    @Column(name = "points")
    @Builder.Default
    private Integer points = 1;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "event_data", columnDefinition = "jsonb")
    @Builder.Default
    private Map<String, Object> eventData = Map.of();

    @Column(name = "is_counted", nullable = false)
    @Builder.Default
    private Boolean isCounted = true;

    // Helper methods
    public void exclude() {
        this.isCounted = false;
    }

    public void include() {
        this.isCounted = true;
    }
}
