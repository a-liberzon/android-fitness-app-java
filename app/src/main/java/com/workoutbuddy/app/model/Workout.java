package com.workoutbuddy.app.model;

import com.workoutbuddy.app.dto.WorkoutDTO;
import com.workoutbuddy.app.dto.WorkoutExerciseDTO;

import java.io.Serializable;
import java.time.Duration;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.IntStream;

public class Workout implements Serializable {
    private String name;
    private String key;
    private int rounds;
    private int difficulty;
    private int totDifficulty = 0;
    private String category;
    private String equipment;
    private Set<MuscleGroup> muscleGroups;
    private Set<Equipment> equipmentSet;
    private List<WorkoutExercise> exerciseList;
    private Duration restBetweenExercises;
    private Duration restBetweenSets;
    private Duration restBetweenRounds;
    private Duration workoutTime;

    // TODO: Find a better way to deal with this (database will be inconsistent)
    private boolean usingImperial = true;
    private final Duration REP_TIME = Duration.ofSeconds(4);

    public Workout() {
        name = "N/A";
        rounds = 1;
        exerciseList = new ArrayList<>();
        difficulty = 0;
        muscleGroups = new LinkedHashSet<>();
        equipmentSet = new HashSet<>();
        workoutTime = Duration.ZERO;
    }

    // TODO: remove, just for testing
    public Workout(int i) {
        this.name = "Workout "+i;
        this.rounds = i % 2 == 0 ? 1 : 3;
        exerciseList = new ArrayList<>();
        difficulty = 1;
        muscleGroups = new LinkedHashSet<>();
        equipmentSet = new HashSet<>();
        workoutTime = Duration.ofMinutes(30);
        restBetweenExercises = Duration.ofSeconds(5);
        restBetweenSets = Duration.ofSeconds(10);
        restBetweenRounds = Duration.ofSeconds(15);
        //addDummyExercises(i*2);
        muscleGroups.add(MuscleGroup.LOWER_BACK);
        muscleGroups.add(MuscleGroup.BICEPS);
        determineEquipment();
        determineCategory();
        if (rounds > 1) {
            addRounds();
        }
    }

    // Constructor for new workouts
    public Workout(String name, int rounds) {
        this.name = name;
        this.rounds = rounds;
        exerciseList = new ArrayList<>();
        difficulty = 0;
        muscleGroups = new LinkedHashSet<>();
        equipmentSet = new HashSet<>();
        workoutTime = Duration.ZERO;
        // TODO: Set to app preferences
        restBetweenExercises = Duration.ofSeconds(30);
        restBetweenSets = Duration.ofSeconds(60);
        restBetweenRounds = Duration.ofSeconds(120);
        if (rounds > 1) {
            addRounds();
        }
    }

    public Workout(String key, String name, int rounds) {
        this.key = key;
        this.name = name;
        this.rounds = rounds;
        exerciseList = new ArrayList<>();
        difficulty = 0;
        muscleGroups = new LinkedHashSet<>();
        equipmentSet = new HashSet<>();
        workoutTime = Duration.ZERO;
        // TODO: Set to app preferences
        restBetweenExercises = Duration.ofSeconds(30);
        restBetweenSets = Duration.ofSeconds(60);
        restBetweenRounds = Duration.ofSeconds(120);
        if (rounds > 1) {
            addRounds();
        }
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public List<WorkoutExercise> getExerciseList() {
        return exerciseList;
    }

    public void setExerciseList(List<WorkoutExercise> exerciseList) {
        this.exerciseList = exerciseList;
    }

    public Duration getRestBetweenSets() {
        return restBetweenSets;
    }

    public void setRestBetweenSets(Duration restBetweenSets) {
        this.restBetweenSets = restBetweenSets;
    }

    public Duration getRestBetweenRounds() {
        return restBetweenRounds;
    }

    public void setRestBetweenRounds(Duration restBetweenRounds) {
        this.restBetweenRounds = restBetweenRounds;
    }

    public int getRounds() {
        return rounds;
    }

    public void setRounds(int rounds) {
        this.rounds = rounds;
    }

    public int getDifficulty() {
        return difficulty;
    }

    public String getDifficultyAsString() {
        return Exercise.getDifficultyAsString(difficulty);
    }

    public void setDifficulty(int difficulty) {
        this.difficulty = difficulty;
    }

    public Duration getWorkoutTime() {
        return workoutTime;
    }

    public void setWorkoutTime(Duration workoutTime) {
        this.workoutTime = workoutTime;
    }

    // TODO: Update method to handle more combinations
    public void determineCategory() {
        if (muscleGroups.isEmpty()) {
            category = "Uncategorized";
        } else if (muscleGroups.size() == 1) {
            category = muscleGroups.iterator().next().toString();
        } else if (muscleGroups.containsAll(Set.of(MuscleGroup.CHEST, MuscleGroup.TRICEPS))) {
            category = "Upper Body Push";
        } else if (muscleGroups.containsAll(Set.of(MuscleGroup.LOWER_BACK, MuscleGroup.BICEPS))) {
            category = "Upper Body Pull";
        } else if (muscleGroups.containsAll(Set.of(MuscleGroup.QUADS, MuscleGroup.HAMSTRINGS))) {
            category = "Legs";
        } else {
            category = "Mixed";
        }
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public void addExercise(WorkoutExercise exercise) {
        // Add exercise to the list of exercises
        exerciseList.add(exercise);

        // Add all the muscle groups in that exercise to the set of muscle groups
        muscleGroups.add(exercise.getPrimaryMuscleGroup());
        muscleGroups.addAll(exercise.getSecondaryMuscleGroups());

        // Add all the equipment needed for that exercise to the set of equipment
        equipmentSet.addAll(exercise.getEquipment());

        // Update category
        determineCategory();

        // Update equipment
        determineEquipment();

        // Calculate workout time
        calculateWorkoutTime();

        totDifficulty += exercise.getDifficultyLevel();
    }

    public String getEquipment() {
        return equipment;
    }

    public void setEquipment(String equipment) {
        this.equipment = equipment;
    }

    // TODO: Update method to handle more combinations
    public void determineEquipment() {
        if (equipmentSet == null) {
            equipment = "No equipment";
        }
        if (equipmentSet.size() == 1) {
            equipment = equipmentSet.iterator().next().toString();
        } else {
            equipment = "[EQUIPMENT]";
        }
    }

    public void calculateWorkoutTime() {
        Duration newWorkoutTime = Duration.ZERO;

        // Time spent for each set
        for (WorkoutExercise exercise : exerciseList) {
            // Time spent on exercises
            if (exercise instanceof RepsExercise) {
                newWorkoutTime = newWorkoutTime.plus(REP_TIME.multipliedBy(
                        (long) ((RepsExercise) exercise).getReps() *
                                (exercise).getSets()));
            }
            else if (exercise instanceof  DurationExercise) {
                newWorkoutTime = newWorkoutTime.plus(((DurationExercise) exercise).getDuration().multipliedBy(
                        (exercise).getSets()));
            }
            // Time spent resting during each set
            if (exercise.getSets() > 1) {
                newWorkoutTime = newWorkoutTime.plus(restBetweenExercises.multipliedBy((exercise.getSets() - 1)));
            }
        }
        // Time spent resting between sets
        if (exerciseList.size() > 1) {
            newWorkoutTime = newWorkoutTime.plus(restBetweenSets.multipliedBy(exerciseList.size() - 1));
        }

        // Time spent resting between rounds
        if (rounds > 1) {
            newWorkoutTime = newWorkoutTime.plus(restBetweenRounds.multipliedBy(rounds - 1));
        }

        // Set new workout time
        workoutTime = newWorkoutTime;
    }

    public Duration getRestBetweenExercises() {
        return restBetweenExercises;
    }

    public void setRestBetweenExercises(Duration restBetweenExercises) {
        this.restBetweenExercises = restBetweenExercises;
    }


    public void changeUnitsToMetric() {
        // If using imperial only
        if (usingImperial) {
            exerciseList.forEach(exercise -> {
                double weight = exercise.getLoad();
                weight /=  0.45359237;
                weight = Math.round(weight * 10.0) / 10.0;
                exercise.setLoad(weight);
            });
        }
        usingImperial = false;
    }

    public void changeUnitsToImperial() {
        // If not already using imperial
        if (!usingImperial) {
            exerciseList.forEach(exercise -> {
                double weight = exercise.getLoad();
                weight *=  0.45359237;
                weight = Math.round(weight * 10.0) / 10.0;
                exercise.setLoad(weight);
            });
        }
        usingImperial = true;
    }

    public boolean usingImperial() {
        return usingImperial;
    }

    public boolean isRoundBased() {
        return rounds > 1;
    }

    // Add the list of exercises for as many rounds as there are
    private void addRounds() {
        // Set all sets in exercises to 1. No sets allowed with rounds
        exerciseList.forEach(ex -> ex.setSets(1));

        // Copy the list of exercises to repeat in next rounds
        ArrayList<WorkoutExercise> temp = new ArrayList<>();
        for(int i = 0; i < rounds; i++){
            temp.addAll(exerciseList);
        }
        exerciseList = temp;
    }

    public WorkoutDTO toWorkoutDTO() {
        WorkoutDTO workoutDTO = new WorkoutDTO();
        workoutDTO.setName(this.getName());
        workoutDTO.setKey(this.getKey());
        workoutDTO.setRounds(this.getRounds());
        workoutDTO.setDifficulty(this.getDifficulty());
        workoutDTO.setCategory(this.getCategory());
        workoutDTO.setEquipment(this.getEquipment());
        workoutDTO.setRestBetweenExercises(this.getRestBetweenExercises().toMillis());
        workoutDTO.setRestBetweenSets(this.getRestBetweenSets().toMillis());
        workoutDTO.setRestBetweenRounds(this.getRestBetweenRounds().toMillis());
        workoutDTO.setWorkoutTime(this.getWorkoutTime().toMillis());

        List<WorkoutExerciseDTO> exerciseDTOList = new ArrayList<>();
        for (WorkoutExercise exercise : this.getExerciseList()) {
            exerciseDTOList.add(exercise.toWorkoutExerciseDTO());
        }
        workoutDTO.setExerciseList(exerciseDTOList);

        return workoutDTO;
    }


    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null || getClass() != obj.getClass()) {
            return false;
        }
        Workout other = (Workout) obj;
        return rounds == other.rounds
                && difficulty == other.difficulty
                && name.equals(other.name)
                && (Objects.equals(key, other.key))
                && (Objects.equals(category, other.category))
                && (Objects.equals(equipment, other.equipment))
                && muscleGroups.equals(other.muscleGroups)
                && equipmentSet.equals(other.equipmentSet)
                && exerciseList.equals(other.exerciseList)
                && restBetweenExercises.equals(other.restBetweenExercises)
                && restBetweenSets.equals(other.restBetweenSets)
                && restBetweenRounds.equals(other.restBetweenRounds)
                && workoutTime.equals(other.workoutTime)
                && usingImperial == other.usingImperial;
    }

    public String getDetails() {
        return getDifficultyAsString() + " · " + getEquipment()
                + " · " + getCategory();
        // TODO: Complete
        //Difficulty · Equipment
    }

    public String getMinutesString() {
        return workoutTime.toMinutes() + " min";
    }

}