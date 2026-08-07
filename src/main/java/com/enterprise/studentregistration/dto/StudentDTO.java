package com.enterprise.studentregistration.dto;

import com.enterprise.studentregistration.entity.Gender;
import com.enterprise.studentregistration.entity.StudentStatus;
import com.enterprise.studentregistration.validation.UniqueEmail;
import com.enterprise.studentregistration.validation.ValidMobile;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDate;

/**
 * Data Transfer Object used to move data between the Controller and
 * Service layers for create/update operations. Keeping this separate
 * from the Student entity prevents leaking JPA internals to the web
 * layer and lets us apply web-specific validation rules.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@UniqueEmail
public class StudentDTO {

    /** Null when creating a new student; populated when editing. */
    private Long id;

    /** Read-only, auto-generated (e.g. STU2026001); ignored on input. */
    private String studentId;

    @NotBlank(message = "First name is required")
    @Size(max = 50, message = "First name must not exceed 50 characters")
    private String firstName;

    @NotBlank(message = "Last name is required")
    @Size(max = 50, message = "Last name must not exceed 50 characters")
    private String lastName;

    @NotNull(message = "Gender is required")
    private Gender gender;

    @NotNull(message = "Date of birth is required")
    @Past(message = "Date of birth must be in the past")
    private LocalDate dateOfBirth;

    @NotBlank(message = "Mobile number is required")
    @ValidMobile
    private String mobileNumber;

    @NotBlank(message = "Email is required")
    @Email(message = "Email must be a valid email address")
    private String email;

    @NotBlank(message = "Address is required")
    @Size(max = 255)
    private String address;

    @NotBlank(message = "City is required")
    private String city;

    @NotBlank(message = "State is required")
    private String state;

    @NotBlank(message = "Pin code is required")
    @Pattern(regexp = "^\\d{6}$", message = "Pin code must be a 6-digit number")
    private String pinCode;

    @NotBlank(message = "Course is required")
    private String course;

    @NotBlank(message = "Branch is required")
    private String branch;

    @NotNull(message = "Semester is required")
    @Min(value = 1, message = "Semester must be between 1 and 8")
    @Max(value = 8, message = "Semester must be between 1 and 8")
    private Integer semester;

    @NotNull(message = "Admission date is required")
    private LocalDate admissionDate;

    /** Populated by controller when a new photo is uploaded (not persisted directly). */
    private MultipartFile photo;

    /** Existing photo path - used to display current photo on the edit form. */
    private String photoPath;

    private StudentStatus status;
}
