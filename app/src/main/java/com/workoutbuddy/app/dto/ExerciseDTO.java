package com.workoutbuddy.app.dto;

import static com.workoutbuddy.app.model.Utilities.convertToEquipment;
import static com.workoutbuddy.app.model.Utilities.convertToMuscleGroup;

import com.workoutbuddy.app.model.Equipment;
import com.workoutbuddy.app.model.Exercise;
import com.workoutbuddy.app.model.MuscleGroup;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

public class ExerciseDTO {
    private String name;
    private String primaryMuscleGroup;
    private List<String> secondaryMuscleGroups;
    private List<String> equipment;
    private int difficultyLevel;
    private String key;

    public ExerciseDTO() {
        // Default constructor required for Firebase deserialization
    }

    public ExerciseDTO(String key, String name, String primaryMuscleGroup, List<String> secondaryMuscleGroups,
                       List<String> equipment, int difficultyLevel) {
        this.key = key;
        this.name = name;
        this.primaryMuscleGroup = primaryMuscleGroup;
        this.secondaryMuscleGroups = secondaryMuscleGroups;
        this.equipment = equipment;
        this.difficultyLevel = difficultyLevel;
    }

    public ExerciseDTO(ExerciseDTO exerciseDTO) {
        this.key = exerciseDTO.key;
        this.name = exerciseDTO.name;
        this.primaryMuscleGroup = exerciseDTO.primaryMuscleGroup;
        this.secondaryMuscleGroups = exerciseDTO.secondaryMuscleGroups;
        this.equipment = exerciseDTO.equipment;
        this.difficultyLevel = exerciseDTO.difficultyLevel;

    }

    // Getters and setters

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPrimaryMuscleGroup() {
        return primaryMuscleGroup;
    }

    public void setPrimaryMuscleGroup(String primaryMuscleGroup) {
        this.primaryMuscleGroup = primaryMuscleGroup;
    }

    public List<String> getSecondaryMuscleGroups() {
        return secondaryMuscleGroups;
    }

    public void setSecondaryMuscleGroups(List<String> secondaryMuscleGroups) {
        this.secondaryMuscleGroups = secondaryMuscleGroups;
    }

    public List<String> getEquipment() {
        if (equipment == null) {
            List<String> noEquipmentSet = new ArrayList<>();
            noEquipmentSet.add("No Equipment");
            return noEquipmentSet;
        }
        return equipment;
    }

    public void setEquipment(List<String> equipment) {
        this.equipment = equipment;
    }

    public int getDifficultyLevel() {
        return difficultyLevel;
    }

    public void setDifficultyLevel(int difficultyLevel) {
        this.difficultyLevel = difficultyLevel;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public Exercise toExercise() {
        Exercise exercise = new Exercise(key ,name, convertToMuscleGroup(primaryMuscleGroup),
                convertToMuscleGroupSet(secondaryMuscleGroups), convertToEquipmentSet(equipment),
                difficultyLevel);
        exercise.setKey(key);
        return exercise;
    }

    private Set<MuscleGroup> convertToMuscleGroupSet(List<String> muscleGroupStrings) {
        if (muscleGroupStrings == null) {
            muscleGroupStrings = new ArrayList<>();
        }
        Set<MuscleGroup> muscleGroups = new LinkedHashSet<>();
        for (String muscleGroupString : muscleGroupStrings) {
            muscleGroups.add(convertToMuscleGroup(muscleGroupString));
        }
        return muscleGroups;
    }

    private Set<Equipment> convertToEquipmentSet(List<String> equipmentStrings) {
        if (equipmentStrings == null) {
            equipmentStrings = new ArrayList<>();
        }
        Set<Equipment> equipmentSet = new LinkedHashSet<>();
        for (String equipmentString : equipmentStrings) {
            equipmentSet.add(convertToEquipment(equipmentString));
        }
        return equipmentSet;
    }

}
