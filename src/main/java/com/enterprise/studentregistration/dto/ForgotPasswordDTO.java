package com.enterprise.studentregistration.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class ForgotPasswordDTO {

    @NotBlank(message = "Username is required")
    private String username;

    @NotBlank(message = "Registered email is required")
    @Email(message = "Please enter a valid email")
    private String email;
}
