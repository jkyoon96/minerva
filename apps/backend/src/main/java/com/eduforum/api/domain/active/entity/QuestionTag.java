package com.eduforum.api.domain.active.entity;

import com.eduforum.api.domain.common.entity.BaseEntity;
import com.eduforum.api.domain.course.entity.Course;
import jakarta.persistence.*;
import lombok.*;

/**
 * Question tag entity - for categorizing questions
 */
@Entity
@Table(schema = "active", name = "question_tags")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class QuestionTag extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "course_id", nullable = false)
    private Course course;

    @Column(nullable = false, length = 100)
    private String name;

    @Column(length = 7)
    private String color;
}
