package com.eduforum.api.domain.active.entity;

import com.eduforum.api.domain.auth.entity.User;
import com.eduforum.api.domain.common.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.util.ArrayList;
import java.util.List;

/**
 * Poll response entity - represents a user's response to a poll
 */
@Entity
@Table(schema = "active", name = "poll_responses")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PollResponse extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "poll_id", nullable = false)
    private Poll poll;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "selected_option_ids", columnDefinition = "jsonb")
    @Builder.Default
    private List<Long> selectedOptionIds = new ArrayList<>();

    @Column(name = "text_response", columnDefinition = "TEXT")
    private String textResponse;
}
