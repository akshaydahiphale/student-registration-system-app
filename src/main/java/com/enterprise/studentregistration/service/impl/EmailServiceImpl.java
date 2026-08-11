package com.enterprise.studentregistration.service.impl;

import com.enterprise.studentregistration.service.EmailService;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.HashMap;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Slf4j
public class EmailServiceImpl implements EmailService {

    @Value("${brevo.api.key}")
    private String brevoApiKey;

    @Value("${app.mail.from}")
    private String fromAddress;

    @Value("${app.mail.admin}")
    private String adminEmail;

    @Value("${app.base-url}")
    private String baseUrl;

    private final ObjectMapper objectMapper = new ObjectMapper();
    private final HttpClient httpClient = HttpClient.newHttpClient();

    private void sendViaBrevo(String toEmail, String subject, String textContent) {
        try {
            Map<String, Object> sender = new HashMap<>();
            sender.put("email", fromAddress);
            sender.put("name", "Student Registration System");

            Map<String, Object> recipient = new HashMap<>();
            recipient.put("email", toEmail);

            Map<String, Object> body = new HashMap<>();
            body.put("sender", sender);
            body.put("to", new Object[]{recipient});
            body.put("subject", subject);
            body.put("textContent", textContent);

            String jsonBody = objectMapper.writeValueAsString(body);

            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create("https://api.brevo.com/v3/smtp/email"))
                    .header("accept", "application/json")
                    .header("api-key", brevoApiKey)
                    .header("content-type", "application/json")
                    .POST(HttpRequest.BodyPublishers.ofString(jsonBody))
                    .build();

            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

            if (response.statusCode() >= 200 && response.statusCode() < 300) {
                log.info("Email sent to {} via Brevo, status {}", toEmail, response.statusCode());
            } else {
                log.error("Brevo API error sending to {}: status {}, body {}", toEmail, response.statusCode(), response.body());
            }
        } catch (Exception e) {
            log.error("Failed to send email to {}: {}", toEmail, e.getMessage());
        }
    }

    @Override
    public void sendPasswordResetEmail(String toEmail, String username, String resetToken) {
        String resetLink = baseUrl + "/reset-password?token=" + resetToken;
        String text = "Hello " + username + ",\n\n"
                + "We received a request to reset your password.\n"
                + "Click the link below to set a new password (valid for 30 minutes):\n\n"
                + resetLink + "\n\n"
                + "If you did not request this, please ignore this email.\n\n"
                + "Regards,\nStudent Registration System";
        sendViaBrevo(toEmail, "Password Reset - Student Registration System", text);
    }

    @Override
    public void sendApprovalEmail(String toEmail, String username) {
        String text = "Hello " + username + ",\n\n"
                + "Good news! Your registration has been approved by the admin.\n"
                + "You can now log in using your username and password at:\n\n"
                + baseUrl + "/login\n\n"
                + "Regards,\nStudent Registration System";
        sendViaBrevo(toEmail, "Account Approved - Student Registration System", text);
    }

    @Override
    public void sendNewRegistrationNotificationToAdmin(String studentUsername, String studentEmail) {
        String text = "A new student has registered and is awaiting approval:\n\n"
                + "Username: " + studentUsername + "\n"
                + "Email: " + studentEmail + "\n\n"
                + "Please review and approve at:\n"
                + baseUrl + "/users/pending\n\n"
                + "Regards,\nStudent Registration System";
        sendViaBrevo(adminEmail, "New Student Registration Pending Approval", text);
    }
}