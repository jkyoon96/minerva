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
    ALREADY_SUBMITTED(HttpStatus.CONFLICT, "AS003", "이미 제출한 과제입니다");

    private final HttpStatus httpStatus;
    private final String code;
    private final String message;

    public int getStatus() {
        return httpStatus.value();
    }
}
