package com.fittrack.service;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class FitnessCalculatorTest {

    @Test
    @DisplayName("Estimation calorique : 60 min de musculation a intensite moderee pese 80 kg")
    void estimateCaloriesUsesMetFormula() {
        int calories = FitnessCalculator.estimateCalories(
                com.fittrack.domain.WorkoutType.MUSCULATION,
                com.fittrack.domain.Intensity.MODEREE, 60, 80);
        // 5.0 MET x 3.5 x 80 / 200 x 60 = 420 kcal
        assertThat(calories).isEqualTo(420);
    }

    @Test
    @DisplayName("Une intensite elevee augmente la depense calorique")
    void higherIntensityBurnsMore() {
        int moderate = FitnessCalculator.estimateCalories(
                com.fittrack.domain.WorkoutType.HIIT, com.fittrack.domain.Intensity.MODEREE, 30, 75);
        int high = FitnessCalculator.estimateCalories(
                com.fittrack.domain.WorkoutType.HIIT, com.fittrack.domain.Intensity.ELEVEE, 30, 75);
        assertThat(high).isGreaterThan(moderate);
    }

    @Test
    @DisplayName("Donnees invalides : aucune calorie negative")
    void invalidInputReturnsZero() {
        assertThat(FitnessCalculator.estimateCalories(null, null, 0, 0)).isZero();
    }

    @Test
    @DisplayName("IMC et categorie")
    void computesBmiAndCategory() {
        assertThat(FitnessCalculator.bmi(178, 80.5)).isEqualTo(25.4);
        assertThat(FitnessCalculator.bmiCategory(22.0)).isEqualTo("Corpulence normale");
        assertThat(FitnessCalculator.bmiCategory(27.4)).isEqualTo("Surpoids");
    }

    @Test
    @DisplayName("Metabolisme de base : formule de Mifflin-St Jeor")
    void computesBasalMetabolism() {
        int bmr = FitnessCalculator.basalMetabolism(80, 178, 31, com.fittrack.domain.Gender.HOMME);
        // 10*80 + 6.25*178 - 5*31 + 5 = 1762.5 -> 1763
        assertThat(bmr).isEqualTo(1763);
    }

    @Test
    @DisplayName("Serie : deux semaines consecutives puis trou")
    void computesWeekStreak() {
        LocalDate today = LocalDate.of(2026, 11, 8); // dimanche
        List<LocalDate> dates = List.of(
                today.minusWeeks(1),
                today.minusWeeks(2),
                today.minusWeeks(2).plusDays(2),
                today.minusWeeks(4));
        assertThat(FitnessCalculator.weekStreak(dates, today)).isEqualTo(2);
    }

    @Test
    @DisplayName("Serie : semaine en cours vide, serie conservee")
    void keepsStreakWhenCurrentWeekIsEmpty() {
        LocalDate today = LocalDate.of(2026, 11, 8);
        List<LocalDate> dates = List.of(today.minusWeeks(1), today.minusWeeks(2), today.minusWeeks(3));
        assertThat(FitnessCalculator.weekStreak(dates, today)).isEqualTo(3);
    }
}
