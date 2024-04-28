package com.workoutbuddy.app.dto;

import com.workoutbuddy.app.model.Workout;
import com.workoutbuddy.app.model.WorkoutExercise;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class WorkoutDTO {
    private String name;
    private String key;
    private int rounds;
    private int difficulty;
    private String category;
    private String equipment;
    private List<WorkoutExerciseDTO> exerciseList;

    // In milliseconds
    private long restBetweenExercises;
    private long restBetweenSets;
    private long restBetweenRounds;
    private long workoutTime;

    public WorkoutDTO() {
        exerciseList = new ArrayList<>();
    }

    public WorkoutDTO(String name, String key, int rounds, int difficulty, String category,
                      String equipment, List<WorkoutExerciseDTO> exerciseList,
                      long restBetweenExercises,
                      long restBetweenSets, long restBetweenRounds, long workoutTime) {
        this.key = key;
        this.name = name;
        this.rounds = rounds;
        this.difficulty = difficulty;
        this.category = category;
        this.equipment = equipment;
        this.exerciseList = exerciseList;
        this.restBetweenExercises = restBetweenExercises;
        this.restBetweenSets = restBetweenSets;
        this.restBetweenRounds = restBetweenRounds;
        this.workoutTime = workoutTime;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
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

    public void setDifficulty(int difficulty) {
        this.difficulty = difficulty;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getEquipment() {
        return equipment;
    }

    public void setEquipment(String equipment) {
        this.equipment = equipment;
    }

    public List<WorkoutExerciseDTO> getExerciseList() {
        return exerciseList;
    }

    public void setExerciseList(List<WorkoutExerciseDTO> exerciseList) {
        this.exerciseList = exerciseList;
    }

    public long getRestBetweenExercises() {
        return restBetweenExercises;
    }

    public void setRestBetweenExercises(long restBetweenExercises) {
        this.restBetweenExercises = restBetweenExercises;
    }

    public long getRestBetweenSets() {
        return restBetweenSets;
    }

    public void setRestBetweenSets(long restBetweenSets) {
        this.restBetweenSets = restBetweenSets;
    }

    public long getRestBetweenRounds() {
        return restBetweenRounds;
    }

    public void setRestBetweenRounds(long restBetweenRounds) {
        this.restBetweenRounds = restBetweenRounds;
    }

    public long getWorkoutTime() {
        return workoutTime;
    }

    public void setWorkoutTime(long workoutTime) {
        this.workoutTime = workoutTime;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public Workout toWorkout() {
        Workout workout = new Workout();
        workout.setName(this.getName());
        workout.setRounds(this.getRounds());
        workout.setDifficulty(this.getDifficulty());
        workout.setCategory(this.getCategory());
        workout.setEquipment(this.getEquipment());
        workout.setRestBetweenExercises(Duration.ofMillis(this.getRestBetweenExercises()));
        workout.setRestBetweenSets(Duration.ofMillis(this.getRestBetweenSets()));
        workout.setRestBetweenRounds(Duration.ofMillis(this.getRestBetweenRounds()));
        workout.setWorkoutTime(Duration.ofMillis(this.getWorkoutTime()));

        for (WorkoutExerciseDTO workoutExerciseDTO : this.getExerciseList()) {
            WorkoutExercise exercise = workoutExerciseDTO.toWorkoutExercise();
            workout.addExercise(exercise);
        }

        return workout;
    }
}
