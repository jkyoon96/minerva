package com.eduforum.api.common.validation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

/**
 * ValidEnum 애노테이션 검증 로직
 */
public class ValidEnumValidator implements ConstraintValidator<ValidEnum, String> {

    private Class<? extends Enum<?>> enumClass;
    private boolean ignoreCase;

    @Override
    public void initialize(ValidEnum annotation) {
        this.enumClass = annotation.enumClass();
        this.ignoreCase = annotation.ignoreCase();
    }

    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        // null은 @NotNull로 검증
        if (value == null) {
            return true;
        }

        Enum<?>[] enumConstants = enumClass.getEnumConstants();
        if (enumConstants == null) {
            return false;
        }

        for (Enum<?> enumConstant : enumConstants) {
            String enumValue = enumConstant.name();
            if (ignoreCase) {
                if (enumValue.equalsIgnoreCase(value)) {
                    return true;
                }
            } else {
                if (enumValue.equals(value)) {
                    return true;
                }
            }
        }

        return false;
    }
}
