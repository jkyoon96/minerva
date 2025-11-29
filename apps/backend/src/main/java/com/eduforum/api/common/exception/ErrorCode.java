package com.eduforum.api.common.exception;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

/**
 * 에러 코드 정의
 */
@Getter
@RequiredArgsConstructor
public enum ErrorCode {

    // Common
    INVALID_INPUT_VALUE(HttpStatus.BAD_REQUEST, "C001", "잘못된 입력 값입니다"),
    METHOD_NOT_ALLOWED(HttpStatus.METHOD_NOT_ALLOWED, "C002", "지원하지 않는 HTTP 메서드입니다"),
    ENTITY_NOT_FOUND(HttpStatus.NOT_FOUND, "C003", "엔티티를 찾을 수 없습니다"),
    INTERNAL_SERVER_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "C004", "서버 오류가 발생했습니다"),
    INVALID_TYPE_VALUE(HttpStatus.BAD_REQUEST, "C005", "잘못된 타입입니다"),
    ACCESS_DENIED(HttpStatus.FORBIDDEN, "C006", "접근이 거부되었습니다"),

    // Authentication & Authorization
    AUTHENTICATION_FAILED(HttpStatus.UNAUTHORIZED, "A001", "인증에 실패했습니다"),
    INVALID_TOKEN(HttpStatus.UNAUTHORIZED, "A002", "유효하지 않은 토큰입니다"),
    EXPIRED_TOKEN(HttpStatus.UNAUTHORIZED, "A003", "만료된 토큰입니다"),
    UNAUTHORIZED(HttpStatus.UNAUTHORIZED, "A004", "인증이 필요합니다"),
    FORBIDDEN(HttpStatus.FORBIDDEN, "A005", "권한이 없습니다"),

    // User
    USER_NOT_FOUND(HttpStatus.NOT_FOUND, "U001", "사용자를 찾을 수 없습니다"),
    DUPLICATE_EMAIL(HttpStatus.CONFLICT, "U002", "이미 사용 중인 이메일입니다"),
    DUPLICATE_USERNAME(HttpStatus.CONFLICT, "U003", "이미 사용 중인 사용자명입니다"),
    INVALID_PASSWORD(HttpStatus.BAD_REQUEST, "U004", "잘못된 비밀번호입니다"),

    // Course
    COURSE_NOT_FOUND(HttpStatus.NOT_FOUND, "CO001", "코스를 찾을 수 없습니다"),
    COURSE_ACCESS_DENIED(HttpStatus.FORBIDDEN, "CO002", "코스에 접근할 수 없습니다"),
    ALREADY_ENROLLED(HttpStatus.CONFLICT, "CO003", "이미 등록된 코스입니다"),
    ENROLLMENT_CLOSED(HttpStatus.BAD_REQUEST, "CO004", "수강 신청 기간이 아닙니다"),

    // Session
    SESSION_NOT_FOUND(HttpStatus.NOT_FOUND, "S001", "세션을 찾을 수 없습니다"),
    SESSION_FULL(HttpStatus.BAD_REQUEST, "S002", "세션 정원이 초과되었습니다"),
    SESSION_ALREADY_STARTED(HttpStatus.BAD_REQUEST, "S003", "이미 시작된 세션입니다"),

    // Assignment
    ASSIGNMENT_NOT_FOUND(HttpStatus.NOT_FOUND, "AS001", "과제를 찾을 수 없습니다"),
    SUBMISSION_DEADLINE_PASSED(HttpStatus.BAD_REQUEST, "AS002", "제출 기한이 지났습니다"),
    ALREADY_SUBMITTED(HttpStatus.CONFLICT, "AS003", "이미 제출한 과제입니다"),

    // Seminar Room
    ROOM_NOT_FOUND(HttpStatus.NOT_FOUND, "SR001", "세미나 룸을 찾을 수 없습니다"),
    ROOM_ALREADY_EXISTS(HttpStatus.CONFLICT, "SR002", "세션에 이미 룸이 존재합니다"),
    ROOM_FULL(HttpStatus.BAD_REQUEST, "SR003", "룸 정원이 초과되었습니다"),
    ROOM_ALREADY_STARTED(HttpStatus.BAD_REQUEST, "SR004", "이미 시작된 룸입니다"),
    ROOM_ALREADY_ENDED(HttpStatus.BAD_REQUEST, "SR005", "이미 종료된 룸입니다"),
    ROOM_NOT_ACTIVE(HttpStatus.BAD_REQUEST, "SR006", "활성화된 룸이 아닙니다"),

    // Participant
    PARTICIPANT_NOT_FOUND(HttpStatus.NOT_FOUND, "SP001", "참가자를 찾을 수 없습니다"),
    ALREADY_IN_ROOM(HttpStatus.CONFLICT, "SP002", "이미 룸에 참가 중입니다"),
    NOT_IN_ROOM(HttpStatus.BAD_REQUEST, "SP003", "룸에 참가하지 않았습니다"),
    PARTICIPANT_NOT_HOST(HttpStatus.FORBIDDEN, "SP004", "호스트 권한이 필요합니다"),

    // Chat
    CHAT_MESSAGE_NOT_FOUND(HttpStatus.NOT_FOUND, "CH001", "채팅 메시지를 찾을 수 없습니다"),
    CHAT_DISABLED(HttpStatus.BAD_REQUEST, "CH002", "채팅이 비활성화되어 있습니다"),

    // Screen Share
    SCREEN_SHARE_NOT_ALLOWED(HttpStatus.FORBIDDEN, "SS001", "화면 공유 권한이 없습니다"),
    SCREEN_SHARE_IN_PROGRESS(HttpStatus.CONFLICT, "SS002", "이미 화면 공유 중입니다"),

    // Poll
    POLL_NOT_FOUND(HttpStatus.NOT_FOUND, "PL001", "투표를 찾을 수 없습니다"),
    POLL_NOT_ACTIVE(HttpStatus.BAD_REQUEST, "PL002", "활성화된 투표가 아닙니다"),
    POLL_ALREADY_RESPONDED(HttpStatus.CONFLICT, "PL003", "이미 응답한 투표입니다"),

    // Question & Quiz
    QUESTION_NOT_FOUND(HttpStatus.NOT_FOUND, "QZ001", "문제를 찾을 수 없습니다"),
    QUIZ_NOT_FOUND(HttpStatus.NOT_FOUND, "QZ002", "퀴즈를 찾을 수 없습니다"),
    QUIZ_NOT_ACTIVE(HttpStatus.BAD_REQUEST, "QZ003", "활성화된 퀴즈가 아닙니다"),
    QUIZ_ALREADY_SUBMITTED(HttpStatus.CONFLICT, "QZ004", "이미 제출한 퀴즈입니다"),
    QUIZ_TIME_EXPIRED(HttpStatus.BAD_REQUEST, "QZ005", "퀴즈 시간이 만료되었습니다"),

    // Breakout Room
    BREAKOUT_NOT_FOUND(HttpStatus.NOT_FOUND, "BR001", "분반 룸을 찾을 수 없습니다"),
    BREAKOUT_NOT_ACTIVE(HttpStatus.BAD_REQUEST, "BR002", "활성화된 분반 룸이 아닙니다"),
    BREAKOUT_FULL(HttpStatus.BAD_REQUEST, "BR003", "분반 룸이 가득 찼습니다"),

    // Whiteboard
    WHITEBOARD_NOT_FOUND(HttpStatus.NOT_FOUND, "WB001", "화이트보드를 찾을 수 없습니다"),

    // Discussion
    DISCUSSION_THREAD_NOT_FOUND(HttpStatus.NOT_FOUND, "DS001", "토론 스레드를 찾을 수 없습니다"),
    SPEAKING_QUEUE_NOT_FOUND(HttpStatus.NOT_FOUND, "DS002", "발언 대기열을 찾을 수 없습니다"),
    ALREADY_IN_QUEUE(HttpStatus.CONFLICT, "DS003", "이미 발언 대기 중입니다"),

    // Assessment - Grading
    GRADING_RESULT_NOT_FOUND(HttpStatus.NOT_FOUND, "GR001", "채점 결과를 찾을 수 없습니다"),
    GRADING_ALREADY_FINALIZED(HttpStatus.BAD_REQUEST, "GR002", "이미 확정된 채점 결과입니다"),
    ANSWER_STATISTICS_NOT_FOUND(HttpStatus.NOT_FOUND, "GR003", "답변 통계를 찾을 수 없습니다"),

    // Assessment - Code Evaluation
    CODE_SUBMISSION_NOT_FOUND(HttpStatus.NOT_FOUND, "CE001", "코드 제출을 찾을 수 없습니다"),
    CODE_EXECUTION_FAILED(HttpStatus.INTERNAL_SERVER_ERROR, "CE002", "코드 실행에 실패했습니다"),
    COMPILATION_ERROR(HttpStatus.BAD_REQUEST, "CE003", "컴파일 오류가 발생했습니다"),
    TEST_CASE_NOT_FOUND(HttpStatus.NOT_FOUND, "CE004", "테스트 케이스를 찾을 수 없습니다"),

    // Assessment - Plagiarism
    PLAGIARISM_REPORT_NOT_FOUND(HttpStatus.NOT_FOUND, "PG001", "표절 검사 보고서를 찾을 수 없습니다"),
    INSUFFICIENT_SUBMISSIONS(HttpStatus.BAD_REQUEST, "PG002", "표절 검사를 위한 제출이 부족합니다"),

    // Assessment - Feedback
    FEEDBACK_NOT_FOUND(HttpStatus.NOT_FOUND, "FB001", "피드백을 찾을 수 없습니다"),
    LEARNING_RESOURCE_NOT_FOUND(HttpStatus.NOT_FOUND, "FB002", "학습 자료를 찾을 수 없습니다"),

    // Assessment - Participation
    PARTICIPATION_EVENT_NOT_FOUND(HttpStatus.NOT_FOUND, "PT001", "참여 이벤트를 찾을 수 없습니다"),
    PARTICIPATION_SCORE_NOT_FOUND(HttpStatus.NOT_FOUND, "PT002", "참여 점수를 찾을 수 없습니다"),
    PARTICIPATION_WEIGHT_NOT_FOUND(HttpStatus.NOT_FOUND, "PT003", "참여 가중치를 찾을 수 없습니다"),

    // Assessment - Peer Review
    PEER_REVIEW_NOT_FOUND(HttpStatus.NOT_FOUND, "PR001", "동료 평가를 찾을 수 없습니다"),
    PEER_REVIEW_NOT_ASSIGNED(HttpStatus.FORBIDDEN, "PR002", "평가 권한이 없습니다"),
    PEER_REVIEW_DEADLINE_PASSED(HttpStatus.BAD_REQUEST, "PR003", "동료 평가 기한이 지났습니다"),
    PEER_REVIEW_ALREADY_SUBMITTED(HttpStatus.CONFLICT, "PR004", "이미 제출한 평가입니다"),
    PEER_REVIEW_NOT_SETUP(HttpStatus.BAD_REQUEST, "PR005", "동료 평가가 설정되지 않았습니다");

    private final HttpStatus httpStatus;
    private final String code;
    private final String message;

    public int getStatus() {
        return httpStatus.value();
    }
}
