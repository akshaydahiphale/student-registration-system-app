package com.enterprise.studentregistration.controller;

import com.enterprise.studentregistration.dto.CreateUserDTO;
import com.enterprise.studentregistration.entity.Role;
import com.enterprise.studentregistration.repository.StudentRepository;
import com.enterprise.studentregistration.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;
    private final StudentRepository studentRepository;

    @GetMapping("/users/new")
    public String newUserForm(Model model) {
        model.addAttribute("createUserDTO", new CreateUserDTO());
        model.addAttribute("roles", Role.values());
        model.addAttribute("students", studentRepository.findAll());
        model.addAttribute("activePage", "users");
        return "users/form";
    }

    @PostMapping("/users")
    public String createUser(@Valid @ModelAttribute("createUserDTO") CreateUserDTO dto,
                              BindingResult result, Model model,
                              RedirectAttributes redirectAttributes) {
        if (result.hasErrors()) {
            model.addAttribute("roles", Role.values());
            model.addAttribute("students", studentRepository.findAll());
            model.addAttribute("activePage", "users");
            return "users/form";
        }
        userService.createUser(dto);
        redirectAttributes.addFlashAttribute("successMessage", "User account created successfully");
        return "redirect:/dashboard";
    }

    @GetMapping("/users/pending")
    public String pendingUsers(Model model) {
        model.addAttribute("pendingUsers", userService.getPendingUsers());
        model.addAttribute("activePage", "pending");
        return "users/pending";
    }

    @PostMapping("/users/{id}/approve")
    public String approveUser(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        userService.approveUser(id);
        redirectAttributes.addFlashAttribute("successMessage", "User approved — they can now log in.");
        return "redirect:/users/pending";
    }
}