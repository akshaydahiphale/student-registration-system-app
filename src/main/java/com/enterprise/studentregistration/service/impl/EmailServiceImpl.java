package com.enterprise.studentregistration.service.impl;

import com.enterprise.studentregistration.service.EmailService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class EmailServiceImpl implements EmailService {

    private final JavaMailSender mailSender;

    @Value("${app.mail.from}")
    private String fromAddress;

    @Value("${app.mail.admin}")
    private String adminEmail;

    @Override
    public void sendPasswordResetEmail(String toEmail, String username, String resetToken) {
        String resetLink = "http://localhost:8080/reset-password?token=" + resetToken;

        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom(fromAddress);
        message.setTo(toEmail);
        message.setSubject("Password Reset - Student Registration System");
        message.setText("Hello " + username + ",\n\n"
                + "We received a request to reset your password.\n"
                + "Click the link below to set a new password (valid for 30 minutes):\n\n"
                + resetLink + "\n\n"
                + "If you did not request this, please ignore this email.\n\n"
                + "Regards,\nStudent Registration System");

        try {
            mailSender.send(message);
            log.info("Password reset email sent to {}", toEmail);
        } catch (Exception e) {
            log.error("Failed to send password reset email to {}: {}", toEmail, e.getMessage());
        }
    }

    @Override
    public void sendApprovalEmail(String toEmail, String username) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom(fromAddress);
        message.setTo(toEmail);
        message.setSubject("Account Approved - Student Registration System");
        message.setText("Hello " + username + ",\n\n"
                + "Good news! Your registration has been approved by the admin.\n"
                + "You can now log in using your username and password at:\n\n"
                + "http://localhost:8080/login\n\n"
                + "Regards,\nStudent Registration System");

        try {
            mailSender.send(message);
            log.info("Approval email sent to {}", toEmail);
        } catch (Exception e) {
            log.error("Failed to send approval email to {}: {}", toEmail, e.getMessage());
        }
    }

    @Override
    public void sendNewRegistrationNotificationToAdmin(String studentUsername, String studentEmail) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom(fromAddress);
        message.setTo(adminEmail);
        message.setSubject("New Student Registration Pending Approval");
        message.setText("A new student has registered and is awaiting approval:\n\n"
                + "Username: " + studentUsername + "\n"
                + "Email: " + studentEmail + "\n\n"
                + "Please review and approve at:\n"
                + "http://localhost:8080/users/pending\n\n"
                + "Regards,\nStudent Registration System");

        try {
            mailSender.send(message);
            log.info("New registration notification sent to admin for '{}'", studentUsername);
        } catch (Exception e) {
            log.error("Failed to send admin notification email: {}", e.getMessage());
        }
    }
}