package com.eduforum.api.domain.course.entity;

import com.eduforum.api.domain.common.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

/**
 * Grading criteria entity (maps to course.grading_criteria table)
 */
@Entity
@Table(schema = "course", name = "grading_criteria")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class GradingCriteria extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "course_id", nullable = false)
    private Course course;

    @Column(nullable = false, length = 255)
    private String name;

    @Column(columnDefinition = "TEXT")
    private String description;

    @Column(nullable = false, precision = 5, scale = 2)
    private BigDecimal weight;

    @Column(name = "max_score", nullable = false, precision = 8, scale = 2)
    private BigDecimal maxScore;

    @Column(name = "order_index", nullable = false)
    @Builder.Default
    private Integer orderIndex = 0;

    @OneToMany(mappedBy = "criteria", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<RubricItem> rubricItems = new ArrayList<>();

    /**
     * Add rubric item to this criteria
     */
    public void addRubricItem(RubricItem item) {
        rubricItems.add(item);
        item.setCriteria(this);
    }

    /**
     * Remove rubric item from this criteria
     */
    public void removeRubricItem(RubricItem item) {
        rubricItems.remove(item);
        item.setCriteria(null);
    }
}
