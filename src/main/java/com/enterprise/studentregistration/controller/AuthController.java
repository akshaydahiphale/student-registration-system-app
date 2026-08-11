package com.enterprise.studentregistration.controller;

import com.enterprise.studentregistration.dto.ChangePasswordDTO;
import com.enterprise.studentregistration.dto.ForgotPasswordDTO;
import com.enterprise.studentregistration.dto.ResetPasswordDTO;
import com.enterprise.studentregistration.exception.InvalidPasswordException;
import com.enterprise.studentregistration.exception.ResourceNotFoundException;
import com.enterprise.studentregistration.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * Handles the whole Login Module: sign-in page, forgot-password
 * (identify -> token -> reset), and change-password for logged-in users.
 */
@Controller
@RequiredArgsConstructor
public class AuthController {

    private final UserService userService;

    @GetMapping("/login")
    public String loginPage() {
        return "login";
    }

    @GetMapping("/forgot-password")
    public String forgotPasswordPage(Model model) {
        model.addAttribute("forgotPasswordDTO", new ForgotPasswordDTO());
        return "forgot-password";
    }

    @PostMapping("/forgot-password")
    public String processForgotPassword(@Valid @ModelAttribute ForgotPasswordDTO forgotPasswordDTO,
                                         BindingResult bindingResult,
                                         Model model) {
        if (bindingResult.hasErrors()) {
            return "forgot-password";
        }
        try {
            userService.initiatePasswordReset(forgotPasswordDTO);
            return "forgot-password-confirmation";
        } catch (ResourceNotFoundException e) {
            model.addAttribute("errorMessage", e.getMessage());
            return "forgot-password";
        }
    }

    @GetMapping("/reset-password")
    public String resetPasswordPage(@RequestParam("token") String token, Model model) {
        ResetPasswordDTO dto = new ResetPasswordDTO();
        dto.setToken(token);
        model.addAttribute("resetPasswordDTO", dto);
        return "reset-password";
    }

    @PostMapping("/reset-password")
    public String processResetPassword(@Valid @ModelAttribute ResetPasswordDTO resetPasswordDTO,
                                        BindingResult bindingResult,
                                        Model model) {
        if (bindingResult.hasErrors()) {
            return "reset-password";
        }
        try {
            userService.resetPassword(resetPasswordDTO);
            model.addAttribute("successMessage", "Password reset successful. You can now log in.");
            return "login";
        } catch (RuntimeException e) {
            model.addAttribute("errorMessage", e.getMessage());
            return "reset-password";
        }
    }

    @GetMapping("/change-password")
    public String changePasswordPage(Model model) {
        model.addAttribute("changePasswordDTO", new ChangePasswordDTO());
        return "change-password";
    }

    @PostMapping("/change-password")
    public String processChangePassword(@Valid @ModelAttribute ChangePasswordDTO changePasswordDTO,
                                         BindingResult bindingResult,
                                         Authentication authentication,
                                         Model model) {
        if (bindingResult.hasErrors()) {
            return "change-password";
        }
        try {
            userService.changePassword(authentication.getName(), changePasswordDTO);
            model.addAttribute("successMessage", "Password changed successfully.");
        } catch (InvalidPasswordException e) {
            model.addAttribute("errorMessage", e.getMessage());
        }
        return "change-password";
    }
}
