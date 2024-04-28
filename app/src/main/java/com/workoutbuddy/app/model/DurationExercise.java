package com.workoutbuddy.app.model;

import com.workoutbuddy.app.dto.DurationExerciseDTO;
import com.workoutbuddy.app.dto.WorkoutExerciseDTO;

import java.net.URL;
import java.time.Duration;

public class DurationExercise extends WorkoutExercise {

    private Duration duration;

    public DurationExercise(Exercise exercise, int sets, double load, URL url, Duration duration) {
        super(exercise, sets, load, url);
        this.duration = duration;
    }

    public Duration getDuration() {
        return duration;
    }

    public void setDuration(Duration duration) {
        this.duration = duration;
    }

    @Override
    public WorkoutExerciseDTO toWorkoutExerciseDTO() {
        String url = getUrl() != null ? getUrl().toString() : null;
        return new WorkoutExerciseDTO(toExerciseDTO(), getSets(), getLoad(), url, getDuration().toMillis());
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
        DurationExercise other = (DurationExercise) obj;
        return duration.equals(other.duration);
    }

}
