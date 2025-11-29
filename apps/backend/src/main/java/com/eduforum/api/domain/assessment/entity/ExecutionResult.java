package com.eduforum.api.domain.assessment.entity;

import com.eduforum.api.domain.common.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.*;

/**
 * Execution result entity for individual test case
 */
@Entity
@Table(schema = "assessment", name = "execution_results")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ExecutionResult extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "submission_id", nullable = false)
    private CodeSubmission codeSubmission;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "test_case_id", nullable = false)
    private TestCase testCase;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, columnDefinition = "execution_status")
    private ExecutionStatus status;

    @Column(name = "actual_output", columnDefinition = "TEXT")
    private String actualOutput;

    @Column(name = "error_message", columnDefinition = "TEXT")
    private String errorMessage;

    @Column(name = "execution_time_ms")
    private Long executionTimeMs;

    @Column(name = "memory_used_kb")
    private Long memoryUsedKb;

    @Column(name = "passed", nullable = false)
    @Builder.Default
    private Boolean passed = false;

    // Helper methods
    public boolean isPassed() {
        return passed && status == ExecutionStatus.SUCCESS;
    }

    public boolean isFailed() {
        return !passed || status != ExecutionStatus.SUCCESS;
    }
}
