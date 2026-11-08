package com.fittrack.web;

import com.fittrack.service.UserService;
import com.fittrack.web.dto.RegistrationForm;
import jakarta.validation.Valid;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
public class AuthController {

    private final UserService userService;

    public AuthController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/register")
    public String showForm(Model model) {
        model.addAttribute("form", new RegistrationForm());
        return "register";
    }

    @PostMapping("/register")
    public String register(@Valid @ModelAttribute("form") RegistrationForm form, BindingResult binding,
                           RedirectAttributes redirectAttributes) {
        if (!form.getPassword().equals(form.getConfirmPassword())) {
            binding.rejectValue("confirmPassword", "password.mismatch",
                    "Les mots de passe ne correspondent pas.");
        }
        if (!binding.hasErrors()) {
            try {
                userService.register(form);
                redirectAttributes.addFlashAttribute("registered", form.getFirstName());
                return "redirect:/login?registered";
            } catch (IllegalStateException ex) {
                binding.rejectValue("email", "email.exists", ex.getMessage());
            }
        }
        return "register";
    }
}
