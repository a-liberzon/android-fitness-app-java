package com.workoutbuddy.app.model;

import com.workoutbuddy.app.dto.WorkoutExerciseDTO;

import java.net.URL;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public abstract class WorkoutExercise extends Exercise {

    private int sets;
    private double load;
    private URL url;

    public WorkoutExercise(Exercise exercise, int sets, double load, URL url) {
        super(exercise);
        this.sets = sets;
        this.load = load;
        this.url = url;
    }

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

    public URL getUrl() {
        return url;
    }

    public void setUrl(URL url) {
        this.url = url;
    }

    public abstract WorkoutExerciseDTO toWorkoutExerciseDTO();

    public String getVideoId() {
        if (url == null) {
            return null;
        }
        String fileId = url.getFile();
        if (fileId.length() > 1) {
            return fileId.substring(1);
        } else {
            return fileId; // If the fileId is just one character, return the same string.
        }
    }

}
