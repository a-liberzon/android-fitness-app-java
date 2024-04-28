package com.workoutbuddy.app.model;

import com.workoutbuddy.app.dto.RepsExerciseDTO;
import com.workoutbuddy.app.dto.WorkoutExerciseDTO;

import java.net.URL;

public class RepsExercise extends WorkoutExercise {

    private int reps;

    public RepsExercise(Exercise exercise, int sets, double load, URL url, int reps) {
        super(exercise, sets, load, url);
        this.reps = reps;
    }

    public void setReps(int reps) {
        this.reps = reps;
    }

    public int getReps() {
        return reps;
    }

    @Override
    public WorkoutExerciseDTO toWorkoutExerciseDTO() {
        String url = getUrl() != null ? getUrl().toString() : null;
        return new WorkoutExerciseDTO(toExerciseDTO(), getSets(), getLoad(), url, reps);
    }


    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null || getClass() != obj.getClass()) {
            return false;
        }
        if (!super.equals(obj)) {
            return false;
        }
        RepsExercise other = (RepsExercise) obj;
        return reps == other.reps;
    }

}
