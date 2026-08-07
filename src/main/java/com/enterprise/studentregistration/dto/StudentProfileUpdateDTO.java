package com.enterprise.studentregistration.dto;

import com.enterprise.studentregistration.validation.ValidMobile;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Fields a logged-in STUDENT is allowed to self-edit on their profile.
 * Deliberately excludes studentId, firstName, lastName, gender, dateOfBirth,
 * course, branch, semester, admissionDate, status - those remain admin-only
 * via the existing StudentDTO + updateStudent() flow.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class StudentProfileUpdateDTO {

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
}