package com.enterprise.studentregistration.service;

public interface EmailService {
    void sendPasswordResetEmail(String toEmail, String username, String resetToken);
    void sendApprovalEmail(String toEmail, String username);
    void sendNewRegistrationNotificationToAdmin(String studentUsername, String studentEmail);
}