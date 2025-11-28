package com.eduforum.api.common.validation;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.*;

/**
 * Enum 유효성 검증 애노테이션
 *
 * 사용 예시:
 * @ValidEnum(enumClass = UserRole.class, message = "유효하지 않은 역할입니다.")
 * private String role;
 */
@Target({ElementType.FIELD, ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = ValidEnumValidator.class)
@Documented
public @interface ValidEnum {

    /**
     * Enum 클래스
     */
    Class<? extends Enum<?>> enumClass();

    /**
     * 에러 메시지
     */
    String message() default "유효하지 않은 값입니다.";

    /**
     * 대소문자 무시 여부
     */
    boolean ignoreCase() default false;

    /**
     * 유효성 검증 그룹
     */
    Class<?>[] groups() default {};

    /**
     * 페이로드
     */
    Class<? extends Payload>[] payload() default {};
}
