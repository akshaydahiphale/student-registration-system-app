package com.enterprise.studentregistration.controller;

import com.enterprise.studentregistration.dto.StudentDTO;
import com.enterprise.studentregistration.entity.Gender;
import com.enterprise.studentregistration.entity.Student;
import com.enterprise.studentregistration.entity.StudentStatus;
import com.enterprise.studentregistration.service.StudentService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

/**
 * Full CRUD + Search/Filter/Sort/Pagination controller for students.
 * All routes here are restricted to ROLE_ADMIN by SecurityConfig.
 */
@Controller
@RequestMapping("/students")
@RequiredArgsConstructor
public class StudentController {

    private final StudentService studentService;

    @GetMapping
    public String listStudents(@RequestParam(required = false) String keyword,
                                @RequestParam(required = false) String course,
                                @RequestParam(required = false) String branch,
                                @RequestParam(required = false) Integer semester,
                                @RequestParam(defaultValue = "0") int page,
                                @RequestParam(defaultValue = "10") int size,
                                @RequestParam(defaultValue = "id") String sortBy,
                                @RequestParam(defaultValue = "asc") String direction,
                                Model model) {

        Sort sort = direction.equalsIgnoreCase("desc") ? Sort.by(sortBy).descending() : Sort.by(sortBy).ascending();
        Pageable pageable = PageRequest.of(page, size, sort);

        boolean hasFilters = StringUtils.hasText(keyword) || StringUtils.hasText(course)
                || StringUtils.hasText(branch) || semester != null;

        Page<Student> studentPage = hasFilters
                ? studentService.searchStudents(keyword, course, branch, semester, pageable)
                : studentService.getAllStudents(pageable);

        model.addAttribute("studentPage", studentPage);
        model.addAttribute("keyword", keyword);
        model.addAttribute("course", course);
        model.addAttribute("branch", branch);
        model.addAttribute("semester", semester);
        model.addAttribute("sortBy", sortBy);
        model.addAttribute("direction", direction);
        model.addAttribute("reverseDirection", direction.equalsIgnoreCase("asc") ? "desc" : "asc");
        model.addAttribute("activePage", "students");
        return "students/list";
    }

    @GetMapping("/new")
    public String newStudentForm(Model model) {
        model.addAttribute("studentDTO", new StudentDTO());
        model.addAttribute("genders", Gender.values());
        model.addAttribute("statuses", StudentStatus.values());
        model.addAttribute("isEdit", false);
        model.addAttribute("activePage", "students");
        return "students/form";
    }

    @PostMapping
    public String createStudent(@Valid @ModelAttribute StudentDTO studentDTO,
                                 BindingResult bindingResult,
                                 Model model,
                                 RedirectAttributes redirectAttributes) {
        if (bindingResult.hasErrors()) {
            model.addAttribute("genders", Gender.values());
            model.addAttribute("statuses", StudentStatus.values());
            model.addAttribute("isEdit", false);
            model.addAttribute("activePage", "students");
            return "students/form";
        }
        Student saved = studentService.createStudent(studentDTO);
        redirectAttributes.addFlashAttribute("successMessage",
                "Student '" + saved.getFullName() + "' (" + saved.getStudentId() + ") registered successfully");
        return "redirect:/students";
    }

    @GetMapping("/{id}")
    public String viewStudent(@PathVariable Long id, Model model) {
        model.addAttribute("student", studentService.getStudentById(id));
        model.addAttribute("activePage", "students");
        return "students/view";
    }

    @GetMapping("/{id}/edit")
    public String editStudentForm(@PathVariable Long id, Model model) {
        Student student = studentService.getStudentById(id);
        StudentDTO dto = StudentDTO.builder()
                .id(student.getId())
                .studentId(student.getStudentId())
                .firstName(student.getFirstName())
                .lastName(student.getLastName())
                .gender(student.getGender())
                .dateOfBirth(student.getDateOfBirth())
                .mobileNumber(student.getMobileNumber())
                .email(student.getEmail())
                .address(student.getAddress())
                .city(student.getCity())
                .state(student.getState())
                .pinCode(student.getPinCode())
                .course(student.getCourse())
                .branch(student.getBranch())
                .semester(student.getSemester())
                .admissionDate(student.getAdmissionDate())
                .photoPath(student.getPhotoPath())
                .status(student.getStatus())
                .build();

        model.addAttribute("studentDTO", dto);
        model.addAttribute("genders", Gender.values());
        model.addAttribute("statuses", StudentStatus.values());
        model.addAttribute("isEdit", true);
        model.addAttribute("activePage", "students");
        return "students/form";
    }

    @PostMapping("/{id}")
    public String updateStudent(@PathVariable Long id,
                                 @Valid @ModelAttribute StudentDTO studentDTO,
                                 BindingResult bindingResult,
                                 Model model,
                                 RedirectAttributes redirectAttributes) {
        studentDTO.setId(id); // ensure @UniqueEmail excludes this same record
        if (bindingResult.hasErrors()) {
            model.addAttribute("genders", Gender.values());
            model.addAttribute("statuses", StudentStatus.values());
            model.addAttribute("isEdit", true);
            model.addAttribute("activePage", "students");
            return "students/form";
        }
        Student updated = studentService.updateStudent(id, studentDTO);
        redirectAttributes.addFlashAttribute("successMessage",
                "Student '" + updated.getFullName() + "' updated successfully");
        return "redirect:/students/" + id;
    }

    @PostMapping("/{id}/delete")
    public String deleteStudent(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        studentService.deleteStudent(id);
        redirectAttributes.addFlashAttribute("successMessage", "Student deleted successfully");
        return "redirect:/students";
    }
}
