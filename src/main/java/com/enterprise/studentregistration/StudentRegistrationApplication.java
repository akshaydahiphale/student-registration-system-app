package com.enterprise.studentregistration;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Entry point of the Student Registration System.
 *
 * This is a Spring Boot enterprise application demonstrating:
 * - Layered architecture (Controller -> Service -> Repository -> Entity)
 * - Spring MVC + Thymeleaf server-side rendering
 * - Spring Data JPA with MySQL
 * - Spring Security based authentication (Admin / Student roles)
 * - Bean Validation, custom validators, global exception handling
 */
@SpringBootApplication
public class StudentRegistrationApplication {

    public static void main(String[] args) {
        SpringApplication.run(StudentRegistrationApplication.class, args);
    }
}
