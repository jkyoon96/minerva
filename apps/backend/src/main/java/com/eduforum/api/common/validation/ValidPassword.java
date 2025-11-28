package com.eduforum.api.common.validation;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.*;

/**
 * 비밀번호 유효성 검증 애노테이션
 *
 * 검증 규칙:
 * - 8~20자
 * - 영문 대문자 포함
 * - 영문 소문자 포함
 * - 숫자 포함
 * - 특수문자 포함
 *
 * 사용 예시:
 * @ValidPassword
 * private String password;
 */
@Target({ElementType.FIELD, ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = ValidPasswordValidator.class)
@Documented
public @interface ValidPassword {

    /**
     * 에러 메시지
     */
    String message() default "비밀번호는 8~20자의 영문 대소문자, 숫자, 특수문자를 조합해야 합니다.";

    /**
     * 유효성 검증 그룹
     */
    Class<?>[] groups() default {};

    /**
     * 페이로드
     */
    Class<? extends Payload>[] payload() default {};
}
