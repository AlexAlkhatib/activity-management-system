package com.fittrack.web.dto;

import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;

public class MeasurementForm {

    @NotNull(message = "La date est obligatoire.")
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    private LocalDate date = LocalDate.now();

    @NotNull(message = "Le poids est obligatoire.")
    @DecimalMin(value = "20.0", message = "Poids invalide (20 a 350 kg).")
    @DecimalMax(value = "350.0", message = "Poids invalide (20 a 350 kg).")
    private Double weightKg;

    @DecimalMin(value = "1.0", message = "Pourcentage invalide.")
    @DecimalMax(value = "70.0", message = "Pourcentage invalide.")
    private Double bodyFatPct;

    @DecimalMin(value = "30.0", message = "Mensuration invalide.")
    @DecimalMax(value = "200.0", message = "Mensuration invalide.")
    private Double waistCm;

    @DecimalMin(value = "30.0", message = "Mensuration invalide.")
    @DecimalMax(value = "200.0", message = "Mensuration invalide.")
    private Double chestCm;

    @Size(max = 300, message = "300 caracteres maximum.")
    private String notes;

    public LocalDate getDate() {
        return date;
    }

    public void setDate(LocalDate date) {
        this.date = date;
    }

    public Double getWeightKg() {
        return weightKg;
    }

    public void setWeightKg(Double weightKg) {
        this.weightKg = weightKg;
    }

    public Double getBodyFatPct() {
        return bodyFatPct;
    }

    public void setBodyFatPct(Double bodyFatPct) {
        this.bodyFatPct = bodyFatPct;
    }

    public Double getWaistCm() {
        return waistCm;
    }

    public void setWaistCm(Double waistCm) {
        this.waistCm = waistCm;
    }

    public Double getChestCm() {
        return chestCm;
    }

    public void setChestCm(Double chestCm) {
        this.chestCm = chestCm;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }
}
