package com.workoutbuddy.app.model;

import com.google.firebase.auth.FirebaseUser;

import java.util.Map;

public class User {
    private String email;
    private String password;
    private String name;
    private String uID;
    private Map<String, Workout> workouts;

    public User(FirebaseUser firebaseUser) {
        email = firebaseUser.getEmail();
        name = firebaseUser.getDisplayName();
        uID = firebaseUser.getUid();
    }

    public User(String email, String password, String name) {
        this.email = email;
        this.password = password;
        this.name = name;
    }

    public User(String email, String password) {
        this.email = email;
        this.password = password;
    }

    public User() {
    }

    public Map<String, Workout> getWorkouts() {
        return workouts;
    }

    public void setWorkouts(Map<String, Workout> workouts) {
        this.workouts = workouts;
    }

    public void addWorkout(Workout workout) {
        workouts.put(workout.getName(), workout);
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}