package com.fittrack.web.dto;

import com.fittrack.domain.MuscleGroup;
import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public class ExerciseForm {

    @NotBlank(message = "Le nom de l'exercice est obligatoire.")
    @Size(max = 120, message = "120 caracteres maximum.")
    private String name;

    @NotNull(message = "Choisissez un groupe musculaire.")
    private MuscleGroup muscleGroup = MuscleGroup.CORPS_ENTIER;

    @Size(max = 400, message = "400 caracteres maximum.")
    private String description;

    @DecimalMin(value = "1.0", message = "MET entre 1 et 20.")
    @DecimalMax(value = "20.0", message = "MET entre 1 et 20.")
    private double met = 5.0;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public MuscleGroup getMuscleGroup() {
        return muscleGroup;
    }

    public void setMuscleGroup(MuscleGroup muscleGroup) {
        this.muscleGroup = muscleGroup;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public double getMet() {
        return met;
    }

    public void setMet(double met) {
        this.met = met;
    }
}
