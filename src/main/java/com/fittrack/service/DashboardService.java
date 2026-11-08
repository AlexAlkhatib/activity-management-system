package com.fittrack.service;

import com.fittrack.domain.Measurement;
import com.fittrack.domain.User;
import com.fittrack.domain.Workout;
import com.fittrack.domain.WorkoutExercise;
import com.fittrack.repository.MeasurementRepository;
import com.fittrack.repository.WorkoutRepository;
import com.fittrack.web.view.DashboardView;
import com.fittrack.web.view.DashboardView.ChartSeries;
import com.fittrack.web.view.DashboardView.DayBar;
import com.fittrack.web.view.DashboardView.PersonalBest;
import com.fittrack.web.view.DashboardView.WeekBar;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.TextStyle;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;

/**
 * Agrege les donnees de l'utilisateur connecte pour le tableau de bord.
 */
@Service
public class DashboardService {

    private static final Locale FR = Locale.FRENCH;
    private static final DateTimeFormatter WEEK_LABEL = DateTimeFormatter.ofPattern("dd/MM", FR);

    private final WorkoutRepository workoutRepository;
    private final MeasurementRepository measurementRepository;
    private final UserService userService;

    public DashboardService(WorkoutRepository workoutRepository,
                            MeasurementRepository measurementRepository,
                            UserService userService) {
        this.workoutRepository = workoutRepository;
        this.measurementRepository = measurementRepository;
        this.userService = userService;
    }

    @Transactional(readOnly = true)
    public DashboardView build() {
        User user = userService.getCurrentUser();
        LocalDate today = LocalDate.now();
        LocalDate monday = FitnessCalculator.mondayOf(today);

        List<Workout> workouts = workoutRepository.findByUserOrderByDateDescIdDesc(user);
        List<Workout> thisWeek = workouts.stream()
                .filter(w -> !w.getDate().isBefore(monday) && !w.getDate().isAfter(today))
                .toList();

        int sessions = thisWeek.size();
        int minutes = thisWeek.stream().mapToInt(Workout::getDurationMinutes).sum();
        int calories = thisWeek.stream().mapToInt(Workout::getCalories).sum();
        int goal = Math.max(1, user.getWeeklyGoal());
        int goalPercent = (int) Math.min(100, Math.round(sessions * 100.0 / goal));

        List<Measurement> measurements = measurementRepository.findByUserOrderByDateDescIdDesc(user);
        Double latestWeight = measurements.isEmpty() ? null : measurements.get(0).getWeightKg();
        Double previousWeight = measurements.size() > 1 ? measurements.get(1).getWeightKg() : null;
        Double weightDelta = (latestWeight != null && previousWeight != null)
                ? FitnessCalculator.round1(latestWeight - previousWeight)
                : null;
        double bmi = (latestWeight != null && user.getHeightCm() != null)
                ? FitnessCalculator.bmi(user.getHeightCm(), latestWeight)
                : 0;

        List<Measurement> chartMeasurements = measurementRepository
                .findTop10ByUserOrderByDateDescIdDesc(user);
        Collections.reverse(chartMeasurements);
        List<Double> weightValues = chartMeasurements.stream().map(Measurement::getWeightKg).toList();

        int maxWeight = 0;
        String maxWeightExercise = null;
        int longest = 0;
        for (Workout workout : workouts) {
            longest = Math.max(longest, workout.getDurationMinutes());
            for (WorkoutExercise entry : workout.getExercises()) {
                if (entry.getWeightKg() != null && entry.getWeightKg() > maxWeight) {
                    maxWeight = (int) Math.round(entry.getWeightKg());
                    maxWeightExercise = entry.getExercise().getName();
                }
            }
        }

        return new DashboardView(
                sessions,
                goal,
                goalPercent,
                minutes,
                calories,
                FitnessCalculator.weekStreak(workouts.stream().map(Workout::getDate).toList(), today),
                workouts.size(),
                workouts.stream().mapToInt(Workout::getDurationMinutes).sum(),
                latestWeight,
                weightDelta,
                user.getTargetWeightKg(),
                bmi,
                FitnessCalculator.bmiCategory(bmi),
                buildDays(workouts, today),
                buildWeeks(workouts, monday),
                buildChart(weightValues),
                workouts.stream().limit(5).toList(),
                new PersonalBest(maxWeight, maxWeightExercise, longest),
                workouts.isEmpty()
        );
    }

    private List<DayBar> buildDays(List<Workout> workouts, LocalDate today) {
        List<Integer> minutesPerDay = new ArrayList<>();
        int max = 1;
        for (int i = 6; i >= 0; i--) {
            LocalDate day = today.minusDays(i);
            int sum = workouts.stream()
                    .filter(w -> day.equals(w.getDate()))
                    .mapToInt(Workout::getDurationMinutes)
                    .sum();
            minutesPerDay.add(sum);
            max = Math.max(max, sum);
        }
        List<DayBar> bars = new ArrayList<>();
        for (int i = 0; i < minutesPerDay.size(); i++) {
            LocalDate day = today.minusDays(6 - i);
            int minutes = minutesPerDay.get(i);
            int percent = minutes == 0 ? 0 : Math.max(12, (int) Math.round(minutes * 100.0 / max));
            bars.add(new DayBar(day.getDayOfWeek().getDisplayName(TextStyle.SHORT, FR), minutes, percent,
                    day.equals(today)));
        }
        return bars;
    }

    private List<WeekBar> buildWeeks(List<Workout> workouts, LocalDate currentMonday) {
        int[] counts = new int[8];
        int max = 1;
        for (int i = 7; i >= 0; i--) {
            LocalDate start = currentMonday.minusWeeks(i);
            LocalDate end = start.plusDays(6);
            int count = (int) workouts.stream()
                    .filter(w -> !w.getDate().isBefore(start) && !w.getDate().isAfter(end))
                    .count();
            counts[7 - i] = count;
            max = Math.max(max, count);
        }
        List<WeekBar> bars = new ArrayList<>();
        for (int i = 0; i < counts.length; i++) {
            LocalDate start = currentMonday.minusWeeks(7 - i);
            int count = counts[i];
            int percent = count == 0 ? 0 : Math.max(10, (int) Math.round(count * 100.0 / max));
            bars.add(new WeekBar(WEEK_LABEL.format(start), count, percent, i == counts.length - 1));
        }
        return bars;
    }

    /**
     * Construit une polyligne SVG (viewBox 0 0 300 100) a partir d'une serie de valeurs.
     */
    private ChartSeries buildChart(List<Double> values) {
        if (values == null || values.size() < 2) {
            return new ChartSeries("", "", 0, 0, false);
        }
        double min = values.stream().min(Comparator.naturalOrder()).orElse(0.0);
        double max = values.stream().max(Comparator.naturalOrder()).orElse(0.0);
        if (max - min < 1.0) {
            max = max + 0.6;
            min = min - 0.6;
        }
        double width = 300;
        double height = 100;
        double padX = 8;
        double padY = 12;
        StringBuilder points = new StringBuilder();
        for (int i = 0; i < values.size(); i++) {
            double x = padX + (width - 2 * padX) * i / (values.size() - 1.0);
            double ratio = (values.get(i) - min) / (max - min);
            double y = padY + (height - 2 * padY) * (1 - ratio);
            points.append(String.format(Locale.US, "%.1f,%.1f ", x, y));
        }
        String line = points.toString().trim();
        String area = line + " " + String.format(Locale.US, "%.1f,%.1f %.1f,%.1f",
                width - padX, height, padX, height);
        return new ChartSeries(line, area,
                FitnessCalculator.round1(min), FitnessCalculator.round1(max), true);
    }
}
