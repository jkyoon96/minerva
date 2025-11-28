package com.eduforum.api.common.util;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import java.time.*;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;

/**
 * 날짜/시간 유틸리티 클래스
 *
 * 기능:
 * - 날짜/시간 포맷팅
 * - 날짜/시간 파싱
 * - 날짜 계산 (더하기/빼기)
 * - 시간대 변환
 * - 날짜 비교
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class DateTimeUtil {

    // 공통 포맷 패턴
    public static final String DEFAULT_DATE_FORMAT = "yyyy-MM-dd";
    public static final String DEFAULT_DATETIME_FORMAT = "yyyy-MM-dd HH:mm:ss";
    public static final String ISO_DATETIME_FORMAT = "yyyy-MM-dd'T'HH:mm:ss";
    public static final String KOREAN_DATE_FORMAT = "yyyy년 MM월 dd일";
    public static final String KOREAN_DATETIME_FORMAT = "yyyy년 MM월 dd일 HH시 mm분";

    // DateTimeFormatter 상수
    public static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern(DEFAULT_DATE_FORMAT);
    public static final DateTimeFormatter DATETIME_FORMATTER = DateTimeFormatter.ofPattern(DEFAULT_DATETIME_FORMAT);
    public static final DateTimeFormatter ISO_DATETIME_FORMATTER = DateTimeFormatter.ofPattern(ISO_DATETIME_FORMAT);
    public static final DateTimeFormatter KOREAN_DATE_FORMATTER = DateTimeFormatter.ofPattern(KOREAN_DATE_FORMAT);
    public static final DateTimeFormatter KOREAN_DATETIME_FORMATTER = DateTimeFormatter.ofPattern(KOREAN_DATETIME_FORMAT);

    // 기본 시간대 (서울)
    public static final ZoneId ZONE_SEOUL = ZoneId.of("Asia/Seoul");
    public static final ZoneId ZONE_UTC = ZoneId.of("UTC");

    // ========== 현재 시간 조회 ==========

    /**
     * 현재 날짜/시간 (서울 시간대)
     */
    public static LocalDateTime now() {
        return LocalDateTime.now(ZONE_SEOUL);
    }

    /**
     * 현재 날짜 (서울 시간대)
     */
    public static LocalDate today() {
        return LocalDate.now(ZONE_SEOUL);
    }

    /**
     * 현재 시간 (서울 시간대)
     */
    public static LocalTime currentTime() {
        return LocalTime.now(ZONE_SEOUL);
    }

    /**
     * 현재 UTC 시간
     */
    public static LocalDateTime nowUtc() {
        return LocalDateTime.now(ZONE_UTC);
    }

    // ========== 포맷팅 ==========

    /**
     * LocalDateTime을 문자열로 변환 (기본 포맷)
     *
     * @param dateTime 날짜/시간
     * @return "yyyy-MM-dd HH:mm:ss" 형식의 문자열
     */
    public static String format(LocalDateTime dateTime) {
        return dateTime != null ? dateTime.format(DATETIME_FORMATTER) : null;
    }

    /**
     * LocalDateTime을 문자열로 변환 (커스텀 포맷)
     *
     * @param dateTime 날짜/시간
     * @param pattern  포맷 패턴
     * @return 포맷된 문자열
     */
    public static String format(LocalDateTime dateTime, String pattern) {
        return dateTime != null ? dateTime.format(DateTimeFormatter.ofPattern(pattern)) : null;
    }

    /**
     * LocalDate를 문자열로 변환 (기본 포맷)
     *
     * @param date 날짜
     * @return "yyyy-MM-dd" 형식의 문자열
     */
    public static String format(LocalDate date) {
        return date != null ? date.format(DATE_FORMATTER) : null;
    }

    /**
     * LocalDate를 문자열로 변환 (커스텀 포맷)
     *
     * @param date    날짜
     * @param pattern 포맷 패턴
     * @return 포맷된 문자열
     */
    public static String format(LocalDate date, String pattern) {
        return date != null ? date.format(DateTimeFormatter.ofPattern(pattern)) : null;
    }

    /**
     * 한국어 날짜 포맷 (예: "2024년 01월 15일")
     */
    public static String formatKorean(LocalDate date) {
        return date != null ? date.format(KOREAN_DATE_FORMATTER) : null;
    }

    /**
     * 한국어 날짜/시간 포맷 (예: "2024년 01월 15일 14시 30분")
     */
    public static String formatKorean(LocalDateTime dateTime) {
        return dateTime != null ? dateTime.format(KOREAN_DATETIME_FORMATTER) : null;
    }

    // ========== 파싱 ==========

    /**
     * 문자열을 LocalDateTime으로 변환 (기본 포맷)
     *
     * @param dateTimeStr "yyyy-MM-dd HH:mm:ss" 형식의 문자열
     * @return LocalDateTime
     */
    public static LocalDateTime parseDateTime(String dateTimeStr) {
        return StringUtil.isNotBlank(dateTimeStr)
            ? LocalDateTime.parse(dateTimeStr, DATETIME_FORMATTER)
            : null;
    }

    /**
     * 문자열을 LocalDateTime으로 변환 (커스텀 포맷)
     *
     * @param dateTimeStr 날짜/시간 문자열
     * @param pattern     포맷 패턴
     * @return LocalDateTime
     */
    public static LocalDateTime parseDateTime(String dateTimeStr, String pattern) {
        return StringUtil.isNotBlank(dateTimeStr)
            ? LocalDateTime.parse(dateTimeStr, DateTimeFormatter.ofPattern(pattern))
            : null;
    }

    /**
     * 문자열을 LocalDate로 변환 (기본 포맷)
     *
     * @param dateStr "yyyy-MM-dd" 형식의 문자열
     * @return LocalDate
     */
    public static LocalDate parseDate(String dateStr) {
        return StringUtil.isNotBlank(dateStr)
            ? LocalDate.parse(dateStr, DATE_FORMATTER)
            : null;
    }

    /**
     * 문자열을 LocalDate로 변환 (커스텀 포맷)
     *
     * @param dateStr 날짜 문자열
     * @param pattern 포맷 패턴
     * @return LocalDate
     */
    public static LocalDate parseDate(String dateStr, String pattern) {
        return StringUtil.isNotBlank(dateStr)
            ? LocalDate.parse(dateStr, DateTimeFormatter.ofPattern(pattern))
            : null;
    }

    // ========== 날짜 계산 ==========

    /**
     * 날짜에 일수 더하기
     */
    public static LocalDate plusDays(LocalDate date, long days) {
        return date != null ? date.plusDays(days) : null;
    }

    /**
     * 날짜에 월 더하기
     */
    public static LocalDate plusMonths(LocalDate date, long months) {
        return date != null ? date.plusMonths(months) : null;
    }

    /**
     * 날짜에 년 더하기
     */
    public static LocalDate plusYears(LocalDate date, long years) {
        return date != null ? date.plusYears(years) : null;
    }

    /**
     * 날짜/시간에 시간 더하기
     */
    public static LocalDateTime plusHours(LocalDateTime dateTime, long hours) {
        return dateTime != null ? dateTime.plusHours(hours) : null;
    }

    /**
     * 날짜/시간에 분 더하기
     */
    public static LocalDateTime plusMinutes(LocalDateTime dateTime, long minutes) {
        return dateTime != null ? dateTime.plusMinutes(minutes) : null;
    }

    /**
     * 두 날짜 사이의 일수 계산
     */
    public static long daysBetween(LocalDate startDate, LocalDate endDate) {
        if (startDate == null || endDate == null) {
            return 0;
        }
        return ChronoUnit.DAYS.between(startDate, endDate);
    }

    /**
     * 두 날짜/시간 사이의 시간 계산 (시간 단위)
     */
    public static long hoursBetween(LocalDateTime start, LocalDateTime end) {
        if (start == null || end == null) {
            return 0;
        }
        return ChronoUnit.HOURS.between(start, end);
    }

    /**
     * 두 날짜/시간 사이의 시간 계산 (분 단위)
     */
    public static long minutesBetween(LocalDateTime start, LocalDateTime end) {
        if (start == null || end == null) {
            return 0;
        }
        return ChronoUnit.MINUTES.between(start, end);
    }

    // ========== 날짜 비교 ==========

    /**
     * date1이 date2보다 이후인지 확인
     */
    public static boolean isAfter(LocalDate date1, LocalDate date2) {
        if (date1 == null || date2 == null) {
            return false;
        }
        return date1.isAfter(date2);
    }

    /**
     * date1이 date2보다 이전인지 확인
     */
    public static boolean isBefore(LocalDate date1, LocalDate date2) {
        if (date1 == null || date2 == null) {
            return false;
        }
        return date1.isBefore(date2);
    }

    /**
     * 날짜가 오늘인지 확인
     */
    public static boolean isToday(LocalDate date) {
        return date != null && date.isEqual(today());
    }

    /**
     * 날짜가 과거인지 확인
     */
    public static boolean isPast(LocalDate date) {
        return date != null && date.isBefore(today());
    }

    /**
     * 날짜가 미래인지 확인
     */
    public static boolean isFuture(LocalDate date) {
        return date != null && date.isAfter(today());
    }

    // ========== 시간대 변환 ==========

    /**
     * LocalDateTime을 ZonedDateTime으로 변환 (서울 시간대)
     */
    public static ZonedDateTime toZonedDateTime(LocalDateTime dateTime) {
        return dateTime != null ? dateTime.atZone(ZONE_SEOUL) : null;
    }

    /**
     * LocalDateTime을 UTC ZonedDateTime으로 변환
     */
    public static ZonedDateTime toUtcZonedDateTime(LocalDateTime dateTime) {
        return dateTime != null ? dateTime.atZone(ZONE_UTC) : null;
    }

    /**
     * LocalDateTime을 Epoch Milliseconds로 변환
     */
    public static long toEpochMilli(LocalDateTime dateTime) {
        if (dateTime == null) {
            return 0;
        }
        return dateTime.atZone(ZONE_SEOUL).toInstant().toEpochMilli();
    }

    /**
     * Epoch Milliseconds를 LocalDateTime으로 변환
     */
    public static LocalDateTime fromEpochMilli(long epochMilli) {
        return LocalDateTime.ofInstant(Instant.ofEpochMilli(epochMilli), ZONE_SEOUL);
    }

    // ========== 유틸리티 ==========

    /**
     * 날짜의 시작 시간 (00:00:00)
     */
    public static LocalDateTime startOfDay(LocalDate date) {
        return date != null ? date.atStartOfDay() : null;
    }

    /**
     * 날짜의 종료 시간 (23:59:59)
     */
    public static LocalDateTime endOfDay(LocalDate date) {
        return date != null ? date.atTime(23, 59, 59) : null;
    }

    /**
     * 월의 시작일
     */
    public static LocalDate startOfMonth(LocalDate date) {
        return date != null ? date.withDayOfMonth(1) : null;
    }

    /**
     * 월의 마지막일
     */
    public static LocalDate endOfMonth(LocalDate date) {
        return date != null ? date.withDayOfMonth(date.lengthOfMonth()) : null;
    }

    /**
     * 년의 시작일
     */
    public static LocalDate startOfYear(LocalDate date) {
        return date != null ? date.withDayOfYear(1) : null;
    }

    /**
     * 년의 마지막일
     */
    public static LocalDate endOfYear(LocalDate date) {
        return date != null ? date.withDayOfYear(date.lengthOfYear()) : null;
    }
}
