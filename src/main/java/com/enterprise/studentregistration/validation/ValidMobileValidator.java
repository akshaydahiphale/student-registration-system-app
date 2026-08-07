package com.enterprise.studentregistration.validation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

import java.util.regex.Pattern;

public class ValidMobileValidator implements ConstraintValidator<ValidMobile, String> {

    private static final Pattern MOBILE_PATTERN = Pattern.compile("^[6-9]\\d{9}$");

    @Override
    public boolean isValid(String mobileNumber, ConstraintValidatorContext context) {
        if (mobileNumber == null || mobileNumber.isBlank()) {
            return true; // let @NotBlank handle emptiness
        }
        return MOBILE_PATTERN.matcher(mobileNumber.trim()).matches();
    }
}
