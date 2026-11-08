package com.fittrack.domain;

/**
 * Types de seance proposes par FitTrack. Le MET (Metabolic Equivalent of Task)
 * sert a estimer la depense calorique.
 */
public enum WorkoutType {

    MUSCULATION("Musculation", 5.0),
    CARDIO("Cardio", 8.0),
    HIIT("HIIT", 9.0),
    MOBILITE("Mobilité", 3.0),
    SPORT("Sport collectif", 7.0);

    private final String label;
    private final double met;

    WorkoutType(String label, double met) {
        this.label = label;
        this.met = met;
    }

    public String getLabel() {
        return label;
    }

    public double getMet() {
        return met;
    }

    public String getCssClass() {
        return switch (this) {
            case MUSCULATION -> "badge-accent";
            case CARDIO -> "badge-blue";
            case HIIT -> "badge-danger";
            case MOBILITE -> "badge-warn";
            default -> "";
        };
    }
}
