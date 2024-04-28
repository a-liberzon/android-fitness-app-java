package com.workoutbuddy.app.model;

import com.workoutbuddy.app.R;

import java.util.List;

/**
 * Class to hold friends information
 */
public class Friend {

    private String username;
    private int numWorkouts;
    private int numFollowers;

    private List<Workout> workoutList;
    private boolean pinned = false;
    private int imageResourceId;

    public Friend(String username, int numWorkouts, int numFollowers, List<Workout> workoutList, int imageResourceId) {
        this.username = username;
        this.numWorkouts = numWorkouts;
        this.numFollowers = numFollowers;
        this.workoutList = workoutList;
        this.imageResourceId = imageResourceId != 0 ? imageResourceId : R.drawable.icon_friend;
    }

    public String getUsername() {
        return username;
    }

    public int getNumWorkouts() {
        return numWorkouts;
    }

    public int getNumFollowers() {
        return numFollowers;
    }

    public List<Workout> getWorkoutList() {
        return workoutList;
    }

    public boolean isPinned() {
        return pinned;
    }

    public void setPinned(boolean pinned) {
        this.pinned = pinned;
    }

    public int getImageResourceId() {
        return imageResourceId;
    }

    public void setImageResourceId(int imageResourceId) {
        this.imageResourceId = imageResourceId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Friend)) return false;
        Friend friend = (Friend) o;
        return username.equals(friend.username);
    }

}


