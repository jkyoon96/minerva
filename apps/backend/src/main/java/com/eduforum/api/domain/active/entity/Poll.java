package com.eduforum.api.domain.active.entity;

import com.eduforum.api.domain.auth.entity.User;
import com.eduforum.api.domain.common.entity.BaseEntity;
import com.eduforum.api.domain.course.entity.Course;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Poll entity - represents a poll/vote
 */
@Entity
@Table(schema = "active", name = "polls")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Poll extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "course_id", nullable = false)
    private Course course;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "creator_id", nullable = false)
    private User creator;

    @Column(nullable = false, length = 500)
    private String question;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, columnDefinition = "poll_type")
    private PollType type;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, columnDefinition = "poll_status")
    @Builder.Default
    private PollStatus status = PollStatus.DRAFT;

    @Column(name = "allow_multiple")
    @Builder.Default
    private Boolean allowMultiple = false;

    @Column(name = "show_results")
    @Builder.Default
    private Boolean showResults = true;

    @Column(name = "anonymous")
    @Builder.Default
    private Boolean anonymous = false;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(columnDefinition = "jsonb")
    @Builder.Default
    private Map<String, Object> settings = Map.of();

    @OneToMany(mappedBy = "poll", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<PollOption> options = new ArrayList<>();

    @OneToMany(mappedBy = "poll", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<PollResponse> responses = new ArrayList<>();

    // Helper methods
    public void activate() {
        this.status = PollStatus.ACTIVE;
    }

    public void close() {
        this.status = PollStatus.CLOSED;
    }

    public boolean isActive() {
        return status == PollStatus.ACTIVE;
    }

    public boolean isClosed() {
        return status == PollStatus.CLOSED;
    }

    public void addOption(PollOption option) {
        options.add(option);
        option.setPoll(this);
    }

    public void addResponse(PollResponse response) {
        responses.add(response);
        response.setPoll(this);
    }
}
