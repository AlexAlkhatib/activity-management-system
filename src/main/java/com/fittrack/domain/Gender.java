package com.fittrack.domain;

public enum Gender {

    HOMME("Homme"),
    FEMME("Femme"),
    AUTRE("Autre");

    private final String label;

    Gender(String label) {
        this.label = label;
    }

    public String getLabel() {
        return label;
    }
}
