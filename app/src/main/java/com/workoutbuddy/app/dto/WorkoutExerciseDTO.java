package com.workoutbuddy.app.dto;

import com.workoutbuddy.app.model.DurationExercise;
import com.workoutbuddy.app.model.Exercise;
import com.workoutbuddy.app.model.RepsExercise;
import com.workoutbuddy.app.model.WorkoutExercise;

import java.net.MalformedURLException;
import java.net.URL;
import java.time.Duration;

public class WorkoutExerciseDTO extends ExerciseDTO {
    private int sets;
    private double load;
    private String url;
    private int reps;
    long durationMillis;
    boolean isRepBased;

    public WorkoutExerciseDTO() {
        // Default constructor required for Firebase deserialization
    }


    // For reps
    public WorkoutExerciseDTO(ExerciseDTO exercise, int sets, double load, String url, int reps) {
        super(exercise);
        this.sets = sets;
        this.load = load;
        this.url = url;
        this.reps = reps;
        isRepBased = true;
    }

    // For durations
    public WorkoutExerciseDTO(ExerciseDTO exercise, int sets, double load, String url, long duration) {
        super(exercise);
        this.sets = sets;
        this.load = load;
        this.url = url;
        durationMillis = duration;
        isRepBased = false;
    }

    // Getters and setters

    public int getSets() {
        return sets;
    }

    public void setSets(int sets) {
        this.sets = sets;
    }

    public double getLoad() {
        return load;
    }

    public void setLoad(double load) {
        this.load = load;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public WorkoutExercise toWorkoutExercise() {
        Exercise exercise = toExercise();

        URL url = null;
        try {
            url = new URL(getUrl());
        } catch (MalformedURLException e) {
            // Handle the exception if the URL string is malformed
            //e.printStackTrace();
        }

        if (reps != 0) {
            RepsExercise repsExercise = new RepsExercise(exercise, sets, load, url, reps);
            repsExercise.setKey(getKey());

            return repsExercise;
        } else {
            Duration duration = Duration.ofMillis(durationMillis);
            DurationExercise durationExercise = new DurationExercise(exercise, sets, load, url, duration);
            durationExercise.setKey(getKey());

            return durationExercise;
        }
    }

    public int getReps() {
        return reps;
    }

    public void setReps(int reps) {
        this.reps = reps;
    }

    public long getDuration() {
        return durationMillis;
    }

    public void setDuration(long durationMillis) {
        this.durationMillis = durationMillis;
    }
}
