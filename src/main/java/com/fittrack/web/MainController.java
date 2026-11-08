package com.fittrack.web;

import com.fittrack.service.DashboardService;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class MainController {

    private final DashboardService dashboardService;

    public MainController(DashboardService dashboardService) {
        this.dashboardService = dashboardService;
    }

    @GetMapping("/")
    public String home(@AuthenticationPrincipal UserDetails principal) {
        return principal == null ? "index" : "redirect:/dashboard";
    }

    @GetMapping("/login")
    public String login(@AuthenticationPrincipal UserDetails principal) {
        return principal == null ? "login" : "redirect:/dashboard";
    }

    @GetMapping("/dashboard")
    public String dashboard(Model model) {
        model.addAttribute("dashboard", dashboardService.build());
        return "dashboard";
    }
}
