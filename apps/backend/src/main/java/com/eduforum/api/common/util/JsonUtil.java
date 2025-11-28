package com.eduforum.api.common.util;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.util.List;
import java.util.Map;

/**
 * JSON 직렬화/역직렬화 유틸리티 클래스
 *
 * 기능:
 * - 객체를 JSON 문자열로 변환
 * - JSON 문자열을 객체로 변환
 * - Pretty Print
 * - Map/List 변환
 */
@Slf4j
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class JsonUtil {

    private static final ObjectMapper objectMapper;

    static {
        objectMapper = new ObjectMapper();
        // Java 8 날짜/시간 API 지원
        objectMapper.registerModule(new JavaTimeModule());
        // 날짜를 타임스탬프가 아닌 ISO-8601 형식으로 직렬화
        objectMapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
        // 알 수 없는 속성 무시 (역직렬화 시)
        objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        // null 값을 가진 필드 포함
        objectMapper.setSerializationInclusion(com.fasterxml.jackson.annotation.JsonInclude.Include.ALWAYS);
    }

    /**
     * ObjectMapper 인스턴스 반환
     */
    public static ObjectMapper getObjectMapper() {
        return objectMapper;
    }

    // ========== 직렬화 (Object -> JSON String) ==========

    /**
     * 객체를 JSON 문자열로 변환
     *
     * @param object 변환할 객체
     * @return JSON 문자열
     */
    public static String toJson(Object object) {
        if (object == null) {
            return null;
        }

        try {
            return objectMapper.writeValueAsString(object);
        } catch (JsonProcessingException e) {
            log.error("JSON 직렬화 실패: {}", e.getMessage(), e);
            throw new RuntimeException("JSON 직렬화 실패", e);
        }
    }

    /**
     * 객체를 Pretty Print JSON 문자열로 변환
     *
     * @param object 변환할 객체
     * @return Pretty Print JSON 문자열
     */
    public static String toPrettyJson(Object object) {
        if (object == null) {
            return null;
        }

        try {
            return objectMapper.writerWithDefaultPrettyPrinter()
                .writeValueAsString(object);
        } catch (JsonProcessingException e) {
            log.error("JSON Pretty Print 직렬화 실패: {}", e.getMessage(), e);
            throw new RuntimeException("JSON Pretty Print 직렬화 실패", e);
        }
    }

    /**
     * 객체를 JSON 바이트 배열로 변환
     *
     * @param object 변환할 객체
     * @return JSON 바이트 배열
     */
    public static byte[] toJsonBytes(Object object) {
        if (object == null) {
            return null;
        }

        try {
            return objectMapper.writeValueAsBytes(object);
        } catch (JsonProcessingException e) {
            log.error("JSON 바이트 변환 실패: {}", e.getMessage(), e);
            throw new RuntimeException("JSON 바이트 변환 실패", e);
        }
    }

    // ========== 역직렬화 (JSON String -> Object) ==========

    /**
     * JSON 문자열을 객체로 변환
     *
     * @param json  JSON 문자열
     * @param clazz 변환할 클래스 타입
     * @param <T>   반환 타입
     * @return 변환된 객체
     */
    public static <T> T fromJson(String json, Class<T> clazz) {
        if (StringUtil.isBlank(json)) {
            return null;
        }

        try {
            return objectMapper.readValue(json, clazz);
        } catch (JsonProcessingException e) {
            log.error("JSON 역직렬화 실패: {}", e.getMessage(), e);
            throw new RuntimeException("JSON 역직렬화 실패", e);
        }
    }

    /**
     * JSON 문자열을 객체로 변환 (TypeReference 사용)
     * 제네릭 타입 변환에 사용
     *
     * 예: List<User> users = fromJson(json, new TypeReference<List<User>>(){});
     *
     * @param json          JSON 문자열
     * @param typeReference 타입 레퍼런스
     * @param <T>           반환 타입
     * @return 변환된 객체
     */
    public static <T> T fromJson(String json, TypeReference<T> typeReference) {
        if (StringUtil.isBlank(json)) {
            return null;
        }

        try {
            return objectMapper.readValue(json, typeReference);
        } catch (JsonProcessingException e) {
            log.error("JSON 역직렬화 실패: {}", e.getMessage(), e);
            throw new RuntimeException("JSON 역직렬화 실패", e);
        }
    }

    /**
     * JSON 바이트 배열을 객체로 변환
     *
     * @param jsonBytes JSON 바이트 배열
     * @param clazz     변환할 클래스 타입
     * @param <T>       반환 타입
     * @return 변환된 객체
     */
    public static <T> T fromJsonBytes(byte[] jsonBytes, Class<T> clazz) {
        if (jsonBytes == null || jsonBytes.length == 0) {
            return null;
        }

        try {
            return objectMapper.readValue(jsonBytes, clazz);
        } catch (IOException e) {
            log.error("JSON 바이트 역직렬화 실패: {}", e.getMessage(), e);
            throw new RuntimeException("JSON 바이트 역직렬화 실패", e);
        }
    }

    // ========== Map/List 변환 ==========

    /**
     * 객체를 Map으로 변환
     *
     * @param object 변환할 객체
     * @return Map<String, Object>
     */
    @SuppressWarnings("unchecked")
    public static Map<String, Object> toMap(Object object) {
        if (object == null) {
            return null;
        }

        return objectMapper.convertValue(object, Map.class);
    }

    /**
     * JSON 문자열을 Map으로 변환
     *
     * @param json JSON 문자열
     * @return Map<String, Object>
     */
    public static Map<String, Object> toMap(String json) {
        return fromJson(json, new TypeReference<Map<String, Object>>() {});
    }

    /**
     * JSON 문자열을 List로 변환
     *
     * @param json  JSON 문자열
     * @param clazz 리스트 요소의 클래스 타입
     * @param <T>   리스트 요소 타입
     * @return List<T>
     */
    public static <T> List<T> toList(String json, Class<T> clazz) {
        if (StringUtil.isBlank(json)) {
            return null;
        }

        try {
            return objectMapper.readValue(json,
                objectMapper.getTypeFactory().constructCollectionType(List.class, clazz));
        } catch (JsonProcessingException e) {
            log.error("JSON List 역직렬화 실패: {}", e.getMessage(), e);
            throw new RuntimeException("JSON List 역직렬화 실패", e);
        }
    }

    /**
     * Map을 객체로 변환
     *
     * @param map   Map
     * @param clazz 변환할 클래스 타입
     * @param <T>   반환 타입
     * @return 변환된 객체
     */
    public static <T> T fromMap(Map<String, Object> map, Class<T> clazz) {
        if (map == null) {
            return null;
        }

        return objectMapper.convertValue(map, clazz);
    }

    // ========== 유틸리티 ==========

    /**
     * 유효한 JSON 문자열인지 확인
     *
     * @param json JSON 문자열
     * @return 유효한 JSON이면 true
     */
    public static boolean isValidJson(String json) {
        if (StringUtil.isBlank(json)) {
            return false;
        }

        try {
            objectMapper.readTree(json);
            return true;
        } catch (JsonProcessingException e) {
            return false;
        }
    }

    /**
     * 객체를 깊은 복사 (Deep Copy)
     *
     * @param object 복사할 객체
     * @param clazz  클래스 타입
     * @param <T>    반환 타입
     * @return 복사된 객체
     */
    public static <T> T deepCopy(T object, Class<T> clazz) {
        if (object == null) {
            return null;
        }

        try {
            String json = toJson(object);
            return fromJson(json, clazz);
        } catch (Exception e) {
            log.error("Deep Copy 실패: {}", e.getMessage(), e);
            throw new RuntimeException("Deep Copy 실패", e);
        }
    }

    /**
     * 두 객체를 JSON으로 변환하여 비교
     *
     * @param obj1 객체 1
     * @param obj2 객체 2
     * @return 같으면 true
     */
    public static boolean jsonEquals(Object obj1, Object obj2) {
        if (obj1 == obj2) {
            return true;
        }
        if (obj1 == null || obj2 == null) {
            return false;
        }

        String json1 = toJson(obj1);
        String json2 = toJson(obj2);

        return json1.equals(json2);
    }
}
