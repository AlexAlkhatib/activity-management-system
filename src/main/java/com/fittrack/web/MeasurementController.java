package com.fittrack.web;

import com.fittrack.domain.Measurement;
import com.fittrack.domain.User;
import com.fittrack.service.FitnessCalculator;
import com.fittrack.service.MeasurementService;
import com.fittrack.service.UserService;
import com.fittrack.web.dto.MeasurementForm;
import jakarta.validation.Valid;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Controller
@RequestMapping("/measurements")
public class MeasurementController {

    private final MeasurementService measurementService;
    private final UserService userService;

    public MeasurementController(MeasurementService measurementService, UserService userService) {
        this.measurementService = measurementService;
        this.userService = userService;
    }

    @GetMapping
    public String list(Model model) {
        User user = userService.getCurrentUser();
        List<Measurement> measurements = measurementService.listForCurrentUser();
        Measurement latest = measurements.isEmpty() ? null : measurements.get(0);

        model.addAttribute("measurements", measurements);
        model.addAttribute("latest", latest);
        if (!model.containsAttribute("measurementForm")) {
            MeasurementForm form = new MeasurementForm();
            if (latest != null) {
                form.setWeightKg(latest.getWeightKg());
            }
            model.addAttribute("measurementForm", form);
        }
        model.addAttribute("bmi", latest != null && user.getHeightCm() != null
                ? FitnessCalculator.bmi(user.getHeightCm(), latest.getWeightKg()) : 0);
        model.addAttribute("bmiCategory", FitnessCalculator.bmiCategory(latest != null && user.getHeightCm() != null
                ? FitnessCalculator.bmi(user.getHeightCm(), latest.getWeightKg()) : 0));
        model.addAttribute("targetWeight", user.getTargetWeightKg());
        model.addAttribute("weightSeries", buildSeries(measurements, user));
        return "measurements";
    }

    private String buildSeries(List<Measurement> measurements, User user) {
        if (measurements.isEmpty()) {
            return "";
        }
        List<Measurement> ordered = new ArrayList<>(measurements);
        Collections.reverse(ordered);
        if (ordered.size() > 12) {
            ordered = ordered.subList(ordered.size() - 12, ordered.size());
        }
        double min = ordered.stream().mapToDouble(Measurement::getWeightKg).min().orElse(0);
        double max = ordered.stream().mapToDouble(Measurement::getWeightKg).max().orElse(1);
        if (max - min < 1) {
            max += 0.5;
            min -= 0.5;
        }
        StringBuilder points = new StringBuilder();
        for (int i = 0; i < ordered.size(); i++) {
            double x = 8 + (300 - 16) * i / Math.max(1.0, ordered.size() - 1.0);
            double y = 12 + (100 - 24) * (1 - (ordered.get(i).getWeightKg() - min) / (max - min));
            points.append(String.format(java.util.Locale.US, "%.1f,%.1f ", x, y));
        }
        return points.toString().trim();
    }

    @PostMapping
    public String add(@Valid @ModelAttribute("measurementForm") MeasurementForm form, BindingResult binding,
                      Model model, RedirectAttributes redirectAttributes) {
        if (binding.hasErrors()) {
            return list(model);
        }
        measurementService.add(form);
        redirectAttributes.addFlashAttribute("message", "Mesure enregistrée.");
        return "redirect:/measurements";
    }

    @PostMapping("/{id}/delete")
    public String delete(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        measurementService.delete(id);
        redirectAttributes.addFlashAttribute("message", "Mesure supprimée.");
        return "redirect:/measurements";
    }
}
