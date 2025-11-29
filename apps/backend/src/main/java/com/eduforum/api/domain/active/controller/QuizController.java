package com.eduforum.api.domain.active.controller;

import com.eduforum.api.common.dto.ApiResponse;
import com.eduforum.api.domain.active.dto.quiz.*;
import com.eduforum.api.domain.active.entity.QuestionType;
import com.eduforum.api.domain.active.service.QuestionBankService;
import com.eduforum.api.domain.active.service.QuizService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/v1")
@RequiredArgsConstructor
@Tag(name = "Quiz & Question Bank", description = "퀴즈 및 문제은행 관리 API")
public class QuizController {

    private final QuestionBankService questionBankService;
    private final QuizService quizService;

    // Question Bank Endpoints

    @PostMapping("/questions")
    @Operation(summary = "문제 생성", description = "문제은행에 새로운 문제를 생성합니다")
    public ResponseEntity<ApiResponse<QuestionResponse>> createQuestion(
            @Parameter(hidden = true) @RequestHeader("X-User-Id") Long userId,
            @Valid @RequestBody QuestionCreateRequest request) {
        QuestionResponse response = questionBankService.createQuestion(userId, request);
        return ResponseEntity.status(HttpStatus.CREATED)
            .body(ApiResponse.success("문제가 생성되었습니다", response));
    }

    @GetMapping("/questions/{questionId}")
    @Operation(summary = "문제 조회", description = "문제 정보를 조회합니다")
    public ResponseEntity<ApiResponse<QuestionResponse>> getQuestion(
            @PathVariable Long questionId) {
        QuestionResponse response = questionBankService.getQuestion(questionId);
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @GetMapping("/courses/{courseId}/questions")
    @Operation(summary = "코스별 문제 목록", description = "코스의 모든 문제를 조회합니다")
    public ResponseEntity<ApiResponse<List<QuestionResponse>>> getQuestionsByCourse(
            @PathVariable Long courseId,
            @RequestParam(required = false) QuestionType type,
            @RequestParam(required = false) List<Long> tagIds) {
        List<QuestionResponse> responses = questionBankService.getQuestionsByCourse(courseId, type, tagIds);
        return ResponseEntity.ok(ApiResponse.success(responses));
    }

    @PutMapping("/questions/{questionId}")
    @Operation(summary = "문제 수정", description = "문제를 수정합니다")
    public ResponseEntity<ApiResponse<QuestionResponse>> updateQuestion(
            @Parameter(hidden = true) @RequestHeader("X-User-Id") Long userId,
            @PathVariable Long questionId,
            @Valid @RequestBody QuestionCreateRequest request) {
        QuestionResponse response = questionBankService.updateQuestion(userId, questionId, request);
        return ResponseEntity.ok(ApiResponse.success("문제가 수정되었습니다", response));
    }

    @DeleteMapping("/questions/{questionId}")
    @Operation(summary = "문제 삭제", description = "문제를 삭제합니다")
    public ResponseEntity<ApiResponse<Void>> deleteQuestion(
            @Parameter(hidden = true) @RequestHeader("X-User-Id") Long userId,
            @PathVariable Long questionId) {
        questionBankService.deleteQuestion(userId, questionId);
        return ResponseEntity.ok(ApiResponse.success("문제가 삭제되었습니다", null));
    }

    @GetMapping("/courses/{courseId}/question-tags")
    @Operation(summary = "문제 태그 목록", description = "코스의 모든 문제 태그를 조회합니다")
    public ResponseEntity<ApiResponse<List<QuestionTagResponse>>> getQuestionTags(
            @PathVariable Long courseId) {
        List<QuestionTagResponse> responses = questionBankService.getAllTags(courseId);
        return ResponseEntity.ok(ApiResponse.success(responses));
    }

    @PostMapping("/courses/{courseId}/questions/import")
    @Operation(summary = "문제 일괄 등록", description = "JSON 형식으로 문제를 일괄 등록합니다")
    public ResponseEntity<ApiResponse<List<QuestionResponse>>> importQuestions(
            @Parameter(hidden = true) @RequestHeader("X-User-Id") Long userId,
            @PathVariable Long courseId,
            @RequestBody String jsonData) {
        List<QuestionResponse> responses = questionBankService.importQuestions(userId, courseId, jsonData);
        return ResponseEntity.ok(ApiResponse.success("문제가 등록되었습니다", responses));
    }

    @GetMapping(value = "/courses/{courseId}/questions/export", produces = MediaType.APPLICATION_JSON_VALUE)
    @Operation(summary = "문제 내보내기", description = "코스의 모든 문제를 JSON 형식으로 내보냅니다")
    public ResponseEntity<String> exportQuestions(
            @PathVariable Long courseId) {
        String json = questionBankService.exportQuestions(courseId);
        return ResponseEntity.ok()
            .contentType(MediaType.APPLICATION_JSON)
            .body(json);
    }

    // Quiz Endpoints

    @PostMapping("/quizzes")
    @Operation(summary = "퀴즈 생성", description = "새로운 퀴즈를 생성합니다")
    public ResponseEntity<ApiResponse<QuizResponse>> createQuiz(
            @Parameter(hidden = true) @RequestHeader("X-User-Id") Long userId,
            @Valid @RequestBody QuizCreateRequest request) {
        QuizResponse response = quizService.createQuiz(userId, request);
        return ResponseEntity.status(HttpStatus.CREATED)
            .body(ApiResponse.success("퀴즈가 생성되었습니다", response));
    }

    @GetMapping("/quizzes/{quizId}")
    @Operation(summary = "퀴즈 조회", description = "퀴즈 정보를 조회합니다")
    public ResponseEntity<ApiResponse<QuizResponse>> getQuiz(
            @PathVariable Long quizId) {
        QuizResponse response = quizService.getQuiz(quizId);
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @GetMapping("/courses/{courseId}/quizzes")
    @Operation(summary = "코스별 퀴즈 목록", description = "코스의 모든 퀴즈를 조회합니다")
    public ResponseEntity<ApiResponse<List<QuizResponse>>> getQuizzesByCourse(
            @PathVariable Long courseId) {
        List<QuizResponse> responses = quizService.getQuizzesByCourse(courseId);
        return ResponseEntity.ok(ApiResponse.success(responses));
    }

    @PutMapping("/quizzes/{quizId}")
    @Operation(summary = "퀴즈 수정", description = "퀴즈를 수정합니다")
    public ResponseEntity<ApiResponse<QuizResponse>> updateQuiz(
            @Parameter(hidden = true) @RequestHeader("X-User-Id") Long userId,
            @PathVariable Long quizId,
            @Valid @RequestBody QuizCreateRequest request) {
        QuizResponse response = quizService.updateQuiz(userId, quizId, request);
        return ResponseEntity.ok(ApiResponse.success("퀴즈가 수정되었습니다", response));
    }

    @DeleteMapping("/quizzes/{quizId}")
    @Operation(summary = "퀴즈 삭제", description = "퀴즈를 삭제합니다")
    public ResponseEntity<ApiResponse<Void>> deleteQuiz(
            @Parameter(hidden = true) @RequestHeader("X-User-Id") Long userId,
            @PathVariable Long quizId) {
        quizService.deleteQuiz(userId, quizId);
        return ResponseEntity.ok(ApiResponse.success("퀴즈가 삭제되었습니다", null));
    }

    @PostMapping("/quizzes/{quizId}/start")
    @Operation(summary = "퀴즈 시작", description = "퀴즈를 활성화합니다")
    public ResponseEntity<ApiResponse<QuizResponse>> startQuiz(
            @Parameter(hidden = true) @RequestHeader("X-User-Id") Long userId,
            @PathVariable Long quizId) {
        QuizResponse response = quizService.startQuiz(userId, quizId);
        return ResponseEntity.ok(ApiResponse.success("퀴즈가 시작되었습니다", response));
    }

    @PostMapping("/quizzes/{quizId}/stop")
    @Operation(summary = "퀴즈 종료", description = "퀴즈를 종료합니다")
    public ResponseEntity<ApiResponse<QuizResponse>> stopQuiz(
            @Parameter(hidden = true) @RequestHeader("X-User-Id") Long userId,
            @PathVariable Long quizId) {
        QuizResponse response = quizService.stopQuiz(userId, quizId);
        return ResponseEntity.ok(ApiResponse.success("퀴즈가 종료되었습니다", response));
    }

    @PostMapping("/quizzes/{quizId}/submit")
    @Operation(summary = "퀴즈 답안 제출", description = "퀴즈 답안을 제출합니다")
    public ResponseEntity<ApiResponse<Void>> submitQuizAnswer(
            @Parameter(hidden = true) @RequestHeader("X-User-Id") Long userId,
            @PathVariable Long quizId,
            @Valid @RequestBody QuizSubmitRequest request) {
        quizService.submitQuizAnswer(userId, quizId, request);
        return ResponseEntity.ok(ApiResponse.success("답안이 제출되었습니다", null));
    }

    @GetMapping("/quizzes/{quizId}/results")
    @Operation(summary = "퀴즈 결과 조회", description = "퀴즈 결과를 조회합니다")
    public ResponseEntity<ApiResponse<QuizResultResponse>> getQuizResults(
            @Parameter(hidden = true) @RequestHeader("X-User-Id") Long userId,
            @PathVariable Long quizId) {
        QuizResultResponse response = quizService.getQuizResults(quizId, userId);
        return ResponseEntity.ok(ApiResponse.success(response));
    }
}
