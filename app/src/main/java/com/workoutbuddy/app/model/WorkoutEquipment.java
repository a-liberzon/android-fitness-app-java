package com.workoutbuddy.app.model;

public class WorkoutEquipment {
    // Enum defining the allowed equipment options
    public enum EquipmentOption {

        // Add more options as needed
    }

    private EquipmentOption equipment;

    // Constructor
    public WorkoutEquipment(EquipmentOption equipment) {
        this.equipment = equipment;
    }

    // Getter
    public EquipmentOption getEquipment() {
        return equipment;
    }


}