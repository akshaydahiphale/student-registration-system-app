package com.enterprise.studentregistration.validation;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.*;

/**
 * Validates that a mobile number is exactly 10 digits (Indian format,
 * numeric only, cannot start with 0). Adjust the regex in
 * ValidMobileValidator for other country formats.
 */
@Documented
@Constraint(validatedBy = ValidMobileValidator.class)
@Target({ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
public @interface ValidMobile {
    String message() default "Mobile number must be a valid 10-digit number";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}
