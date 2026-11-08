package com.fittrack.service;

import com.fittrack.domain.Exercise;
import com.fittrack.repository.ExerciseRepository;
import com.fittrack.web.dto.ExerciseForm;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.Comparator;
import java.util.List;
import java.util.Locale;

/**
 * Bibliotheque d'exercices : exercices integres + exercices personnels.
 */
@Service
public class ExerciseService {

    private final ExerciseRepository exerciseRepository;
    private final UserService userService;

    public ExerciseService(ExerciseRepository exerciseRepository, UserService userService) {
        this.exerciseRepository = exerciseRepository;
        this.userService = userService;
    }

    @Transactional(readOnly = true)
    public List<Exercise> listVisible() {
        Long userId = userService.getCurrentUser().getId();
        return exerciseRepository.findByCreatedByIsNullOrCreatedByOrderByNameAsc(userId);
    }

    @Transactional(readOnly = true)
    public List<Exercise> search(String query, String muscleGroup) {
        String needle = query == null ? "" : query.trim().toLowerCase(Locale.FRENCH);
        return listVisible().stream()
                .filter(e -> muscleGroup == null || muscleGroup.isBlank()
                        || e.getMuscleGroup().name().equals(muscleGroup))
                .filter(e -> needle.isEmpty()
                        || e.getName().toLowerCase(Locale.FRENCH).contains(needle)
                        || (e.getDescription() != null
                        && e.getDescription().toLowerCase(Locale.FRENCH).contains(needle)))
                .sorted(Comparator.comparing(Exercise::getName))
                .toList();
    }

    @Transactional
    public Exercise create(ExerciseForm form) {
        Exercise exercise = new Exercise(form.getName().trim(), form.getMuscleGroup(),
                form.getDescription(), form.getMet());
        exercise.setCreatedBy(userService.getCurrentUser().getId());
        return exerciseRepository.save(exercise);
    }

    @Transactional
    public void deleteCustom(Long id) {
        Exercise exercise = exerciseRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Exercice introuvable."));
        if (!userService.getCurrentUser().getId().equals(exercise.getCreatedBy())) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN,
                    "Les exercices officiels de la bibliotheque ne peuvent pas etre supprimes.");
        }
        exerciseRepository.delete(exercise);
    }

    @Transactional(readOnly = true)
    public Exercise findForCurrentUser(Long id) {
        Exercise exercise = exerciseRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Exercice introuvable."));
        Long userId = userService.getCurrentUser().getId();
        if (exercise.getCreatedBy() != null && !exercise.getCreatedBy().equals(userId)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Exercice non accessible.");
        }
        return exercise;
    }
}
