package com.eduforum.api.domain.course.entity;

import com.eduforum.api.domain.common.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;

/**
 * Rubric item entity (maps to course.rubric_items table)
 */
@Entity
@Table(schema = "course", name = "rubric_items")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RubricItem extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "criteria_id", nullable = false)
    private GradingCriteria criteria;

    @Column(nullable = false, length = 100)
    private String level;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String description;

    @Column(nullable = false, precision = 8, scale = 2)
    private BigDecimal score;

    @Column(name = "order_index", nullable = false)
    @Builder.Default
    private Integer orderIndex = 0;
}
