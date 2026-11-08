package com.fittrack.web;

import com.fittrack.domain.Intensity;
import com.fittrack.domain.MuscleGroup;
import com.fittrack.domain.Workout;
import com.fittrack.domain.WorkoutType;
import com.fittrack.service.ExerciseService;
import com.fittrack.service.FitnessCalculator;
import com.fittrack.service.MeasurementService;
import com.fittrack.service.WorkoutService;
import com.fittrack.web.dto.WorkoutExerciseForm;
import com.fittrack.web.dto.WorkoutForm;
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

import java.time.LocalDate;
import java.util.List;

@Controller
@RequestMapping("/workouts")
public class WorkoutController {

    private final WorkoutService workoutService;
    private final ExerciseService exerciseService;
    private final MeasurementService measurementService;

    public WorkoutController(WorkoutService workoutService, ExerciseService exerciseService,
                             MeasurementService measurementService) {
        this.workoutService = workoutService;
        this.exerciseService = exerciseService;
        this.measurementService = measurementService;
    }

    @ModelAttribute("types")
    public WorkoutType[] types() {
        return WorkoutType.values();
    }

    @ModelAttribute("intensities")
    public Intensity[] intensities() {
        return Intensity.values();
    }

    @GetMapping
    public String list(@RequestParam(required = false) WorkoutType type,
                       @RequestParam(defaultValue = "30") int period,
                       Model model) {
        int safePeriod = period <= 0 ? 30 : period;
        List<Workout> workouts = workoutService.listForCurrentUser(type, safePeriod);
        model.addAttribute("workouts", workouts);
        model.addAttribute("selectedType", type);
        model.addAttribute("period", safePeriod);
        model.addAttribute("totalMinutes", workouts.stream().mapToInt(Workout::getDurationMinutes).sum());
        model.addAttribute("totalCalories", workouts.stream().mapToInt(Workout::getCalories).sum());
        return "workouts";
    }

    @GetMapping("/new")
    public String createForm(Model model) {
        model.addAttribute("workoutForm", new WorkoutForm());
        model.addAttribute("editing", false);
        model.addAttribute("formAction", "/workouts");
        return "workout_form";
    }

    @PostMapping
    public String create(@Valid @ModelAttribute("workoutForm") WorkoutForm form, BindingResult binding,
                         Model model, RedirectAttributes redirectAttributes) {
        if (binding.hasErrors()) {
            model.addAttribute("editing", false);
            model.addAttribute("formAction", "/workouts");
            return "workout_form";
        }
        Workout workout = workoutService.create(form);
        redirectAttributes.addFlashAttribute("message", "Séance enregistrée. Ajoutez vos exercices !");
        return "redirect:/workouts/" + workout.getId();
    }

    @GetMapping("/{id}")
    public String detail(@PathVariable Long id, Model model) {
        Workout workout = workoutService.getOwned(id);
        model.addAttribute("workout", workout);
        model.addAttribute("library", exerciseService.listVisible());
        if (!model.containsAttribute("exerciseForm")) {
            model.addAttribute("exerciseForm", new WorkoutExerciseForm());
        }
        return "workout_detail";
    }

    @PostMapping("/{id}/exercises")
    public String addExercise(@PathVariable Long id,
                              @Valid @ModelAttribute("exerciseForm") WorkoutExerciseForm form,
                              BindingResult binding, Model model, RedirectAttributes redirectAttributes) {
        if (binding.hasErrors()) {
            Workout workout = workoutService.getOwned(id);
            model.addAttribute("workout", workout);
            model.addAttribute("library", exerciseService.listVisible());
            return "workout_detail";
        }
        workoutService.addExercise(id, form);
        redirectAttributes.addFlashAttribute("message", "Exercice ajouté à la séance.");
        return "redirect:/workouts/" + id;
    }

    @PostMapping("/{id}/exercises/{entryId}/delete")
    public String removeExercise(@PathVariable Long id, @PathVariable Long entryId,
                                 RedirectAttributes redirectAttributes) {
        workoutService.removeExercise(id, entryId);
        redirectAttributes.addFlashAttribute("message", "Exercice retiré.");
        return "redirect:/workouts/" + id;
    }

    @GetMapping("/{id}/edit")
    public String editForm(@PathVariable Long id, Model model) {
        Workout workout = workoutService.getOwned(id);
        WorkoutForm form = new WorkoutForm();
        form.setDate(workout.getDate());
        form.setType(workout.getType());
        form.setDurationMinutes(workout.getDurationMinutes());
        form.setIntensity(workout.getIntensity());
        form.setNotes(workout.getNotes());
        model.addAttribute("workoutForm", form);
        model.addAttribute("workout", workout);
        model.addAttribute("editing", true);
        model.addAttribute("formAction", "/workouts/" + id);
        return "workout_form";
    }

    @PostMapping("/{id}")
    public String update(@PathVariable Long id, @Valid @ModelAttribute("workoutForm") WorkoutForm form,
                         BindingResult binding, Model model, RedirectAttributes redirectAttributes) {
        if (binding.hasErrors()) {
            model.addAttribute("workout", workoutService.getOwned(id));
            model.addAttribute("editing", true);
            model.addAttribute("formAction", "/workouts/" + id);
            return "workout_form";
        }
        workoutService.update(id, form);
        redirectAttributes.addFlashAttribute("message", "Séance mise à jour.");
        return "redirect:/workouts/" + id;
    }

    @PostMapping("/{id}/delete")
    public String delete(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        workoutService.delete(id);
        redirectAttributes.addFlashAttribute("message", "Séance supprimée.");
        return "redirect:/workouts";
    }
}
