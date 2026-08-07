package com.enterprise.studentregistration.controller;

import com.enterprise.studentregistration.dto.StudentProfileUpdateDTO;
import com.enterprise.studentregistration.entity.Student;
import com.enterprise.studentregistration.entity.User;
import com.enterprise.studentregistration.exception.DuplicateEmailException;
import com.enterprise.studentregistration.exception.ResourceNotFoundException;
import com.enterprise.studentregistration.service.StudentService;
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
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

/**
 * Allows a STUDENT-role account to view (read-only) and self-edit
 * limited contact fields of their own linked student profile.
 */
@Controller
@RequiredArgsConstructor
public class ProfileController {

    private final UserService userService;
    private final StudentService studentService;

    @GetMapping("/profile")
    public String myProfile(Authentication authentication, Model model) {
        User user = userService.getByUsername(authentication.getName());
        if (user.getStudent() == null) {
            throw new ResourceNotFoundException("No student profile is linked to this account");
        }
        model.addAttribute("student", user.getStudent());
        return "students/view";
    }

    @GetMapping("/profile/edit")
    public String editProfileForm(Authentication authentication, Model model) {
        User user = userService.getByUsername(authentication.getName());
        Student student = user.getStudent();
        if (student == null) {
            throw new ResourceNotFoundException("No student profile is linked to this account");
        }

        StudentProfileUpdateDTO dto = StudentProfileUpdateDTO.builder()
                .mobileNumber(student.getMobileNumber())
                .email(student.getEmail())
                .address(student.getAddress())
                .city(student.getCity())
                .state(student.getState())
                .pinCode(student.getPinCode())
                .build();

        model.addAttribute("profileUpdateDTO", dto);
        return "profile-edit";
    }

    @PostMapping("/profile/edit")
    public String updateProfile(@Valid @ModelAttribute("profileUpdateDTO") StudentProfileUpdateDTO dto,
                                 BindingResult result,
                                 Authentication authentication,
                                 Model model,
                                 RedirectAttributes redirectAttributes) {
        if (result.hasErrors()) {
            return "profile-edit";
        }

        User user = userService.getByUsername(authentication.getName());
        Student student = user.getStudent();
        if (student == null) {
            throw new ResourceNotFoundException("No student profile is linked to this account");
        }

        try {
            studentService.updateOwnProfile(student.getId(), dto);
        } catch (DuplicateEmailException ex) {
            result.rejectValue("email", "duplicate", ex.getMessage());
            return "profile-edit";
        }

        redirectAttributes.addFlashAttribute("successMessage", "Profile updated successfully");
        return "redirect:/profile";
    }
}