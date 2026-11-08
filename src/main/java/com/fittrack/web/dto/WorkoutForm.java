package com.fittrack.web.dto;

import com.fittrack.domain.Intensity;
import com.fittrack.domain.WorkoutType;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;

public class WorkoutForm {

    @NotNull(message = "La date de la seance est obligatoire.")
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    private LocalDate date = LocalDate.now();

    @NotNull(message = "Choisissez un type de seance.")
    private WorkoutType type = WorkoutType.MUSCULATION;

    @Min(value = 5, message = "Duree minimale : 5 minutes.")
    @Max(value = 600, message = "Duree maximale : 600 minutes.")
    private int durationMinutes = 45;

    @NotNull(message = "Choisissez une intensite.")
    private Intensity intensity = Intensity.MODEREE;

    @Size(max = 600, message = "600 caracteres maximum.")
    private String notes;

    public LocalDate getDate() {
        return date;
    }

    public void setDate(LocalDate date) {
        this.date = date;
    }

    public WorkoutType getType() {
        return type;
    }

    public void setType(WorkoutType type) {
        this.type = type;
    }

    public int getDurationMinutes() {
        return durationMinutes;
    }

    public void setDurationMinutes(int durationMinutes) {
        this.durationMinutes = durationMinutes;
    }

    public Intensity getIntensity() {
        return intensity;
    }

    public void setIntensity(Intensity intensity) {
        this.intensity = intensity;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }
}
