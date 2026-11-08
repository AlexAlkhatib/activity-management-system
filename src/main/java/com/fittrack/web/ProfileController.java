package com.fittrack.web;

import com.fittrack.domain.ActivityLevel;
import com.fittrack.domain.Gender;
import com.fittrack.domain.User;
import com.fittrack.service.FitnessCalculator;
import com.fittrack.service.MeasurementService;
import com.fittrack.service.UserService;
import com.fittrack.web.dto.ProfileForm;
import jakarta.validation.Valid;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/profile")
public class ProfileController {

    private final UserService userService;
    private final MeasurementService measurementService;

    public ProfileController(UserService userService, MeasurementService measurementService) {
        this.userService = userService;
        this.measurementService = measurementService;
    }

    @ModelAttribute("genders")
    public Gender[] genders() {
        return Gender.values();
    }

    @ModelAttribute("levels")
    public ActivityLevel[] levels() {
        return ActivityLevel.values();
    }

    @GetMapping
    public String profile(Model model) {
        User user = userService.getCurrentUser();
        if (!model.containsAttribute("profileForm")) {
            ProfileForm form = new ProfileForm();
            form.setFirstName(user.getFirstName());
            form.setLastName(user.getLastName());
            form.setBirthDate(user.getBirthDate());
            form.setGender(user.getGender());
            form.setHeightCm(user.getHeightCm());
            form.setTargetWeightKg(user.getTargetWeightKg());
            form.setWeeklyGoal(user.getWeeklyGoal());
            form.setActivityLevel(user.getActivityLevel());
            model.addAttribute("profileForm", form);
        }
        double weight = measurementService.currentWeightOrDefault(0);
        double bmi = weight > 0 && user.getHeightCm() != null
                ? FitnessCalculator.bmi(user.getHeightCm(), weight) : 0;
        int bmr = weight > 0 && user.getHeightCm() != null && user.getAge() > 0
                ? FitnessCalculator.basalMetabolism(weight, user.getHeightCm(), user.getAge(), user.getGender()) : 0;
        model.addAttribute("currentWeight", weight);
        model.addAttribute("bmi", bmi);
        model.addAttribute("bmiCategory", FitnessCalculator.bmiCategory(bmi));
        model.addAttribute("bmr", bmr);
        model.addAttribute("tdee", FitnessCalculator.totalDailyEnergy(bmr, user.getActivityLevel()));
        return "profile";
    }

    @PostMapping
    public String update(@Valid @ModelAttribute("profileForm") ProfileForm form, BindingResult binding,
                         RedirectAttributes redirectAttributes) {
        if (binding.hasErrors()) {
            return "profile";
        }
        userService.updateProfile(form);
        redirectAttributes.addFlashAttribute("message", "Profil mis à jour.");
        return "redirect:/profile";
    }
}
