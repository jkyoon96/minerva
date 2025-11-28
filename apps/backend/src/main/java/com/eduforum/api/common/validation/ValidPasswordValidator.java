package com.eduforum.api.common.validation;

import com.eduforum.api.common.constant.ApiConstants;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

import java.util.regex.Pattern;

/**
 * ValidPassword 애노테이션 검증 로직
 */
public class ValidPasswordValidator implements ConstraintValidator<ValidPassword, String> {

    private static final Pattern PASSWORD_PATTERN = Pattern.compile(ApiConstants.PASSWORD_REGEX);

    @Override
    public void initialize(ValidPassword annotation) {
        // 초기화 로직 (필요시)
    }

    @Override
    public boolean isValid(String password, ConstraintValidatorContext context) {
        // null은 @NotNull로 검증
        if (password == null) {
            return true;
        }

        return PASSWORD_PATTERN.matcher(password).matches();
    }
}
