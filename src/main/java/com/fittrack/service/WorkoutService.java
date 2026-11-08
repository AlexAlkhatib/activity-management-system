package com.fittrack.service;

import com.fittrack.domain.Exercise;
import com.fittrack.domain.User;
import com.fittrack.domain.Workout;
import com.fittrack.domain.WorkoutExercise;
import com.fittrack.domain.WorkoutType;
import com.fittrack.repository.WorkoutRepository;
import com.fittrack.web.dto.WorkoutExerciseForm;
import com.fittrack.web.dto.WorkoutForm;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDate;
import java.util.List;

/**
 * Gestion des seances d'entrainement et de leurs exercices.
 */
@Service
public class WorkoutService {

    private final WorkoutRepository workoutRepository;
    private final ExerciseService exerciseService;
    private final MeasurementService measurementService;
    private final UserService userService;

    public WorkoutService(WorkoutRepository workoutRepository, ExerciseService exerciseService,
                          MeasurementService measurementService, UserService userService) {
        this.workoutRepository = workoutRepository;
        this.exerciseService = exerciseService;
        this.measurementService = measurementService;
        this.userService = userService;
    }

    @Transactional(readOnly = true)
    public List<Workout> listForCurrentUser(WorkoutType type, int periodDays) {
        User user = userService.getCurrentUser();
        LocalDate from = LocalDate.now().minusDays(Math.max(1, periodDays));
        List<Workout> workouts = periodDays >= 3650
                ? workoutRepository.findByUserOrderByDateDescIdDesc(user)
                : workoutRepository.findByUserAndDateGreaterThanEqualOrderByDateDescIdDesc(user, from);
        return workouts.stream()
                .filter(w -> type == null || w.getType() == type)
                .toList();
    }

    @Transactional(readOnly = true)
    public Workout getOwned(Long id) {
        return workoutRepository.findByIdAndUser(id, userService.getCurrentUser())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Seance introuvable."));
    }

    @Transactional
    public Workout create(WorkoutForm form) {
        User user = userService.getCurrentUser();
        Workout workout = new Workout();
        workout.setUser(user);
        applyForm(workout, form);
        double weight = measurementService.currentWeightOrDefault(75.0);
        workout.setCalories(FitnessCalculator.estimateCalories(form.getType(), form.getIntensity(),
                form.getDurationMinutes(), weight));
        return workoutRepository.save(workout);
    }

    @Transactional
    public Workout update(Long id, WorkoutForm form) {
        Workout workout = getOwned(id);
        applyForm(workout, form);
        double weight = measurementService.currentWeightOrDefault(75.0);
        workout.setCalories(FitnessCalculator.estimateCalories(form.getType(), form.getIntensity(),
                form.getDurationMinutes(), weight));
        return workoutRepository.save(workout);
    }

    private void applyForm(Workout workout, WorkoutForm form) {
        workout.setDate(form.getDate());
        workout.setType(form.getType());
        workout.setDurationMinutes(form.getDurationMinutes());
        workout.setIntensity(form.getIntensity());
        workout.setNotes(form.getNotes());
    }

    @Transactional
    public void delete(Long id) {
        workoutRepository.delete(getOwned(id));
    }

    @Transactional
    public WorkoutExercise addExercise(Long workoutId, WorkoutExerciseForm form) {
        Workout workout = getOwned(workoutId);
        Exercise exercise = exerciseService.findForCurrentUser(form.getExerciseId());
        WorkoutExercise entry = new WorkoutExercise();
        entry.setExercise(exercise);
        entry.setSets(form.getSets());
        entry.setReps(form.getReps());
        entry.setWeightKg(form.getWeightKg());
        entry.setDurationSeconds(form.getDurationSeconds());
        entry.setDistanceKm(form.getDistanceKm());
        entry.setComment(form.getComment());
        workout.addExercise(entry);
        workoutRepository.save(workout);
        return entry;
    }

    @Transactional
    public void removeExercise(Long workoutId, Long entryId) {
        Workout workout = getOwned(workoutId);
        WorkoutExercise entry = workout.getExercises().stream()
                .filter(e -> e.getId().equals(entryId))
                .findFirst()
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Exercice de seance introuvable."));
        workout.removeExercise(entry);
        workoutRepository.save(workout);
    }

    @Transactional(readOnly = true)
    public long totalWorkouts() {
        return workoutRepository.countByUser(userService.getCurrentUser());
    }
}
