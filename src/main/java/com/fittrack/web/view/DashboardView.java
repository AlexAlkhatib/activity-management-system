package com.fittrack.web.view;

import com.fittrack.domain.Workout;

import java.util.List;

/**
 * Donnees pretes a l'affichage du tableau de bord.
 */
public record DashboardView(
        int sessionsThisWeek,
        int weeklyGoal,
        int goalPercent,
        int minutesThisWeek,
        int caloriesThisWeek,
        int streakWeeks,
        long totalSessions,
        long totalMinutes,
        Double latestWeight,
        Double weightDelta,
        Double targetWeight,
        double bmi,
        String bmiCategory,
        List<DayBar> days,
        List<WeekBar> weeklyHistory,
        ChartSeries weightChart,
        List<Workout> recentWorkouts,
        PersonalBest personalBest,
        boolean empty
) {

    public record DayBar(String label, int minutes, int percent, boolean today) {
    }

    public record WeekBar(String label, int sessions, int percent, boolean current) {
    }

    public record ChartSeries(String points, String areaPoints, double min, double max, boolean hasData) {
    }

    public record PersonalBest(double maxWeightKg, String maxWeightExercise, int longestMinutes) {
    }
}
