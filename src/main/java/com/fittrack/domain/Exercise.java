package com.fittrack.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

/**
 * Exercice de la bibliotheque (integre ou cree par un utilisateur).
 */
@Entity
@Table(name = "exercises")
public class Exercise {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 120)
    private String name;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private MuscleGroup muscleGroup = MuscleGroup.CORPS_ENTIER;

    @Column(length = 400)
    private String description;

    @Column(nullable = false)
    private double met = 5.0;

    /** null pour un exercice integre a l'application. */
    private Long createdBy;

    public Exercise() {
    }

    public Exercise(String name, MuscleGroup muscleGroup, String description, double met) {
        this.name = name;
        this.muscleGroup = muscleGroup;
        this.description = description;
        this.met = met;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

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

    public Long getCreatedBy() {
        return createdBy;
    }

    public void setCreatedBy(Long createdBy) {
        this.createdBy = createdBy;
    }

    public boolean isCustom() {
        return createdBy != null;
    }
}
