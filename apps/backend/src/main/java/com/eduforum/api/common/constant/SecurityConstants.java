package com.eduforum.api.common.constant;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

/**
 * 보안 관련 상수 정의
 *
 * - JWT 관련 상수
 * - 인증/인가 관련 상수
 * - 보안 헤더
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class SecurityConstants {

    // ========== JWT 토큰 ==========

    /**
     * JWT 토큰 타입
     */
    public static final String TOKEN_TYPE = "Bearer";

    /**
     * JWT 토큰 헤더 이름
     */
    public static final String TOKEN_HEADER = "Authorization";

    /**
     * JWT 토큰 접두사
     */
    public static final String TOKEN_PREFIX = "Bearer ";

    /**
     * JWT Claims - User ID
     */
    public static final String CLAIM_USER_ID = "userId";

    /**
     * JWT Claims - Username (Email)
     */
    public static final String CLAIM_USERNAME = "username";

    /**
     * JWT Claims - Role
     */
    public static final String CLAIM_ROLE = "role";

    /**
     * JWT Claims - Token Type
     */
    public static final String CLAIM_TOKEN_TYPE = "tokenType";

    /**
     * Access Token Type
     */
    public static final String TOKEN_TYPE_ACCESS = "ACCESS";

    /**
     * Refresh Token Type
     */
    public static final String TOKEN_TYPE_REFRESH = "REFRESH";

    // ========== 권한 (Roles) ==========

    /**
     * 관리자 권한
     */
    public static final String ROLE_ADMIN = "ROLE_ADMIN";

    /**
     * 교수 권한
     */
    public static final String ROLE_PROFESSOR = "ROLE_PROFESSOR";

    /**
     * 학생 권한
     */
    public static final String ROLE_STUDENT = "ROLE_STUDENT";

    /**
     * 조교 권한
     */
    public static final String ROLE_TA = "ROLE_TA";

    /**
     * 게스트 권한
     */
    public static final String ROLE_GUEST = "ROLE_GUEST";

    // ========== 권한 이름 (Spring Security용) ==========

    /**
     * 관리자 권한 이름
     */
    public static final String AUTHORITY_ADMIN = "ADMIN";

    /**
     * 교수 권한 이름
     */
    public static final String AUTHORITY_PROFESSOR = "PROFESSOR";

    /**
     * 학생 권한 이름
     */
    public static final String AUTHORITY_STUDENT = "STUDENT";

    /**
     * 조교 권한 이름
     */
    public static final String AUTHORITY_TA = "TA";

    /**
     * 게스트 권한 이름
     */
    public static final String AUTHORITY_GUEST = "GUEST";

    // ========== 공개 경로 (인증 불필요) ==========

    /**
     * 공개 API 경로 배열
     */
    public static final String[] PUBLIC_URLS = {
        "/api/v1/auth/**",           // 인증 API
        "/api/actuator/**",          // Actuator
        "/api/docs/**",              // Swagger/API 문서
        "/api/health",               // Health Check
        "/favicon.ico",              // Favicon
        "/error"                     // Error
    };

    /**
     * Swagger/API 문서 경로
     */
    public static final String[] SWAGGER_URLS = {
        "/api/docs/**",
        "/api/docs/api-docs/**",
        "/api/docs/swagger-ui/**",
        "/api/docs/swagger-ui.html"
    };

    // ========== 보안 헤더 ==========

    /**
     * CORS 허용 Origin 헤더
     */
    public static final String CORS_ALLOW_ORIGIN = "Access-Control-Allow-Origin";

    /**
     * CORS 허용 메서드 헤더
     */
    public static final String CORS_ALLOW_METHODS = "Access-Control-Allow-Methods";

    /**
     * CORS 허용 헤더
     */
    public static final String CORS_ALLOW_HEADERS = "Access-Control-Allow-Headers";

    /**
     * CORS 자격증명 허용
     */
    public static final String CORS_ALLOW_CREDENTIALS = "Access-Control-Allow-Credentials";

    /**
     * X-Frame-Options 헤더 값
     */
    public static final String X_FRAME_OPTIONS_VALUE = "DENY";

    /**
     * X-Content-Type-Options 헤더 값
     */
    public static final String X_CONTENT_TYPE_OPTIONS_VALUE = "nosniff";

    /**
     * X-XSS-Protection 헤더 값
     */
    public static final String X_XSS_PROTECTION_VALUE = "1; mode=block";

    // ========== 비밀번호 정책 ==========

    /**
     * 비밀번호 최소 길이
     */
    public static final int PASSWORD_MIN_LENGTH = 8;

    /**
     * 비밀번호 최대 길이
     */
    public static final int PASSWORD_MAX_LENGTH = 20;

    /**
     * 비밀번호 해싱 강도 (BCrypt)
     */
    public static final int PASSWORD_STRENGTH = 10;

    /**
     * 로그인 실패 최대 횟수
     */
    public static final int MAX_LOGIN_ATTEMPTS = 5;

    /**
     * 계정 잠금 시간 (분)
     */
    public static final int ACCOUNT_LOCK_DURATION_MINUTES = 30;

    // ========== 세션/토큰 유효기간 ==========

    /**
     * Access Token 유효기간 (1시간)
     */
    public static final long ACCESS_TOKEN_VALIDITY_SECONDS = 60 * 60;

    /**
     * Refresh Token 유효기간 (14일)
     */
    public static final long REFRESH_TOKEN_VALIDITY_SECONDS = 60 * 60 * 24 * 14;

    /**
     * 2FA 인증 코드 유효기간 (5분)
     */
    public static final long TWO_FACTOR_CODE_VALIDITY_SECONDS = 60 * 5;

    /**
     * 이메일 인증 토큰 유효기간 (24시간)
     */
    public static final long EMAIL_VERIFICATION_TOKEN_VALIDITY_SECONDS = 60 * 60 * 24;

    /**
     * 비밀번호 재설정 토큰 유효기간 (1시간)
     */
    public static final long PASSWORD_RESET_TOKEN_VALIDITY_SECONDS = 60 * 60;

    // ========== Rate Limiting ==========

    /**
     * API 요청 제한 - 분당 요청 수
     */
    public static final int RATE_LIMIT_PER_MINUTE = 100;

    /**
     * 로그인 요청 제한 - 분당 요청 수
     */
    public static final int LOGIN_RATE_LIMIT_PER_MINUTE = 5;

    /**
     * 비밀번호 재설정 요청 제한 - 시간당 요청 수
     */
    public static final int PASSWORD_RESET_RATE_LIMIT_PER_HOUR = 3;

    // ========== 암호화 ==========

    /**
     * AES 암호화 알고리즘
     */
    public static final String AES_ALGORITHM = "AES";

    /**
     * AES 암호화 모드
     */
    public static final String AES_TRANSFORMATION = "AES/CBC/PKCS5Padding";

    /**
     * 해시 알고리즘
     */
    public static final String HASH_ALGORITHM = "SHA-256";

    // ========== 기타 ==========

    /**
     * 익명 사용자 이름
     */
    public static final String ANONYMOUS_USER = "anonymousUser";

    /**
     * 시스템 사용자 이름
     */
    public static final String SYSTEM_USER = "SYSTEM";

    /**
     * 기본 프로필 이미지 URL
     */
    public static final String DEFAULT_PROFILE_IMAGE_URL = "/images/default-profile.png";
}
