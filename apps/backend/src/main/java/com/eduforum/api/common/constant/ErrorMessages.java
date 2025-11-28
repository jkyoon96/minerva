package com.eduforum.api.common.constant;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

/**
 * 공통 에러 메시지 정의 (한국어)
 *
 * - 인증/인가 관련 에러
 * - 유효성 검증 에러
 * - 리소스 관련 에러
 * - 시스템 에러
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class ErrorMessages {

    // ========== 인증/인가 에러 ==========

    /**
     * 인증 실패
     */
    public static final String AUTHENTICATION_FAILED = "인증에 실패했습니다.";

    /**
     * 인증 토큰 없음
     */
    public static final String MISSING_TOKEN = "인증 토큰이 필요합니다.";

    /**
     * 유효하지 않은 토큰
     */
    public static final String INVALID_TOKEN = "유효하지 않은 토큰입니다.";

    /**
     * 만료된 토큰
     */
    public static final String EXPIRED_TOKEN = "만료된 토큰입니다.";

    /**
     * 권한 없음
     */
    public static final String ACCESS_DENIED = "접근 권한이 없습니다.";

    /**
     * 잘못된 자격증명
     */
    public static final String INVALID_CREDENTIALS = "이메일 또는 비밀번호가 올바르지 않습니다.";

    /**
     * 계정 잠김
     */
    public static final String ACCOUNT_LOCKED = "계정이 잠겼습니다. 잠시 후 다시 시도해주세요.";

    /**
     * 계정 비활성화
     */
    public static final String ACCOUNT_DISABLED = "비활성화된 계정입니다.";

    /**
     * 이메일 미인증
     */
    public static final String EMAIL_NOT_VERIFIED = "이메일 인증이 필요합니다.";

    /**
     * 2FA 인증 필요
     */
    public static final String TWO_FACTOR_REQUIRED = "2단계 인증이 필요합니다.";

    /**
     * 잘못된 2FA 코드
     */
    public static final String INVALID_TWO_FACTOR_CODE = "유효하지 않은 인증 코드입니다.";

    // ========== 사용자 관련 에러 ==========

    /**
     * 사용자를 찾을 수 없음
     */
    public static final String USER_NOT_FOUND = "사용자를 찾을 수 없습니다.";

    /**
     * 이메일 중복
     */
    public static final String EMAIL_ALREADY_EXISTS = "이미 사용 중인 이메일입니다.";

    /**
     * 학번 중복
     */
    public static final String STUDENT_ID_ALREADY_EXISTS = "이미 사용 중인 학번입니다.";

    /**
     * 비밀번호 불일치
     */
    public static final String PASSWORD_MISMATCH = "비밀번호가 일치하지 않습니다.";

    /**
     * 현재 비밀번호 불일치
     */
    public static final String CURRENT_PASSWORD_INCORRECT = "현재 비밀번호가 올바르지 않습니다.";

    /**
     * 비밀번호 형식 오류
     */
    public static final String INVALID_PASSWORD_FORMAT =
        "비밀번호는 8~20자의 영문 대소문자, 숫자, 특수문자를 조합해야 합니다.";

    /**
     * 이메일 형식 오류
     */
    public static final String INVALID_EMAIL_FORMAT = "유효하지 않은 이메일 형식입니다.";

    /**
     * 전화번호 형식 오류
     */
    public static final String INVALID_PHONE_FORMAT = "유효하지 않은 전화번호 형식입니다.";

    /**
     * 학번 형식 오류
     */
    public static final String INVALID_STUDENT_ID_FORMAT = "유효하지 않은 학번 형식입니다.";

    // ========== 코스 관련 에러 ==========

    /**
     * 코스를 찾을 수 없음
     */
    public static final String COURSE_NOT_FOUND = "코스를 찾을 수 없습니다.";

    /**
     * 코스 코드 중복
     */
    public static final String COURSE_CODE_ALREADY_EXISTS = "이미 사용 중인 코스 코드입니다.";

    /**
     * 코스 정원 초과
     */
    public static final String COURSE_CAPACITY_EXCEEDED = "코스 정원이 초과되었습니다.";

    /**
     * 이미 수강 중인 코스
     */
    public static final String ALREADY_ENROLLED = "이미 수강 중인 코스입니다.";

    /**
     * 수강 중이 아님
     */
    public static final String NOT_ENROLLED = "수강 중인 코스가 아닙니다.";

    /**
     * 코스 수강신청 기간 아님
     */
    public static final String ENROLLMENT_PERIOD_CLOSED = "수강신청 기간이 아닙니다.";

    // ========== 세션 관련 에러 ==========

    /**
     * 세션을 찾을 수 없음
     */
    public static final String SESSION_NOT_FOUND = "세션을 찾을 수 없습니다.";

    /**
     * 세션 시작 시간 오류
     */
    public static final String INVALID_SESSION_TIME = "세션 시작 시간은 종료 시간보다 이전이어야 합니다.";

    /**
     * 세션이 아직 시작되지 않음
     */
    public static final String SESSION_NOT_STARTED = "세션이 아직 시작되지 않았습니다.";

    /**
     * 세션이 이미 종료됨
     */
    public static final String SESSION_ALREADY_ENDED = "세션이 이미 종료되었습니다.";

    /**
     * 세션 정원 초과
     */
    public static final String SESSION_CAPACITY_EXCEEDED = "세션 정원이 초과되었습니다.";

    // ========== 과제 관련 에러 ==========

    /**
     * 과제를 찾을 수 없음
     */
    public static final String ASSIGNMENT_NOT_FOUND = "과제를 찾을 수 없습니다.";

    /**
     * 제출을 찾을 수 없음
     */
    public static final String SUBMISSION_NOT_FOUND = "제출물을 찾을 수 없습니다.";

    /**
     * 제출 마감 기한 초과
     */
    public static final String SUBMISSION_DEADLINE_PASSED = "제출 마감 기한이 지났습니다.";

    /**
     * 이미 제출함
     */
    public static final String ALREADY_SUBMITTED = "이미 제출한 과제입니다.";

    /**
     * 과제 제출 파일 없음
     */
    public static final String MISSING_SUBMISSION_FILE = "제출 파일이 필요합니다.";

    // ========== 파일 관련 에러 ==========

    /**
     * 파일을 찾을 수 없음
     */
    public static final String FILE_NOT_FOUND = "파일을 찾을 수 없습니다.";

    /**
     * 파일 업로드 실패
     */
    public static final String FILE_UPLOAD_FAILED = "파일 업로드에 실패했습니다.";

    /**
     * 파일 크기 초과
     */
    public static final String FILE_SIZE_EXCEEDED = "파일 크기가 제한을 초과했습니다.";

    /**
     * 지원하지 않는 파일 형식
     */
    public static final String UNSUPPORTED_FILE_TYPE = "지원하지 않는 파일 형식입니다.";

    /**
     * 파일이 비어있음
     */
    public static final String EMPTY_FILE = "빈 파일은 업로드할 수 없습니다.";

    // ========== 유효성 검증 에러 ==========

    /**
     * 필수 필드 누락
     */
    public static final String REQUIRED_FIELD_MISSING = "필수 필드가 누락되었습니다.";

    /**
     * 잘못된 입력값
     */
    public static final String INVALID_INPUT = "잘못된 입력값입니다.";

    /**
     * 잘못된 파라미터
     */
    public static final String INVALID_PARAMETER = "잘못된 파라미터입니다.";

    /**
     * 잘못된 요청
     */
    public static final String INVALID_REQUEST = "잘못된 요청입니다.";

    /**
     * 잘못된 날짜 형식
     */
    public static final String INVALID_DATE_FORMAT = "잘못된 날짜 형식입니다.";

    /**
     * 잘못된 시간 범위
     */
    public static final String INVALID_TIME_RANGE = "시작 시간은 종료 시간보다 이전이어야 합니다.";

    /**
     * 값이 너무 작음
     */
    public static final String VALUE_TOO_SMALL = "값이 최소값보다 작습니다.";

    /**
     * 값이 너무 큼
     */
    public static final String VALUE_TOO_LARGE = "값이 최대값보다 큽니다.";

    /**
     * 문자열 길이 초과
     */
    public static final String STRING_TOO_LONG = "문자열 길이가 최대 길이를 초과했습니다.";

    /**
     * 문자열 길이 부족
     */
    public static final String STRING_TOO_SHORT = "문자열 길이가 최소 길이보다 짧습니다.";

    // ========== 리소스 에러 ==========

    /**
     * 리소스를 찾을 수 없음
     */
    public static final String RESOURCE_NOT_FOUND = "요청한 리소스를 찾을 수 없습니다.";

    /**
     * 리소스 중복
     */
    public static final String RESOURCE_ALREADY_EXISTS = "이미 존재하는 리소스입니다.";

    /**
     * 리소스 삭제 불가
     */
    public static final String RESOURCE_CANNOT_BE_DELETED = "삭제할 수 없는 리소스입니다.";

    /**
     * 리소스 수정 불가
     */
    public static final String RESOURCE_CANNOT_BE_UPDATED = "수정할 수 없는 리소스입니다.";

    // ========== 시스템 에러 ==========

    /**
     * 내부 서버 오류
     */
    public static final String INTERNAL_SERVER_ERROR = "서버 내부 오류가 발생했습니다.";

    /**
     * 서비스 이용 불가
     */
    public static final String SERVICE_UNAVAILABLE = "서비스를 일시적으로 이용할 수 없습니다.";

    /**
     * 데이터베이스 오류
     */
    public static final String DATABASE_ERROR = "데이터베이스 오류가 발생했습니다.";

    /**
     * 외부 API 오류
     */
    public static final String EXTERNAL_API_ERROR = "외부 API 호출 중 오류가 발생했습니다.";

    /**
     * 타임아웃
     */
    public static final String TIMEOUT = "요청 시간이 초과되었습니다.";

    /**
     * 네트워크 오류
     */
    public static final String NETWORK_ERROR = "네트워크 오류가 발생했습니다.";

    // ========== 페이징 에러 ==========

    /**
     * 잘못된 페이지 번호
     */
    public static final String INVALID_PAGE_NUMBER = "잘못된 페이지 번호입니다.";

    /**
     * 잘못된 페이지 크기
     */
    public static final String INVALID_PAGE_SIZE = "잘못된 페이지 크기입니다.";

    /**
     * 페이지 크기 초과
     */
    public static final String PAGE_SIZE_EXCEEDED = "페이지 크기가 최대값을 초과했습니다.";

    // ========== 기타 ==========

    /**
     * 알 수 없는 오류
     */
    public static final String UNKNOWN_ERROR = "알 수 없는 오류가 발생했습니다.";

    /**
     * 작업 실패
     */
    public static final String OPERATION_FAILED = "작업 수행에 실패했습니다.";

    /**
     * 중복 요청
     */
    public static final String DUPLICATE_REQUEST = "중복된 요청입니다.";

    /**
     * 요청 횟수 제한 초과
     */
    public static final String RATE_LIMIT_EXCEEDED = "요청 횟수 제한을 초과했습니다.";
}
