package com.workoutbuddy.app.model;

import com.workoutbuddy.app.dto.ExerciseDTO;

import java.io.Serializable;
import java.net.URL;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

public class Exercise implements Serializable {
    private String name;
    private MuscleGroup primaryMuscleGroup;
    private Set<MuscleGroup> secondaryMuscleGroups;
    private Set<Equipment> equipment;
    private int difficultyLevel;
    private String key;

    //private URL url;

    public Exercise(String key, String name, MuscleGroup primaryMuscleGroup, Set<MuscleGroup> secondaryMuscleGroups,
                    Set<Equipment> equipment, int difficultyLevel) {
        this.key = key;
        this.name = name;
        this.primaryMuscleGroup = primaryMuscleGroup;
        this.secondaryMuscleGroups = secondaryMuscleGroups;
        this.equipment = equipment;
        this.difficultyLevel = difficultyLevel;
    }

    // Copy constructor
    public Exercise(Exercise exercise) {
        this.key = exercise.key;
        this.name = exercise.name;
        this.primaryMuscleGroup = exercise.primaryMuscleGroup;
        this.secondaryMuscleGroups = new LinkedHashSet<>(exercise.secondaryMuscleGroups);
        this.equipment = new LinkedHashSet<>(exercise.equipment);
        this.difficultyLevel = exercise.difficultyLevel;
    }

    // Getters and setters

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public MuscleGroup getPrimaryMuscleGroup() {
        return primaryMuscleGroup;
    }

    public void setPrimaryMuscleGroup(MuscleGroup primaryMuscleGroup) {
        this.primaryMuscleGroup = primaryMuscleGroup;
    }

    public Set<MuscleGroup> getSecondaryMuscleGroups() {
        return secondaryMuscleGroups;
    }

    public void setSecondaryMuscleGroups(Set<MuscleGroup> secondaryMuscleGroups) {
        this.secondaryMuscleGroups = secondaryMuscleGroups;
    }

    public Set<Equipment> getEquipment() {
        if (equipment == null) {
            Set<Equipment> noEquipmentSet = new LinkedHashSet<>();
            noEquipmentSet.add(Equipment.NO_EQUIPMENT);
            return noEquipmentSet;
        }
        return equipment;
    }


    public void setEquipment(Set<Equipment> equipment) {
        this.equipment = equipment;
    }

    public int getDifficultyLevel() {
        return difficultyLevel;
    }

    public void setDifficultyLevel(int difficultyLevel) {
        this.difficultyLevel = difficultyLevel;
    }

    public String getDifficultyAsString() {
        return getDifficultyAsString(difficultyLevel);
    }

    public static String getDifficultyAsString(int num) {
        String difficultyString;
        switch (num) {
            case 1:
                difficultyString = "Beginner";
                break;
            case 2:
                difficultyString = "Intermediate";
                break;
            case 3:
                difficultyString = "Advanced";
                break;
            default:
                difficultyString = "Unknown";
                break;
        }
        return difficultyString;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getKey() {
        return key;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null || getClass() != obj.getClass()) {
            return false;
        }
        Exercise other = (Exercise) obj;
        return name.equals(other.name)
                && primaryMuscleGroup == other.primaryMuscleGroup
                && secondaryMuscleGroups.equals(other.secondaryMuscleGroups)
                && equipment.equals(other.equipment)
                && difficultyLevel == other.difficultyLevel;
    }

    public ExerciseDTO toExerciseDTO() {
        List<String> secondaryMuscleGroupStrings = convertToMuscleGroupList(secondaryMuscleGroups);
        List<String> equipmentStrings = convertToEquipmentList(equipment);
        return new ExerciseDTO(key ,name, primaryMuscleGroup.name(), secondaryMuscleGroupStrings, equipmentStrings, difficultyLevel);
    }

    private List<String> convertToMuscleGroupList(Set<MuscleGroup> muscleGroups) {
        List<String> muscleGroupStrings = new ArrayList<>();
        for (MuscleGroup muscleGroup : muscleGroups) {
            muscleGroupStrings.add(muscleGroup.name());
        }
        return muscleGroupStrings;
    }

    private List<String> convertToEquipmentList(Set<Equipment> equipmentSet) {
        List<String> equipmentStrings = new ArrayList<>();
        for (Equipment equipment : equipmentSet) {
            equipmentStrings.add(equipment.name());
        }
        return equipmentStrings;
    }

    public String convertMuscleGroupSetToString() {
        StringBuilder stringBuilder = new StringBuilder();
        for (MuscleGroup muscleGroup : secondaryMuscleGroups) {
            if (stringBuilder.length() > 0) {
                stringBuilder.append(", ");
            }
            stringBuilder.append(muscleGroup.toString());
        }
        return stringBuilder.toString();
    }

    public String convertEquipmentSetToString() {
        StringBuilder stringBuilder = new StringBuilder();
        for (Equipment equipment : this.equipment) {
            if (stringBuilder.length() > 0) {
                stringBuilder.append(", ");
            }
            stringBuilder.append(equipment.toString());
        }
        return stringBuilder.toString();
    }
}
