package com.fittrack.domain;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OrderBy;
import jakarta.persistence.Table;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Seance d'entrainement realisee par un utilisateur.
 */
@Entity
@Table(name = "workouts")
public class Workout {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(nullable = false)
    private LocalDate date = LocalDate.now();

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private WorkoutType type = WorkoutType.MUSCULATION;

    @Column(nullable = false)
    private int durationMinutes = 45;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Intensity intensity = Intensity.MODEREE;

    @Column(nullable = false)
    private int calories;

    @Column(length = 600)
    private String notes;

    @Column(nullable = false)
    private LocalDateTime createdAt = LocalDateTime.now();

    @OneToMany(mappedBy = "workout", cascade = CascadeType.ALL, orphanRemoval = true)
    @OrderBy("position ASC")
    private List<WorkoutExercise> exercises = new ArrayList<>();

    public Workout() {
    }

    public void addExercise(WorkoutExercise entry) {
        entry.setWorkout(this);
        entry.setPosition(exercises.size());
        exercises.add(entry);
    }

    public void removeExercise(WorkoutExercise entry) {
        exercises.remove(entry);
        entry.setWorkout(null);
        for (int i = 0; i < exercises.size(); i++) {
            exercises.get(i).setPosition(i);
        }
    }

    /** Volume total souleve (somme series x repetitions x charge). */
    public double getVolumeKg() {
        return exercises.stream()
                .filter(e -> e.getWeightKg() != null && e.getSets() != null && e.getReps() != null)
                .mapToDouble(e -> e.getWeightKg() * e.getSets() * e.getReps())
                .sum();
    }

    public int getTotalSets() {
        return exercises.stream()
                .filter(e -> e.getSets() != null)
                .mapToInt(WorkoutExercise::getSets)
                .sum();
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

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

    public int getCalories() {
        return calories;
    }

    public void setCalories(int calories) {
        this.calories = calories;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public List<WorkoutExercise> getExercises() {
        return exercises;
    }

    public void setExercises(List<WorkoutExercise> exercises) {
        this.exercises = exercises;
    }
}
