package com.fittrack.web;

import com.fittrack.domain.MuscleGroup;
import com.fittrack.service.ExerciseService;
import com.fittrack.web.dto.ExerciseForm;
import jakarta.validation.Valid;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/exercises")
public class ExerciseController {

    private final ExerciseService exerciseService;

    public ExerciseController(ExerciseService exerciseService) {
        this.exerciseService = exerciseService;
    }

    @ModelAttribute("muscleGroups")
    public MuscleGroup[] muscleGroups() {
        return MuscleGroup.values();
    }

    @GetMapping
    public String list(@RequestParam(required = false) String q,
                       @RequestParam(required = false) String group,
                       Model model) {
        model.addAttribute("exercises", exerciseService.search(q, group));
        model.addAttribute("query", q);
        model.addAttribute("selectedGroup", group);
        if (!model.containsAttribute("exerciseForm")) {
            model.addAttribute("exerciseForm", new ExerciseForm());
        }
        return "exercises";
    }

    @PostMapping
    public String create(@Valid @ModelAttribute("exerciseForm") ExerciseForm form, BindingResult binding,
                         Model model, RedirectAttributes redirectAttributes) {
        if (binding.hasErrors()) {
            model.addAttribute("exercises", exerciseService.listVisible());
            return "exercises";
        }
        exerciseService.create(form);
        redirectAttributes.addFlashAttribute("message", "Exercice ajouté à votre bibliothèque.");
        return "redirect:/exercises";
    }

    @PostMapping("/{id}/delete")
    public String delete(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        exerciseService.deleteCustom(id);
        redirectAttributes.addFlashAttribute("message", "Exercice supprimé.");
        return "redirect:/exercises";
    }
}
