package com.fittrack.domain;

/**
 * Niveau d'activite quotidien, utilise pour estimer la depense energetique totale (TDEE).
 */
public enum ActivityLevel {

    SEDENTAIRE("Sédentaire", 1.2, "Travail assis, peu de marche"),
    LEGER("Léger", 1.375, "1 à 2 séances par semaine"),
    MODERE("Modéré", 1.55, "3 à 5 séances par semaine"),
    INTENSE("Intense", 1.725, "6 séances ou plus par semaine");

    private final String label;
    private final double factor;
    private final String description;

    ActivityLevel(String label, double factor, String description) {
        this.label = label;
        this.factor = factor;
        this.description = description;
    }

    public String getLabel() {
        return label;
    }

    public double getFactor() {
        return factor;
    }

    public String getDescription() {
        return description;
    }
}
