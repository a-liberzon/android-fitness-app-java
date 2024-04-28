package com.workoutbuddy.app.dto;

import com.workoutbuddy.app.model.DurationExercise;
import com.workoutbuddy.app.model.Exercise;
import com.workoutbuddy.app.model.WorkoutExercise;

import java.net.MalformedURLException;
import java.net.URL;
import java.time.Duration;

public class DurationExerciseDTO extends WorkoutExerciseDTO {

    long durationMillis;

    public DurationExerciseDTO() {
        // Default constructor required for Firebase deserialization
    }


    public long getDuration() {
        return durationMillis;
    }

    public void setDuration(long durationMillis) {
        this.durationMillis = durationMillis;
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
        Duration duration = Duration.ofMillis(durationMillis);
        DurationExercise durationExercise = new DurationExercise(exercise, sets, load, url, duration);
        durationExercise.setKey(getKey());

        return durationExercise;
    }
}
