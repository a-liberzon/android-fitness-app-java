package com.workoutbuddy.app.model;

import android.content.Context;

import java.util.List;
import java.util.Optional;

// Class to control flow of a workout
public class WorkoutController {

    private Workout workout;
    private WorkoutExercise currentExercise;
    private int currentSet;
    private List<WorkoutExercise> exerciseList;
    private int currentExerciseIndex;
    private int listSize;


    // Builder
    public WorkoutController(Workout workout) {
        this.workout = workout;
        exerciseList = workout.getExerciseList();
        currentSet = 1;
        currentExerciseIndex = 0;
        currentExercise = exerciseList.get(0);
        listSize = exerciseList.size();
    }

    // Returns the next exercise or set to perform
    public Optional<WorkoutExercise> getNextExerciseOrSetAndMove() {
        int lastSet = currentExercise.getSets();

        // If no more sets: Get next exercise, if any
        if (currentSet == lastSet) {
            // If currently on the last exercise return empty
            if (listSize == currentExerciseIndex + 1) {
                return Optional.empty();
            }
            // Else return the next exercise
            else {
                updateCurrents(true);
                return Optional.of(currentExercise);
            }
        }
        // Else, there are more sets: Just move to the next set and return the same exercise
        else {
            currentSet++;
            return Optional.of(currentExercise);
        }
    }

    /**
     * Returns the previous exercise or set to perform
     */
    public Optional<WorkoutExercise> getPrevExerciseOrSetAndMove() {
        // If this is the first set of the exercise, go back to previous exercise
        if (currentSet == 1) {
            // If currently on the first exercise, return empty
            if (currentExerciseIndex == 0) {
                return Optional.empty();
            }
            // Else return the previous exercise
            else {
                updateCurrents(false);
                return Optional.of(currentExercise);
            }
        }
        // Else return the same exercise, but update the current set
        else {
            currentSet--;
            return Optional.of(currentExercise);
        }
    }

    // Method increases/decreases the current exercise index, sets the current exercise, and
    // update the current set.
    private void updateCurrents(boolean increase) {
        if (increase) {
            currentExerciseIndex++;
            currentExercise = exerciseList.get(currentExerciseIndex);
            currentSet = 1;
        } else {
            currentExerciseIndex--;
            currentExercise = exerciseList.get(currentExerciseIndex);
            // If moving back, then set the current set to the last set of previous exercise
            currentSet = currentExercise.getSets();
        }
    }

    public WorkoutExercise getCurrentExercise() {
        return currentExercise;
    }

    public int getCurrentSet() {
        return currentSet;
    }

    /**
     * Method to check if exercise is currently on its last set
     */
    public boolean currentlyOnLastSet() {
        return currentExercise.getSets() == currentSet;
    }

    /**
     * Method to check if current exercise is the last exercise and the last set
     */
    public boolean isLastExerciseAndSet() {
        return (listSize == currentExerciseIndex + 1) && currentlyOnLastSet();
    }

    /**
     * Method to get the next exercise after this, if any. Notice method does not move the
     * current exercise. It just gets the next.
     *
     * @return The next exercise after the current one, or empty if this is the last.
     */
    public Optional<WorkoutExercise> getExerciseAfterThis() {
        if (currentExerciseIndex == listSize - 1)
            return Optional.empty();
        else
            return Optional.of(exerciseList.get(currentExerciseIndex + 1));
    }

    /**
     * Method checks if current exercise is the first of the workout and the first set of it
     */
    public boolean isFirstExerciseAndSet() {
        return (currentExerciseIndex == 0) && (currentSet == 1);
    }

    public int getListSize() {
        return listSize;
    }

    /**
     * Method to get the set information.
     *
     * @return the formatted string with the sets information.
     */
    public String getExerciseSetsText() {
        String curSet = String.valueOf(currentSet);
        String numSets = String.valueOf(currentExercise.getSets());
        return ("Set " + curSet + "/" + numSets);
    }

    /**
     * Method to know if current exercise is right before another round.
     *
     * @param offSet Number to add/subtract to current exercise index. Useful if asking after
     *               or before the exercise was switched.
     * @return True if this is last exercise of round and another round is coming.
     */
    //TODO: Enforce no sets in rounds
    public boolean isNewRoundComing(int offSet) {
        if (workout.isRoundBased()) {
            int rounds = workout.getRounds();
            int roundSize = listSize / rounds;
            if ((currentExerciseIndex + 1 + offSet) % roundSize == 0) {
                return true && !(listSize == currentExerciseIndex + 1);
            }

        }
        return false;
    }

    /**
     * Method calculates the rest time depending if rest is for exercise or round.
     *
     * @return the amount of time in seconds to rest.
     */
    // TODO: Maybe add different time for sets too?
    public long getRestTime() {
        return isNewRoundComing(-1) ? workout.getRestBetweenRounds().getSeconds() : workout.getRestBetweenExercises().getSeconds();
    }

    /**
     * Method returns next round number.
     *
     * @return next round's number.
     */
    public int getNextRoundNum() {
        int rounds = workout.getRounds();
        int roundSize = listSize / rounds;
        return currentExerciseIndex / roundSize + 2;
    }

    /**
     * Method to determine the next exercise text.
     *
     * @param context The context to use if an error needs to be shown.
     * @return The text to set on the GUI.
     */
    public String getNextExerciseText(Context context) {
        // If this is the last exercise and set
        if (isLastExerciseAndSet())
            return "Last exercise!";

            // If this is the last set, move on to next exercise
        else if (currentlyOnLastSet()) {
            Optional<WorkoutExercise> nextExerciseOp = getExerciseAfterThis();
            if (nextExerciseOp.isPresent()) {
                // If it is also, the last exercise of the round, if applicable, add round number
                return isNewRoundComing(0) ? "Next: Round " +
                        getNextRoundNum() + ", " + nextExerciseOp.get().getName() :
                        "Next: " + nextExerciseOp.get().getName();
            } else {
                // TODO: Handle this better
                return "Error!";
            }
        }
        // Else, this is not the last set, so do not show anything
        else
            return "";
    }

    // Method to know if current exercise is a duration exercise
    public boolean onDurationExercise () {
        return currentExercise instanceof  DurationExercise;
    }
}
