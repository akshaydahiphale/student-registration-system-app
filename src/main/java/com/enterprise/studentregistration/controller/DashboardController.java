package com.enterprise.studentregistration.controller;

import com.enterprise.studentregistration.service.DashboardService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

/**
 * Renders the admin dashboard with aggregate statistics:
 * total/active/inactive students, gender breakdown, and the most
 * recently registered students.
 */
@Controller
@RequiredArgsConstructor
public class DashboardController {

    private final DashboardService dashboardService;

    @GetMapping("/dashboard")
    public String dashboard(Model model) {
        model.addAttribute("stats", dashboardService.getStats());
        model.addAttribute("activePage", "dashboard");
        return "dashboard";
    }
}
