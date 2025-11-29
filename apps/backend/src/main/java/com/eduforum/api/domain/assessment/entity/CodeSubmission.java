package com.eduforum.api.domain.assessment.entity;

import com.eduforum.api.domain.common.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Code submission entity
 */
@Entity
@Table(schema = "assessment", name = "code_submissions")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CodeSubmission extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "assignment_id", nullable = false)
    private Long assignmentId;

    @Column(name = "student_id", nullable = false)
    private Long studentId;

    @Column(name = "language", nullable = false, length = 50)
    private String language;

    @Column(name = "code", nullable = false, columnDefinition = "TEXT")
    private String code;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, columnDefinition = "submission_status")
    @Builder.Default
    private SubmissionStatus status = SubmissionStatus.SUBMITTED;

    @Column(name = "submitted_at", nullable = false)
    @Builder.Default
    private OffsetDateTime submittedAt = OffsetDateTime.now();

    @Column(name = "executed_at")
    private OffsetDateTime executedAt;

    @Column(name = "passed_tests")
    @Builder.Default
    private Integer passedTests = 0;

    @Column(name = "total_tests")
    @Builder.Default
    private Integer totalTests = 0;

    @Column(name = "execution_time_ms")
    private Long executionTimeMs;

    @Column(name = "memory_used_kb")
    private Long memoryUsedKb;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "compiler_output", columnDefinition = "jsonb")
    private Map<String, Object> compilerOutput;

    @OneToMany(mappedBy = "codeSubmission", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<ExecutionResult> executionResults = new ArrayList<>();

    // Helper methods
    public void markAsRunning() {
        this.status = SubmissionStatus.RUNNING;
    }

    public void markAsCompleted() {
        this.status = SubmissionStatus.COMPLETED;
        this.executedAt = OffsetDateTime.now();
    }

    public void markAsError() {
        this.status = SubmissionStatus.ERROR;
        this.executedAt = OffsetDateTime.now();
    }

    public void markAsCompileError() {
        this.status = SubmissionStatus.COMPILE_ERROR;
        this.executedAt = OffsetDateTime.now();
    }

    public boolean isCompleted() {
        return status == SubmissionStatus.COMPLETED;
    }

    public double getPassRate() {
        if (totalTests == 0) return 0.0;
        return (double) passedTests / totalTests * 100;
    }

    public void addExecutionResult(ExecutionResult result) {
        executionResults.add(result);
        result.setCodeSubmission(this);
    }
}
