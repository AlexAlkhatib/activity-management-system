package com.fittrack.web.dto;

import com.fittrack.domain.ActivityLevel;
import com.fittrack.domain.Gender;
import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;

public class ProfileForm {

    @NotBlank(message = "Le prenom est obligatoire.")
    private String firstName;

    @NotBlank(message = "Le nom est obligatoire.")
    private String lastName;

    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    private LocalDate birthDate;

    private Gender gender = Gender.AUTRE;

    @DecimalMin(value = "100.0", message = "Taille invalide (100 a 250 cm).")
    @DecimalMax(value = "250.0", message = "Taille invalide (100 a 250 cm).")
    private Double heightCm;

    @DecimalMin(value = "30.0", message = "Objectif de poids invalide.")
    @DecimalMax(value = "250.0", message = "Objectif de poids invalide.")
    private Double targetWeightKg;

    @Min(value = 1, message = "Objectif hebdomadaire entre 1 et 7.")
    @Max(value = 7, message = "Objectif hebdomadaire entre 1 et 7.")
    private int weeklyGoal = 3;

    private ActivityLevel activityLevel = ActivityLevel.MODERE;

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public LocalDate getBirthDate() {
        return birthDate;
    }

    public void setBirthDate(LocalDate birthDate) {
        this.birthDate = birthDate;
    }

    public Gender getGender() {
        return gender;
    }

    public void setGender(Gender gender) {
        this.gender = gender;
    }

    public Double getHeightCm() {
        return heightCm;
    }

    public void setHeightCm(Double heightCm) {
        this.heightCm = heightCm;
    }

    public Double getTargetWeightKg() {
        return targetWeightKg;
    }

    public void setTargetWeightKg(Double targetWeightKg) {
        this.targetWeightKg = targetWeightKg;
    }

    public int getWeeklyGoal() {
        return weeklyGoal;
    }

    public void setWeeklyGoal(int weeklyGoal) {
        this.weeklyGoal = weeklyGoal;
    }

    public ActivityLevel getActivityLevel() {
        return activityLevel;
    }

    public void setActivityLevel(ActivityLevel activityLevel) {
        this.activityLevel = activityLevel;
    }
}
