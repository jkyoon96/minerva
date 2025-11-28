package com.eduforum.api.common.validation;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.*;

/**
 * 전화번호 유효성 검증 애노테이션 (한국 휴대폰 번호)
 *
 * 검증 규칙:
 * - 010, 011, 016, 017, 018, 019로 시작
 * - 하이픈(-) 있거나 없거나 모두 허용
 * - 예: 010-1234-5678, 01012345678
 *
 * 사용 예시:
 * @ValidPhone
 * private String phone;
 */
@Target({ElementType.FIELD, ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = ValidPhoneValidator.class)
@Documented
public @interface ValidPhone {

    /**
     * 에러 메시지
     */
    String message() default "유효하지 않은 전화번호 형식입니다.";

    /**
     * 유효성 검증 그룹
     */
    Class<?>[] groups() default {};

    /**
     * 페이로드
     */
    Class<? extends Payload>[] payload() default {};
}
