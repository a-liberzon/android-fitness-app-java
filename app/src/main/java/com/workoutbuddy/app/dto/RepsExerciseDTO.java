package com.workoutbuddy.app.dto;

import com.workoutbuddy.app.model.Exercise;
import com.workoutbuddy.app.model.RepsExercise;
import com.workoutbuddy.app.model.WorkoutExercise;

import java.net.MalformedURLException;
import java.net.URL;

public class RepsExerciseDTO extends WorkoutExerciseDTO {

    private int reps;

    public void setReps(int reps) {
        this.reps = reps;
    }

    public int getReps() {
        return reps;
    }

    @Override
    public WorkoutExercise toWorkoutExercise() {
        Exercise exercise = toExercise();

        int sets = getSets();
        double load = getLoad();
        URL url = null;
        try {
            url = new URL(getUrl());
        } catch (MalformedURLException e) {
            // Handle the exception if the URL string is malformed
            //e.printStackTrace();
        }
        int reps = getReps();
        RepsExercise repsExercise = new RepsExercise(exercise, sets, load, url, reps);
        repsExercise.setKey(getKey());

        return repsExercise;
    }

}
