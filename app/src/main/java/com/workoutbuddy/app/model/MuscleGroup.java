package com.workoutbuddy.app.model;

public enum MuscleGroup {
    // Front
    CHEST("Chest"),
    ABS("Abs"),
    OBLIQUES("Obliques"),

    // Back
    LOWER_BACK("Lower Back"),
    TRAPS("Traps"),
    LATS("Lats"),

    // Arms
    SHOULDERS("Shoulders"),
    BICEPS("Biceps"),
    TRICEPS("Triceps"),
    FOREARMS("Forearms"),

    // Legs
    QUADS("Quads"),
    HAMSTRINGS("Hamstrings"),
    GLUTES("Glutes"),
    ABDUCTORS("Abductors"),
    ADDUCTORS("Adductors"),
    CALVES("Calves"),

    // Cardio
    CARDIO("Cardio");

    private String description;

    private MuscleGroup(String description) {
        this.description = description;
    }

    // Override the toString() method
    @Override
    public String toString() {
        return description;
    }
}

