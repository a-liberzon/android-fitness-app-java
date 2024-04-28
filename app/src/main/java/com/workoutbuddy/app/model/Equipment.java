package com.workoutbuddy.app.model;

public enum Equipment {
    NO_EQUIPMENT("No Equipment"),
    BARBELL("Barbell"),
    DUMBBELL("Dumbbell"),
    KETTLEBELL("Kettlebell"),
    RESISTANCE_BAND("Resistance Band"),
    ANKLE_WEIGHTS("Ankle Weights"),
    PULL_UP_BAR("Pull-up Bar"),
    JUMP_ROPE("Jump Rope"),
    PARALLEL_BARS("Parallel Bars"),
    TRX("TRX"),
    MEDICINE_BALL("Medicine Ball");

    private String description;

    private Equipment(String description) {
        this.description = description;
    }

    // Override the toString() method
    @Override
    public String toString() {
        return description;
    }
}
