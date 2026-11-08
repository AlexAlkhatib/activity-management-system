package com.fittrack.domain;

/**
 * Groupe musculaire principal d'un exercice.
 */
public enum MuscleGroup {

    PECTORAUX("Pectoraux"),
    DOS("Dos"),
    EPAULES("Épaules"),
    BICEPS("Biceps"),
    TRICEPS("Triceps"),
    JAMBES("Jambes"),
    FESSIERS("Fessiers"),
    ABDOMINAUX("Abdominaux"),
    MOLLETS("Mollets"),
    CARDIO("Cardio"),
    CORPS_ENTIER("Corps entier");

    private final String label;

    MuscleGroup(String label) {
        this.label = label;
    }

    public String getLabel() {
        return label;
    }
}
