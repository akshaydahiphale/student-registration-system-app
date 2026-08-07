package com.enterprise.studentregistration.controller;

import com.enterprise.studentregistration.dto.SelfRegisterDTO;
import com.enterprise.studentregistration.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequiredArgsConstructor
public class RegistrationController {

    private final UserService userService;

    @GetMapping("/register")
    public String showRegistrationForm(Model model) {
        model.addAttribute("selfRegisterDTO", new SelfRegisterDTO());
        return "register";
    }

    @PostMapping("/register")
    public String register(@Valid @ModelAttribute("selfRegisterDTO") SelfRegisterDTO dto,
                            BindingResult result, Model model,
                            RedirectAttributes redirectAttributes) {
        if (result.hasErrors()) {
            return "register";
        }
        userService.registerStudent(dto);
        redirectAttributes.addFlashAttribute("successMessage",
                "Registration successful! Please wait for admin approval before logging in.");
        return "redirect:/login";
    }
}