package com.eduforum.api.common.constant;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

/**
 * API 관련 상수 정의
 *
 * - API 버전
 * - 공통 경로
 * - 페이징 기본값
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class ApiConstants {

    // ========== API 버전 ==========

    /**
     * API 버전
     */
    public static final String API_VERSION = "v1";

    /**
     * API 버전 경로
     */
    public static final String API_VERSION_PATH = "/v1";

    // ========== 공통 경로 ==========

    /**
     * 인증 관련 API 경로
     */
    public static final String AUTH_API_PATH = API_VERSION_PATH + "/auth";

    /**
     * 사용자 관련 API 경로
     */
    public static final String USERS_API_PATH = API_VERSION_PATH + "/users";

    /**
     * 코스 관련 API 경로
     */
    public static final String COURSES_API_PATH = API_VERSION_PATH + "/courses";

    /**
     * 세션 관련 API 경로
     */
    public static final String SESSIONS_API_PATH = API_VERSION_PATH + "/sessions";

    /**
     * 과제 관련 API 경로
     */
    public static final String ASSIGNMENTS_API_PATH = API_VERSION_PATH + "/assignments";

    /**
     * 평가 관련 API 경로
     */
    public static final String ASSESSMENTS_API_PATH = API_VERSION_PATH + "/assessments";

    /**
     * 분석 관련 API 경로
     */
    public static final String ANALYTICS_API_PATH = API_VERSION_PATH + "/analytics";

    /**
     * 관리자 관련 API 경로
     */
    public static final String ADMIN_API_PATH = API_VERSION_PATH + "/admin";

    // ========== 페이징 기본값 ==========

    /**
     * 기본 페이지 번호
     */
    public static final int DEFAULT_PAGE_NUMBER = 0;

    /**
     * 기본 페이지 크기
     */
    public static final int DEFAULT_PAGE_SIZE = 20;

    /**
     * 최대 페이지 크기
     */
    public static final int MAX_PAGE_SIZE = 100;

    /**
     * 기본 정렬 필드
     */
    public static final String DEFAULT_SORT_FIELD = "createdAt";

    /**
     * 기본 정렬 방향
     */
    public static final String DEFAULT_SORT_DIRECTION = "DESC";

    // ========== 파일 업로드 ==========

    /**
     * 허용되는 이미지 파일 확장자
     */
    public static final String[] ALLOWED_IMAGE_EXTENSIONS = {
        "jpg", "jpeg", "png", "gif", "webp"
    };

    /**
     * 허용되는 문서 파일 확장자
     */
    public static final String[] ALLOWED_DOCUMENT_EXTENSIONS = {
        "pdf", "doc", "docx", "ppt", "pptx", "xls", "xlsx", "txt", "hwp"
    };

    /**
     * 최대 이미지 파일 크기 (10MB)
     */
    public static final long MAX_IMAGE_FILE_SIZE = 10 * 1024 * 1024;

    /**
     * 최대 문서 파일 크기 (50MB)
     */
    public static final long MAX_DOCUMENT_FILE_SIZE = 50 * 1024 * 1024;

    // ========== 캐시 ==========

    /**
     * 캐시 TTL (초) - 5분
     */
    public static final long CACHE_TTL_SECONDS = 5 * 60;

    /**
     * 캐시 이름 - 사용자
     */
    public static final String CACHE_USERS = "users";

    /**
     * 캐시 이름 - 코스
     */
    public static final String CACHE_COURSES = "courses";

    // ========== HTTP 헤더 ==========

    /**
     * Authorization 헤더
     */
    public static final String HEADER_AUTHORIZATION = "Authorization";

    /**
     * Bearer 토큰 접두사
     */
    public static final String BEARER_PREFIX = "Bearer ";

    /**
     * Content-Type 헤더
     */
    public static final String HEADER_CONTENT_TYPE = "Content-Type";

    /**
     * X-Request-ID 헤더
     */
    public static final String HEADER_REQUEST_ID = "X-Request-ID";

    /**
     * X-User-ID 헤더
     */
    public static final String HEADER_USER_ID = "X-User-ID";

    // ========== 날짜/시간 포맷 ==========

    /**
     * ISO 8601 날짜/시간 포맷
     */
    public static final String ISO_DATETIME_FORMAT = "yyyy-MM-dd'T'HH:mm:ss";

    /**
     * 기본 날짜 포맷
     */
    public static final String DEFAULT_DATE_FORMAT = "yyyy-MM-dd";

    /**
     * 기본 날짜/시간 포맷
     */
    public static final String DEFAULT_DATETIME_FORMAT = "yyyy-MM-dd HH:mm:ss";

    // ========== 정규식 패턴 ==========

    /**
     * 이메일 정규식 패턴
     */
    public static final String EMAIL_REGEX = "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$";

    /**
     * 전화번호 정규식 패턴 (한국)
     */
    public static final String PHONE_REGEX = "^01[016789]-?\\d{3,4}-?\\d{4}$";

    /**
     * 학번 정규식 패턴 (8~10자리 숫자)
     */
    public static final String STUDENT_ID_REGEX = "^\\d{8,10}$";

    /**
     * 비밀번호 정규식 패턴 (8~20자, 영문 대소문자, 숫자, 특수문자 조합)
     */
    public static final String PASSWORD_REGEX = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[@$!%*?&])[A-Za-z\\d@$!%*?&]{8,20}$";

    // ========== 응답 메시지 ==========

    /**
     * 성공 메시지
     */
    public static final String SUCCESS_MESSAGE = "요청이 성공적으로 처리되었습니다.";

    /**
     * 생성 성공 메시지
     */
    public static final String CREATED_MESSAGE = "리소스가 성공적으로 생성되었습니다.";

    /**
     * 수정 성공 메시지
     */
    public static final String UPDATED_MESSAGE = "리소스가 성공적으로 수정되었습니다.";

    /**
     * 삭제 성공 메시지
     */
    public static final String DELETED_MESSAGE = "리소스가 성공적으로 삭제되었습니다.";
}
