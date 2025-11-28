package com.eduforum.api.common.util;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import java.util.regex.Pattern;

/**
 * 문자열 유틸리티 클래스
 *
 * 기능:
 * - null/공백 체크
 * - 문자열 변환
 * - 마스킹 처리
 * - 유효성 검사 (이메일, 전화번호 등)
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class StringUtil {

    // 정규식 패턴
    private static final Pattern EMAIL_PATTERN = Pattern.compile(
        "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$"
    );
    private static final Pattern PHONE_PATTERN = Pattern.compile(
        "^01[016789]-?\\d{3,4}-?\\d{4}$"
    );
    private static final Pattern STUDENT_ID_PATTERN = Pattern.compile(
        "^\\d{8,10}$"
    );

    // ========== Null/공백 체크 ==========

    /**
     * 문자열이 null 또는 빈 문자열("")인지 확인
     */
    public static boolean isEmpty(String str) {
        return str == null || str.isEmpty();
    }

    /**
     * 문자열이 null이 아니고 비어있지 않은지 확인
     */
    public static boolean isNotEmpty(String str) {
        return !isEmpty(str);
    }

    /**
     * 문자열이 null, 빈 문자열(""), 공백 문자열("  ")인지 확인
     */
    public static boolean isBlank(String str) {
        return str == null || str.trim().isEmpty();
    }

    /**
     * 문자열이 null이 아니고 공백이 아닌지 확인
     */
    public static boolean isNotBlank(String str) {
        return !isBlank(str);
    }

    // ========== 기본값 처리 ==========

    /**
     * 문자열이 null이면 기본값 반환
     */
    public static String defaultIfNull(String str, String defaultValue) {
        return str != null ? str : defaultValue;
    }

    /**
     * 문자열이 null이면 빈 문자열 반환
     */
    public static String defaultString(String str) {
        return defaultIfNull(str, "");
    }

    /**
     * 문자열이 null이거나 공백이면 기본값 반환
     */
    public static String defaultIfBlank(String str, String defaultValue) {
        return isNotBlank(str) ? str : defaultValue;
    }

    // ========== 문자열 변환 ==========

    /**
     * 문자열 앞뒤 공백 제거 (null-safe)
     */
    public static String trim(String str) {
        return str != null ? str.trim() : null;
    }

    /**
     * 문자열 앞뒤 공백 제거 후 빈 문자열이면 null 반환
     */
    public static String trimToNull(String str) {
        String trimmed = trim(str);
        return isEmpty(trimmed) ? null : trimmed;
    }

    /**
     * 문자열 소문자 변환 (null-safe)
     */
    public static String toLowerCase(String str) {
        return str != null ? str.toLowerCase() : null;
    }

    /**
     * 문자열 대문자 변환 (null-safe)
     */
    public static String toUpperCase(String str) {
        return str != null ? str.toUpperCase() : null;
    }

    /**
     * 문자열 첫 글자만 대문자로 변환
     */
    public static String capitalize(String str) {
        if (isEmpty(str)) {
            return str;
        }
        return str.substring(0, 1).toUpperCase() + str.substring(1).toLowerCase();
    }

    // ========== 마스킹 ==========

    /**
     * 이메일 마스킹 (예: abc@example.com -> a**@example.com)
     */
    public static String maskEmail(String email) {
        if (isEmpty(email) || !email.contains("@")) {
            return email;
        }

        String[] parts = email.split("@");
        String username = parts[0];
        String domain = parts[1];

        if (username.length() <= 1) {
            return email;
        }

        String masked = username.charAt(0) + "**";
        return masked + "@" + domain;
    }

    /**
     * 전화번호 마스킹 (예: 010-1234-5678 -> 010-****-5678)
     */
    public static String maskPhone(String phone) {
        if (isEmpty(phone)) {
            return phone;
        }

        // 숫자만 추출
        String numbers = phone.replaceAll("[^0-9]", "");

        if (numbers.length() < 10) {
            return phone;
        }

        // 010-****-5678 형식
        return numbers.substring(0, 3) + "-****-" + numbers.substring(numbers.length() - 4);
    }

    /**
     * 이름 마스킹 (예: 홍길동 -> 홍*동)
     */
    public static String maskName(String name) {
        if (isEmpty(name)) {
            return name;
        }

        if (name.length() <= 1) {
            return name;
        }

        if (name.length() == 2) {
            return name.charAt(0) + "*";
        }

        // 3글자 이상: 중간 글자들을 * 처리
        StringBuilder masked = new StringBuilder();
        masked.append(name.charAt(0));
        for (int i = 1; i < name.length() - 1; i++) {
            masked.append("*");
        }
        masked.append(name.charAt(name.length() - 1));

        return masked.toString();
    }

    /**
     * 학번 마스킹 (예: 20231234 -> 2023****)
     */
    public static String maskStudentId(String studentId) {
        if (isEmpty(studentId) || studentId.length() < 4) {
            return studentId;
        }

        return studentId.substring(0, 4) + "****";
    }

    // ========== 유효성 검사 ==========

    /**
     * 이메일 형식 유효성 검사
     */
    public static boolean isValidEmail(String email) {
        return isNotBlank(email) && EMAIL_PATTERN.matcher(email).matches();
    }

    /**
     * 전화번호 형식 유효성 검사 (한국 휴대폰 번호)
     */
    public static boolean isValidPhone(String phone) {
        if (isBlank(phone)) {
            return false;
        }
        return PHONE_PATTERN.matcher(phone).matches();
    }

    /**
     * 학번 형식 유효성 검사 (8~10자리 숫자)
     */
    public static boolean isValidStudentId(String studentId) {
        if (isBlank(studentId)) {
            return false;
        }
        return STUDENT_ID_PATTERN.matcher(studentId).matches();
    }

    /**
     * URL 형식 유효성 검사 (간단한 검사)
     */
    public static boolean isValidUrl(String url) {
        if (isBlank(url)) {
            return false;
        }
        return url.startsWith("http://") || url.startsWith("https://");
    }

    // ========== 문자열 조작 ==========

    /**
     * 문자열 반복
     */
    public static String repeat(String str, int count) {
        if (isEmpty(str) || count <= 0) {
            return "";
        }
        return str.repeat(count);
    }

    /**
     * 문자열 좌측 패딩
     */
    public static String leftPad(String str, int size, char padChar) {
        if (str == null) {
            str = "";
        }
        int pads = size - str.length();
        if (pads <= 0) {
            return str;
        }
        return repeat(String.valueOf(padChar), pads) + str;
    }

    /**
     * 문자열 우측 패딩
     */
    public static String rightPad(String str, int size, char padChar) {
        if (str == null) {
            str = "";
        }
        int pads = size - str.length();
        if (pads <= 0) {
            return str;
        }
        return str + repeat(String.valueOf(padChar), pads);
    }

    /**
     * 문자열 길이 제한 (초과 시 말줄임표 추가)
     */
    public static String truncate(String str, int maxLength) {
        if (isEmpty(str) || str.length() <= maxLength) {
            return str;
        }
        return str.substring(0, maxLength - 3) + "...";
    }

    /**
     * 문자열이 특정 패턴과 일치하는지 확인
     */
    public static boolean matches(String str, String regex) {
        if (str == null || regex == null) {
            return false;
        }
        return str.matches(regex);
    }

    // ========== 문자열 비교 ==========

    /**
     * 두 문자열이 같은지 비교 (null-safe)
     */
    public static boolean equals(String str1, String str2) {
        if (str1 == null && str2 == null) {
            return true;
        }
        if (str1 == null || str2 == null) {
            return false;
        }
        return str1.equals(str2);
    }

    /**
     * 두 문자열이 같은지 비교 (대소문자 무시, null-safe)
     */
    public static boolean equalsIgnoreCase(String str1, String str2) {
        if (str1 == null && str2 == null) {
            return true;
        }
        if (str1 == null || str2 == null) {
            return false;
        }
        return str1.equalsIgnoreCase(str2);
    }

    /**
     * 문자열이 특정 문자열로 시작하는지 확인 (null-safe)
     */
    public static boolean startsWith(String str, String prefix) {
        if (str == null || prefix == null) {
            return false;
        }
        return str.startsWith(prefix);
    }

    /**
     * 문자열이 특정 문자열로 끝나는지 확인 (null-safe)
     */
    public static boolean endsWith(String str, String suffix) {
        if (str == null || suffix == null) {
            return false;
        }
        return str.endsWith(suffix);
    }

    /**
     * 문자열에 특정 문자열이 포함되는지 확인 (null-safe)
     */
    public static boolean contains(String str, String searchStr) {
        if (str == null || searchStr == null) {
            return false;
        }
        return str.contains(searchStr);
    }
}
