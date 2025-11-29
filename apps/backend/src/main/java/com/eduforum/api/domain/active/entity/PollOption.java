package com.eduforum.api.domain.active.entity;

import com.eduforum.api.domain.common.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.*;

/**
 * Poll option entity - represents a single option in a poll
 */
@Entity
@Table(schema = "active", name = "poll_options")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PollOption extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "poll_id", nullable = false)
    private Poll poll;

    @Column(nullable = false, length = 500)
    private String text;

    @Column(name = "display_order", nullable = false)
    private Integer order;

    @Column(name = "is_correct")
    private Boolean isCorrect;
}
