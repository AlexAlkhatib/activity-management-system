package com.fittrack.web;

import com.fittrack.domain.User;
import com.fittrack.service.DashboardService;
import com.fittrack.service.UserService;
import com.fittrack.web.view.DashboardView;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * API REST en lecture seule, utile pour une future application mobile.
 */
@RestController
@RequestMapping("/api")
public class StatsApiController {

    private final DashboardService dashboardService;
    private final UserService userService;

    public StatsApiController(DashboardService dashboardService, UserService userService) {
        this.dashboardService = dashboardService;
        this.userService = userService;
    }

    @GetMapping("/stats/summary")
    public Map<String, Object> summary() {
        User user = userService.getCurrentUser();
        DashboardView view = dashboardService.build();
        Map<String, Object> payload = new LinkedHashMap<>();
        payload.put("athlete", user.getFullName());
        payload.put("week", Map.of(
                "sessions", view.sessionsThisWeek(),
                "goal", view.weeklyGoal(),
                "minutes", view.minutesThisWeek(),
                "calories", view.caloriesThisWeek()
        ));
        payload.put("streakWeeks", view.streakWeeks());
        payload.put("totalSessions", view.totalSessions());
        payload.put("totalMinutes", view.totalMinutes());
        payload.put("latestWeightKg", view.latestWeight());
        payload.put("bmi", view.bmi());
        payload.put("dailyMinutes", view.days().stream()
                .map(day -> Map.of("label", day.label(), "minutes", day.minutes()))
                .toList());
        payload.put("weeklySessions", view.weeklyHistory().stream()
                .map(week -> Map.of("week", week.label(), "sessions", week.sessions()))
                .toList());
        return payload;
    }
}
