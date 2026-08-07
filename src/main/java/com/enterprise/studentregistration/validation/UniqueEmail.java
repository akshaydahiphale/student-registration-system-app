package com.enterprise.studentregistration.validation;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.*;

/**
 * Class-level constraint applied on StudentDTO. It is class-level (rather
 * than field-level) so the validator can see BOTH the email and the DTO's
 * own id at the same time - required to correctly skip the "duplicate"
 * check against the record being edited.
 */
@Documented
@Constraint(validatedBy = UniqueEmailValidator.class)
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
public @interface UniqueEmail {
    String message() default "Email is already registered with another student";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}
