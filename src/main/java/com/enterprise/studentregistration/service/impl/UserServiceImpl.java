package com.enterprise.studentregistration.service.impl;

import com.enterprise.studentregistration.dto.ChangePasswordDTO;
import com.enterprise.studentregistration.dto.CreateUserDTO;
import com.enterprise.studentregistration.dto.ForgotPasswordDTO;
import com.enterprise.studentregistration.dto.ResetPasswordDTO;
import com.enterprise.studentregistration.dto.SelfRegisterDTO;
import com.enterprise.studentregistration.entity.Role;
import com.enterprise.studentregistration.entity.Student;
import com.enterprise.studentregistration.entity.User;
import com.enterprise.studentregistration.exception.DuplicateEmailException;
import com.enterprise.studentregistration.exception.InvalidPasswordException;
import com.enterprise.studentregistration.exception.ResourceNotFoundException;
import com.enterprise.studentregistration.repository.StudentRepository;
import com.enterprise.studentregistration.repository.UserRepository;
import com.enterprise.studentregistration.service.EmailService;
import com.enterprise.studentregistration.service.StudentService;
import com.enterprise.studentregistration.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final StudentRepository studentRepository;
    private final StudentService studentService;
    private final PasswordEncoder passwordEncoder;
    private final EmailService emailService;

    @Override
    public User getByUsername(String username) {
        return userRepository.findByUsernameWithStudent(username)
                .orElseThrow(() -> new ResourceNotFoundException("User not found: " + username));
    }

    @Override
    @Transactional
    public String initiatePasswordReset(ForgotPasswordDTO dto) {
        User user = userRepository.findByUsernameAndEmailIgnoreCase(dto.getUsername(), dto.getEmail())
                .orElseThrow(() -> new ResourceNotFoundException(
                        "No account found matching that username and email"));
        String token = UUID.randomUUID().toString();
        user.setResetToken(token);
        user.setResetTokenExpiry(LocalDateTime.now().plusMinutes(30));
        userRepository.save(user);
        emailService.sendPasswordResetEmail(user.getEmail(), user.getUsername(), token);
        log.info("Password reset token generated for user '{}': {}", user.getUsername(), token);
        return token;
    }

    @Override
    @Transactional
    public void resetPassword(ResetPasswordDTO dto) {
        User user = userRepository.findByResetToken(dto.getToken())
                .orElseThrow(() -> new ResourceNotFoundException("Invalid or expired reset link"));
        if (user.getResetTokenExpiry() == null || user.getResetTokenExpiry().isBefore(LocalDateTime.now())) {
            throw new ResourceNotFoundException("This reset link has expired. Please request a new one.");
        }
        if (!dto.getNewPassword().equals(dto.getConfirmPassword())) {
            throw new InvalidPasswordException("New password and confirmation do not match");
        }
        user.setPassword(passwordEncoder.encode(dto.getNewPassword()));
        user.setResetToken(null);
        user.setResetTokenExpiry(null);
        userRepository.save(user);
        log.info("Password reset completed for user '{}'", user.getUsername());
    }

    @Override
    @Transactional
    public void changePassword(String username, ChangePasswordDTO dto) {
        User user = getByUsername(username);
        if (!passwordEncoder.matches(dto.getCurrentPassword(), user.getPassword())) {
            throw new InvalidPasswordException("Current password is incorrect");
        }
        if (!dto.getNewPassword().equals(dto.getConfirmPassword())) {
            throw new InvalidPasswordException("New password and confirmation do not match");
        }
        user.setPassword(passwordEncoder.encode(dto.getNewPassword()));
        userRepository.save(user);
        log.info("Password changed for user '{}'", username);
    }

    @Override
    @Transactional
    public User createUser(CreateUserDTO dto) {
        if (userRepository.existsByUsername(dto.getUsername())) {
            throw new DuplicateEmailException("Username '" + dto.getUsername() + "' is already taken");
        }
        if (!dto.getPassword().equals(dto.getConfirmPassword())) {
            throw new InvalidPasswordException("Password and confirmation do not match");
        }

        User.UserBuilder userBuilder = User.builder()
                .username(dto.getUsername())
                .password(passwordEncoder.encode(dto.getPassword()))
                .email(dto.getEmail())
                .role(dto.getRole())
                .enabled(true);

        if (dto.getRole() == Role.STUDENT) {
            if (dto.getStudentId() == null) {
                throw new ResourceNotFoundException("Please select a student to link this account to");
            }
            if (userRepository.existsByStudentId(dto.getStudentId())) {
                throw new DuplicateEmailException("This student already has a login account");
            }
            Student student = studentRepository.findById(dto.getStudentId())
                    .orElseThrow(() -> new ResourceNotFoundException("Student not found"));
            userBuilder.student(student);
        }

        User user = userBuilder.build();
        userRepository.save(user);
        log.info("New '{}' account created: {}", user.getRole(), user.getUsername());
        return user;
    }

    @Override
    @Transactional
    public User registerStudent(SelfRegisterDTO dto) {
        if (userRepository.existsByUsername(dto.getUsername())) {
            throw new DuplicateEmailException("Username '" + dto.getUsername() + "' is already taken");
        }
        if (!dto.getPassword().equals(dto.getConfirmPassword())) {
            throw new InvalidPasswordException("Password and confirmation do not match");
        }

        Student student = studentService.createStudent(dto);

        User user = User.builder()
                .username(dto.getUsername())
                .password(passwordEncoder.encode(dto.getPassword()))
                .email(dto.getEmail())
                .role(Role.STUDENT)
                .enabled(false)
                .student(student)
                .build();

        userRepository.save(user);
        emailService.sendNewRegistrationNotificationToAdmin(user.getUsername(), user.getEmail());
        log.info("New self-registered STUDENT account pending approval: {}", user.getUsername());
        return user;
    }

    @Override
    public List<User> getPendingUsers() {
        return userRepository.findByEnabledFalseWithStudent();
    }

    @Override
    @Transactional
    public void approveUser(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
        user.setEnabled(true);
        userRepository.save(user);
        emailService.sendApprovalEmail(user.getEmail(), user.getUsername());
        log.info("User '{}' approved by admin", user.getUsername());
    }
}