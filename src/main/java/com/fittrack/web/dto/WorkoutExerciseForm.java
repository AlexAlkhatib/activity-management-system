package com.fittrack.web.dto;

import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public class WorkoutExerciseForm {

    @NotNull(message = "Selectionnez un exercice.")
    private Long exerciseId;

    @Min(value = 0, message = "Valeur positive attendue.")
    @Max(value = 100, message = "100 series maximum.")
    private Integer sets;

    @Min(value = 0, message = "Valeur positive attendue.")
    @Max(value = 1000, message = "1000 repetitions maximum.")
    private Integer reps;

    @DecimalMin(value = "0.0", message = "Charge positive attendue.")
    @DecimalMax(value = "600.0", message = "Charge maximale : 600 kg.")
    private Double weightKg;

    @Min(value = 0, message = "Valeur positive attendue.")
    @Max(value = 36000, message = "Duree maximale : 10 heures.")
    private Integer durationSeconds;

    @DecimalMin(value = "0.0", message = "Distance positive attendue.")
    @DecimalMax(value = "500.0", message = "Distance maximale : 500 km.")
    private Double distanceKm;

    @Size(max = 300, message = "300 caracteres maximum.")
    private String comment;

    public Long getExerciseId() {
        return exerciseId;
    }

    public void setExerciseId(Long exerciseId) {
        this.exerciseId = exerciseId;
    }

    public Integer getSets() {
        return sets;
    }

    public void setSets(Integer sets) {
        this.sets = sets;
    }

    public Integer getReps() {
        return reps;
    }

    public void setReps(Integer reps) {
        this.reps = reps;
    }

    public Double getWeightKg() {
        return weightKg;
    }

    public void setWeightKg(Double weightKg) {
        this.weightKg = weightKg;
    }

    public Integer getDurationSeconds() {
        return durationSeconds;
    }

    public void setDurationSeconds(Integer durationSeconds) {
        this.durationSeconds = durationSeconds;
    }

    public Double getDistanceKm() {
        return distanceKm;
    }

    public void setDistanceKm(Double distanceKm) {
        this.distanceKm = distanceKm;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }
}
