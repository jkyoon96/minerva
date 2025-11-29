package com.eduforum.api.domain.assessment.entity;

import com.eduforum.api.domain.common.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.*;

/**
 * Test case entity for code evaluation
 */
@Entity
@Table(schema = "assessment", name = "test_cases")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TestCase extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "assignment_id", nullable = false)
    private Long assignmentId;

    @Column(name = "test_name", nullable = false, length = 100)
    private String testName;

    @Column(name = "input_data", columnDefinition = "TEXT")
    private String inputData;

    @Column(name = "expected_output", columnDefinition = "TEXT")
    private String expectedOutput;

    @Column(name = "is_hidden", nullable = false)
    @Builder.Default
    private Boolean isHidden = false;

    @Column(name = "points")
    private Integer points;

    @Column(name = "time_limit_ms")
    private Integer timeLimitMs;

    @Column(name = "memory_limit_kb")
    private Integer memoryLimitKb;

    @Column(name = "display_order")
    private Integer displayOrder;

    // Helper methods
    public boolean isVisible() {
        return !isHidden;
    }
}
