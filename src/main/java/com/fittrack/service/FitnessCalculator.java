package com.fittrack.service;

import com.fittrack.domain.ActivityLevel;
import com.fittrack.domain.Gender;
import com.fittrack.domain.Intensity;
import com.fittrack.domain.WorkoutType;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Calculs metier partages par les services et les tests.
 */
public final class FitnessCalculator {

    private FitnessCalculator() {
    }

    /**
     * Estimation de la depense calorique : MET x facteur d'intensite x 3,5 x poids / 200 x minutes.
     */
    public static int estimateCalories(WorkoutType type, Intensity intensity, int minutes, double weightKg) {
        if (type == null || intensity == null || minutes <= 0 || weightKg <= 0) {
            return 0;
        }
        double met = type.getMet() * intensity.getFactor();
        return (int) Math.round(met * 3.5 * weightKg / 200.0 * minutes);
    }

    public static double bmi(double heightCm, double weightKg) {
        if (heightCm <= 0 || weightKg <= 0) {
            return 0;
        }
        double meters = heightCm / 100.0;
        return round1(weightKg / (meters * meters));
    }

    public static String bmiCategory(double bmi) {
        if (bmi <= 0) {
            return "Non calcule";
        }
        if (bmi < 18.5) {
            return "Insuffisance ponderale";
        }
        if (bmi < 25) {
            return "Corpulence normale";
        }
        if (bmi < 30) {
            return "Surpoids";
        }
        return "Obesite";
    }

    /** Metabolisme de base, formule de Mifflin-St Jeor. */
    public static int basalMetabolism(double weightKg, double heightCm, int age, Gender gender) {
        if (weightKg <= 0 || heightCm <= 0 || age <= 0) {
            return 0;
        }
        double value = 10 * weightKg + 6.25 * heightCm - 5 * age;
        value += gender == Gender.FEMME ? -161 : 5;
        return (int) Math.round(value);
    }

    /** Depense energetique totale journaliere estimee. */
    public static int totalDailyEnergy(int basalMetabolism, ActivityLevel level) {
        if (basalMetabolism <= 0) {
            return 0;
        }
        double factor = level == null ? 1.55 : level.getFactor();
        return (int) Math.round(basalMetabolism * factor);
    }

    /**
     * Nombre de semaines consecutives (en remontant depuis cette semaine) contenant au moins une seance.
     * La semaine en cours peut etre vide sans casser la serie, tant que la precedente est active.
     */
    public static int weekStreak(List<LocalDate> workoutDates, LocalDate today) {
        if (workoutDates == null || workoutDates.isEmpty() || today == null) {
            return 0;
        }
        Set<LocalDate> activeWeeks = new HashSet<>();
        for (LocalDate date : workoutDates) {
            if (date != null) {
                activeWeeks.add(mondayOf(date));
            }
        }
        LocalDate cursor = mondayOf(today);
        if (!activeWeeks.contains(cursor)) {
            cursor = cursor.minusWeeks(1);
        }
        int streak = 0;
        while (activeWeeks.contains(cursor)) {
            streak++;
            cursor = cursor.minusWeeks(1);
        }
        return streak;
    }

    public static LocalDate mondayOf(LocalDate date) {
        return date.with(DayOfWeek.MONDAY);
    }

    public static long daysBetween(LocalDate from, LocalDate to) {
        return ChronoUnit.DAYS.between(from, to);
    }

    public static double round1(double value) {
        return Math.round(value * 10.0) / 10.0;
    }
}
