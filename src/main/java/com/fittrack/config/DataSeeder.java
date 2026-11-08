package com.fittrack.config;

import com.fittrack.domain.ActivityLevel;
import com.fittrack.domain.Exercise;
import com.fittrack.domain.Gender;
import com.fittrack.domain.Intensity;
import com.fittrack.domain.Measurement;
import com.fittrack.domain.MuscleGroup;
import com.fittrack.domain.Role;
import com.fittrack.domain.User;
import com.fittrack.domain.Workout;
import com.fittrack.domain.WorkoutExercise;
import com.fittrack.domain.WorkoutType;
import com.fittrack.repository.ExerciseRepository;
import com.fittrack.repository.MeasurementRepository;
import com.fittrack.repository.RoleRepository;
import com.fittrack.repository.UserRepository;
import com.fittrack.repository.WorkoutRepository;
import com.fittrack.service.FitnessCalculator;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Cree la bibliotheque d'exercices et un compte de demonstration avec
 * dix semaines d'historique, uniquement si la base est vide.
 */
@Component
@ConditionalOnProperty(name = "app.seed.enabled", havingValue = "true", matchIfMissing = true)
public class DataSeeder implements CommandLineRunner {

    private static final String DEMO_EMAIL = "demo@fittrack.app";
    private static final String DEMO_PASSWORD = "Demo1234!";

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final ExerciseRepository exerciseRepository;
    private final WorkoutRepository workoutRepository;
    private final MeasurementRepository measurementRepository;
    private final PasswordEncoder passwordEncoder;

    public DataSeeder(UserRepository userRepository, RoleRepository roleRepository,
                      ExerciseRepository exerciseRepository, WorkoutRepository workoutRepository,
                      MeasurementRepository measurementRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.exerciseRepository = exerciseRepository;
        this.workoutRepository = workoutRepository;
        this.measurementRepository = measurementRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    @Transactional
    public void run(String... args) {
        seedLibrary();
        if (userRepository.existsByEmailIgnoreCase(DEMO_EMAIL)) {
            return;
        }
        seedDemoUser();
    }

    private void seedLibrary() {
        if (exerciseRepository.count() > 0) {
            return;
        }
        List<Exercise> exercises = List.of(
                new Exercise("Développé couché", MuscleGroup.PECTORAUX, "Barre ou haltères, poussée horizontale.", 5.0),
                new Exercise("Développé incliné", MuscleGroup.PECTORAUX, "Haut des pectoraux, banc à 30-45°.", 5.0),
                new Exercise("Écarté haltères", MuscleGroup.PECTORAUX, "Ouverture contrôlée, étirement du pectoral.", 4.0),
                new Exercise("Pompes", MuscleGroup.PECTORAUX, "Poids du corps, gainage actif.", 5.0),
                new Exercise("Traction", MuscleGroup.DOS, "Tirage vertical, prise pronation.", 6.0),
                new Exercise("Rowing barre", MuscleGroup.DOS, "Tirage horizontal, dos gainé.", 5.5),
                new Exercise("Tirage vertical", MuscleGroup.DOS, "Poulie haute, grand dorsal.", 5.0),
                new Exercise("Soulevé de terre", MuscleGroup.DOS, "Mouvement complet, chaîne postérieure.", 6.5),
                new Exercise("Développé militaire", MuscleGroup.EPAULES, "Poussée verticale, gainage lombaire.", 5.5),
                new Exercise("Élévations latérales", MuscleGroup.EPAULES, "Deltoïdes moyens, charge légère.", 3.5),
                new Exercise("Curl biceps", MuscleGroup.BICEPS, "Flexion du coude, sans élan.", 3.5),
                new Exercise("Dips", MuscleGroup.TRICEPS, "Poids du corps, buste légèrement penché.", 6.0),
                new Exercise("Extension triceps", MuscleGroup.TRICEPS, "Poulie ou haltère au-dessus de la tête.", 3.5),
                new Exercise("Squat", MuscleGroup.JAMBES, "Roi des jambes, descente contrôlée.", 6.0),
                new Exercise("Presse à cuisses", MuscleGroup.JAMBES, "Charge guidée, amplitude sûre.", 5.0),
                new Exercise("Fente avant", MuscleGroup.JAMBES, "Unilatéral, équilibre et quadriceps.", 5.0),
                new Exercise("Leg curl", MuscleGroup.JAMBES, "Ischio-jambiers en flexion.", 4.5),
                new Exercise("Hip thrust", MuscleGroup.FESSIERS, "Extension de hanche, gainage.", 5.0),
                new Exercise("Mollets debout", MuscleGroup.MOLLETS, "Extension complète des chevilles.", 3.0),
                new Exercise("Planche", MuscleGroup.ABDOMINAUX, "Gainage statique, alignement strict.", 3.5),
                new Exercise("Crunch", MuscleGroup.ABDOMINAUX, "Flexion courte du buste.", 3.0),
                new Exercise("Relevé de jambes", MuscleGroup.ABDOMINAUX, "Gainage suspendu ou au sol.", 4.0),
                new Exercise("Course à pied", MuscleGroup.CARDIO, "Endurance fondamentale ou fractionné.", 9.0),
                new Exercise("Vélo", MuscleGroup.CARDIO, "Cardio articulaire, faible impact.", 7.0),
                new Exercise("Rameur", MuscleGroup.CARDIO, "Cardio complet, haut et bas du corps.", 8.5),
                new Exercise("Corde à sauter", MuscleGroup.CARDIO, "Cardio intense, coordination.", 10.0),
                new Exercise("Natation", MuscleGroup.CARDIO, "Cardio sans impact sur les articulations.", 8.0),
                new Exercise("Burpees", MuscleGroup.CORPS_ENTIER, "Enchaînement cardio et force.", 9.5),
                new Exercise("Mountain climbers", MuscleGroup.CORPS_ENTIER, "Cardio au sol, gainage dynamique.", 8.0),
                new Exercise("Kettlebell swing", MuscleGroup.CORPS_ENTIER, "Hip hinge explosif.", 8.0),
                new Exercise("Étirement ischio-jambiers", MuscleGroup.JAMBES, "Fin de séance, respiration lente.", 2.5),
                new Exercise("Salutation au soleil", MuscleGroup.CORPS_ENTIER, "Enchaînement de mobilité.", 3.0)
        );
        exerciseRepository.saveAll(exercises);
    }

    private void seedDemoUser() {
        Role userRole = roleRepository.findByName("ROLE_USER")
                .orElseGet(() -> roleRepository.save(new Role("ROLE_USER")));
        Role adminRole = roleRepository.findByName("ROLE_ADMIN")
                .orElseGet(() -> roleRepository.save(new Role("ROLE_ADMIN")));

        User user = new User("Alex", "Martin", DEMO_EMAIL, passwordEncoder.encode(DEMO_PASSWORD));
        user.setBirthDate(LocalDate.now().minusYears(31));
        user.setGender(Gender.HOMME);
        user.setHeightCm(178.0);
        user.setTargetWeightKg(76.0);
        user.setWeeklyGoal(4);
        user.setActivityLevel(ActivityLevel.MODERE);
        user.getRoles().add(userRole);
        user.getRoles().add(adminRole);
        userRepository.save(user);

        LocalDate today = LocalDate.now();
        for (int week = 9; week >= 0; week--) {
            LocalDate date = today.minusWeeks(week);
            double weight = weightForWeek(week);
            Measurement measurement = new Measurement(user, date, FitnessCalculator.round1(weight));
            measurement.setBodyFatPct(FitnessCalculator.round1(22.4 - (9 - week) * 0.29));
            measurement.setWaistCm(FitnessCalculator.round1(92.0 - (9 - week) * 0.55));
            measurement.setChestCm(104.0);
            if (week == 0) {
                measurement.setNotes("Bonne énergie, sommeil régulier.");
            }
            measurementRepository.save(measurement);
        }

        Map<String, Exercise> library = new HashMap<>();
        for (Exercise exercise : exerciseRepository.findAll()) {
            library.put(exercise.getName(), exercise);
        }

        WorkoutType[] rotation = {WorkoutType.MUSCULATION, WorkoutType.CARDIO,
                WorkoutType.MUSCULATION, WorkoutType.HIIT};
        int[] dayOffsets = {0, 2, 4, 6};
        String[] strengthNames = {"Développé couché", "Rowing barre", "Squat", "Développé militaire"};
        String[] hypertrophyNames = {"Traction", "Élévations latérales", "Presse à cuisses", "Curl biceps"};

        for (int week = 9; week >= 0; week--) {
            LocalDate monday = FitnessCalculator.mondayOf(today).minusWeeks(week);
            for (int session = 0; session < rotation.length; session++) {
                LocalDate date = monday.plusDays(dayOffsets[session]);
                if (date.isAfter(today)) {
                    continue;
                }
                WorkoutType type = rotation[session];
                Intensity intensity = session == 3 ? Intensity.ELEVEE
                        : (week % 2 == 0 ? Intensity.MODEREE : Intensity.ELEVEE);
                int duration = 45 + ((week + session) % 3) * 15;

                Workout workout = new Workout();
                workout.setUser(user);
                workout.setDate(date);
                workout.setType(type);
                workout.setDurationMinutes(duration);
                workout.setIntensity(intensity);
                double bodyWeight = weightForWeek(week);
                workout.setCalories(FitnessCalculator.estimateCalories(type, intensity, duration, bodyWeight));
                workout.setNotes(notesFor(type));

                switch (type) {
                    case MUSCULATION -> {
                        String[] names = week % 2 == 0 ? strengthNames : hypertrophyNames;
                        for (int i = 0; i < names.length; i++) {
                            Exercise exercise = library.get(names[i]);
                            if (exercise == null) {
                                continue;
                            }
                            WorkoutExercise entry = new WorkoutExercise();
                            entry.setExercise(exercise);
                            entry.setSets(4);
                            entry.setReps(8 + (week % 3) * 2);
                            entry.setWeightKg(FitnessCalculator.round1(40 + i * 10 + (9 - week) * 0.9));
                            entry.setComment(i == 0 ? "Dernière série proche de l'échec." : null);
                            workout.addExercise(entry);
                        }
                    }
                    case CARDIO -> {
                        Exercise run = library.get("Course à pied");
                        if (run != null) {
                            WorkoutExercise entry = new WorkoutExercise();
                            entry.setExercise(run);
                            entry.setDurationSeconds(duration * 60);
                            entry.setDistanceKm(FitnessCalculator.round1(duration / 6.2));
                            entry.setComment("Allure régulière, terrain vallonné.");
                            workout.addExercise(entry);
                        }
                        Exercise bike = library.get("Vélo");
                        if (bike != null) {
                            WorkoutExercise entry = new WorkoutExercise();
                            entry.setExercise(bike);
                            entry.setDurationSeconds(600);
                            entry.setDistanceKm(4.0);
                            workout.addExercise(entry);
                        }
                    }
                    case HIIT -> {
                        addEntry(workout, library, "Burpees", 4, 20, null);
                        addEntry(workout, library, "Mountain climbers", 4, 30, null);
                        addEntry(workout, library, "Corde à sauter", null, null, 600);
                    }
                    case MOBILITE -> addEntry(workout, library, "Salutation au soleil", 3, 10, null);
                    default -> {
                    }
                }
                workoutRepository.save(workout);
            }
        }
    }

    private void addEntry(Workout workout, Map<String, Exercise> library, String name,
                          Integer sets, Integer reps, Integer durationSeconds) {
        Exercise exercise = library.get(name);
        if (exercise == null) {
            return;
        }
        WorkoutExercise entry = new WorkoutExercise();
        entry.setExercise(exercise);
        entry.setSets(sets);
        entry.setReps(reps);
        entry.setDurationSeconds(durationSeconds);
        workout.addExercise(entry);
    }

    private double weightForWeek(int week) {
        double base = 84.2 - (9 - week) * 0.45;
        return base + (week % 3 == 1 ? 0.2 : 0);
    }

    private String notesFor(WorkoutType type) {
        return switch (type) {
            case MUSCULATION -> "Échauffement 10 min, tempo contrôlé sur chaque série.";
            case CARDIO -> "Sortie d'endurance, respiration nasale sur la première moitié.";
            case HIIT -> "6 blocs d'effort, récupération 60 s entre les blocs.";
            case MOBILITE -> "Séance de récupération active et mobilité des hanches.";
            default -> null;
        };
    }
}
