package com.fittrack.domain;

/**
 * Intensite percue de la seance : module l'estimation calorique.
 */
public enum Intensity {

    FAIBLE("Faible", 0.85),
    MODEREE("Modérée", 1.0),
    ELEVEE("Élevée", 1.2);

    private final String label;
    private final double factor;

    Intensity(String label, double factor) {
        this.label = label;
        this.factor = factor;
    }

    public String getLabel() {
        return label;
    }

    public double getFactor() {
        return factor;
    }

    public String getCssClass() {
        return switch (this) {
            case FAIBLE -> "badge-warn";
            case ELEVEE -> "badge-accent";
            default -> "badge-blue";
        };
    }
}
