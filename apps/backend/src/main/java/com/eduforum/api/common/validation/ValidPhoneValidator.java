package com.eduforum.api.common.validation;

import com.eduforum.api.common.constant.ApiConstants;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

import java.util.regex.Pattern;

/**
 * ValidPhone 애노테이션 검증 로직
 */
public class ValidPhoneValidator implements ConstraintValidator<ValidPhone, String> {

    private static final Pattern PHONE_PATTERN = Pattern.compile(ApiConstants.PHONE_REGEX);

    @Override
    public void initialize(ValidPhone annotation) {
        // 초기화 로직 (필요시)
    }

    @Override
    public boolean isValid(String phone, ConstraintValidatorContext context) {
        // null은 @NotNull로 검증
        if (phone == null || phone.isEmpty()) {
            return true;
        }

        return PHONE_PATTERN.matcher(phone).matches();
    }
}
