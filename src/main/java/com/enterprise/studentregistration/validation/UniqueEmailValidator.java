package com.enterprise.studentregistration.validation;

import com.enterprise.studentregistration.dto.StudentDTO;
import com.enterprise.studentregistration.repository.StudentRepository;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

/**
 * Validates that the email on a StudentDTO is not already used by a
 * DIFFERENT student. When editing (dto.getId() != null) the current
 * record is excluded from the duplicate check.
 */
@Component
@RequiredArgsConstructor
public class UniqueEmailValidator implements ConstraintValidator<UniqueEmail, StudentDTO> {

    private final StudentRepository studentRepository;

    @Override
    public boolean isValid(StudentDTO dto, ConstraintValidatorContext context) {
        if (dto == null || dto.getEmail() == null || dto.getEmail().isBlank()) {
            return true; // let @NotBlank handle emptiness
        }

        return studentRepository.findByEmailIgnoreCase(dto.getEmail())
                .map(existing -> existing.getId().equals(dto.getId()))
                .orElse(true);
    }
}
