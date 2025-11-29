package com.eduforum.api.domain.assessment.service;

import com.eduforum.api.common.exception.BusinessException;
import com.eduforum.api.common.exception.ErrorCode;
import com.eduforum.api.domain.assessment.dto.code.CodeSubmissionResponse;
import com.eduforum.api.domain.assessment.dto.code.ExecutionResultResponse;
import com.eduforum.api.domain.assessment.entity.*;
import com.eduforum.api.domain.assessment.repository.CodeSubmissionRepository;
import com.eduforum.api.domain.assessment.repository.ExecutionResultRepository;
import com.eduforum.api.domain.assessment.repository.TestCaseRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class CodeExecutionService {

    private final CodeSubmissionRepository codeSubmissionRepository;
    private final TestCaseRepository testCaseRepository;
    private final ExecutionResultRepository executionResultRepository;
    private final Random random = new Random();

    @Transactional
    public CodeSubmissionResponse submitCode(Long studentId, Long assignmentId, String language, String code, Boolean autoRun) {
        log.info("Code submission from student {} for assignment {}", studentId, assignmentId);

        CodeSubmission submission = CodeSubmission.builder()
            .assignmentId(assignmentId)
            .studentId(studentId)
            .language(language)
            .code(code)
            .status(SubmissionStatus.SUBMITTED)
            .totalTests(0)
            .passedTests(0)
            .build();

        submission = codeSubmissionRepository.save(submission);

        if (autoRun) {
            return runCode(submission.getId());
        }

        return toResponse(submission);
    }

    @Transactional
    public CodeSubmissionResponse runCode(Long submissionId) {
        CodeSubmission submission = codeSubmissionRepository.findById(submissionId)
            .orElseThrow(() -> new BusinessException(ErrorCode.ENTITY_NOT_FOUND, "Code submission not found"));

        submission.markAsRunning();
        codeSubmissionRepository.save(submission);

        // Fetch test cases
        List<TestCase> testCases = testCaseRepository.findByAssignmentId(submission.getAssignmentId());
        submission.setTotalTests(testCases.size());

        // Execute code against test cases (simulated)
        int passedCount = 0;
        long totalExecutionTime = 0;

        for (TestCase testCase : testCases) {
            ExecutionResult result = executeTestCase(submission, testCase);
            executionResultRepository.save(result);

            if (result.getPassed()) {
                passedCount++;
            }
            totalExecutionTime += result.getExecutionTimeMs();
        }

        submission.setPassedTests(passedCount);
        submission.setExecutionTimeMs(totalExecutionTime);
        submission.setMemoryUsedKb((long) (512 + random.nextInt(1024))); // Simulated
        submission.markAsCompleted();

        submission = codeSubmissionRepository.save(submission);
        return toResponse(submission);
    }

    @Transactional(readOnly = true)
    public CodeSubmissionResponse getSubmission(Long submissionId) {
        CodeSubmission submission = codeSubmissionRepository.findById(submissionId)
            .orElseThrow(() -> new BusinessException(ErrorCode.ENTITY_NOT_FOUND, "Code submission not found"));

        return toResponse(submission);
    }

    @Transactional(readOnly = true)
    public List<CodeSubmissionResponse> getAssignmentSubmissions(Long assignmentId) {
        List<CodeSubmission> submissions = codeSubmissionRepository.findByAssignmentId(assignmentId);

        return submissions.stream()
            .map(this::toResponse)
            .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<ExecutionResultResponse> getExecutionResults(Long submissionId) {
        List<ExecutionResult> results = executionResultRepository.findByCodeSubmissionId(submissionId);

        return results.stream()
            .map(this::toExecutionResultResponse)
            .collect(Collectors.toList());
    }

    private ExecutionResult executeTestCase(CodeSubmission submission, TestCase testCase) {
        // Simulated code execution - in real implementation, use sandbox/container
        boolean passed = random.nextBoolean(); // Random pass/fail for simulation
        long executionTime = 50 + random.nextInt(200); // 50-250ms

        ExecutionStatus status = passed ? ExecutionStatus.SUCCESS : ExecutionStatus.WRONG_ANSWER;

        return ExecutionResult.builder()
            .codeSubmission(submission)
            .testCase(testCase)
            .status(status)
            .actualOutput(passed ? testCase.getExpectedOutput() : "Different output")
            .executionTimeMs(executionTime)
            .memoryUsedKb((long) (256 + random.nextInt(512)))
            .passed(passed)
            .build();
    }

    private CodeSubmissionResponse toResponse(CodeSubmission submission) {
        return CodeSubmissionResponse.builder()
            .id(submission.getId())
            .assignmentId(submission.getAssignmentId())
            .studentId(submission.getStudentId())
            .language(submission.getLanguage())
            .code(submission.getCode())
            .status(submission.getStatus())
            .submittedAt(submission.getSubmittedAt())
            .executedAt(submission.getExecutedAt())
            .passedTests(submission.getPassedTests())
            .totalTests(submission.getTotalTests())
            .passRate(submission.getPassRate())
            .executionTimeMs(submission.getExecutionTimeMs())
            .memoryUsedKb(submission.getMemoryUsedKb())
            .compilerOutput(submission.getCompilerOutput())
            .build();
    }

    private ExecutionResultResponse toExecutionResultResponse(ExecutionResult result) {
        return ExecutionResultResponse.builder()
            .id(result.getId())
            .testCaseName(result.getTestCase().getTestName())
            .status(result.getStatus())
            .actualOutput(result.getActualOutput())
            .expectedOutput(result.getTestCase().getExpectedOutput())
            .errorMessage(result.getErrorMessage())
            .executionTimeMs(result.getExecutionTimeMs())
            .memoryUsedKb(result.getMemoryUsedKb())
            .passed(result.getPassed())
            .build();
    }
}
