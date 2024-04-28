package com.workoutbuddy.app.model;

public class Area {
    // Enum defining the allowed areas
    public enum AreaOption {
        ABS,
        BACK,
        BICEPS,
        CARDIO,
        CHEST,
        LEGS,
        SHOULDERS,
        TRICEPS
        // Add more options as needed
    }

    private AreaOption areas;

    // Constructor
    public Area(AreaOption newAreas) {
        this.areas = newAreas;
    }

    // Getter
    public AreaOption getArea() {
        return this.areas;
    }
}
