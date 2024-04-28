package com.workoutbuddy.app;

import static org.junit.Assert.assertEquals;

import com.workoutbuddy.app.dto.ExerciseDTO;
import com.workoutbuddy.app.dto.WorkoutDTO;
import com.workoutbuddy.app.model.DurationExercise;
import com.workoutbuddy.app.model.Exercise;
import com.workoutbuddy.app.model.MuscleGroup;
import com.workoutbuddy.app.model.RepsExercise;
import com.workoutbuddy.app.model.Workout;

import org.checkerframework.checker.units.qual.A;
import org.junit.Test;

import java.time.Duration;
import java.util.ArrayList;
import java.util.HashSet;

public class DataTransferObjectTest {

    @Test
    public void testExerciseDTO() {
        Exercise exercise1 = new Exercise(new Exercise(null, "Push-Ups",
                MuscleGroup.CHEST, new HashSet<>(), new HashSet<>(), 1));
        ExerciseDTO exerciseDTO1 = new ExerciseDTO(null, "Push-Ups",
                "Chest", new ArrayList<>(), new ArrayList<>(), 1);
        assertEquals(exercise1, exerciseDTO1.toExercise());
    }

    @Test
    public void testWorkoutDTO() {
        Exercise exercise1 = new Exercise(new Exercise(null, "Push-Ups",
                MuscleGroup.CHEST, new HashSet<>(), new HashSet<>(), 1));
        Exercise exercise2 = new Exercise(new Exercise(null, "Plank",
                MuscleGroup.ABS, new HashSet<>(), new HashSet<>(), 2));
        ExerciseDTO exerciseDTO1 = new ExerciseDTO(null, "Push-Ups",
                "Chest", new ArrayList<>(), new ArrayList<>(), 1);
        ExerciseDTO exerciseDTO2 = new ExerciseDTO(null, "Plank",
                "Abs", new ArrayList<>(), new ArrayList<>(), 2);

        // Create a workout
        Workout workout = new Workout("Workout 1", 3);
        workout.addExercise(new RepsExercise(exercise1, 3, 10.0, null, 10));
        workout.addExercise(new DurationExercise(exercise2, 2, 0.0, null, Duration.ofSeconds(30)));

        // Convert the workout to a workout DTO
        WorkoutDTO workoutDTO = workout.toWorkoutDTO();

        // Create a new workout from the workout DTO
        Workout newWorkout = workoutDTO.toWorkout();

        // Compare the original workout with the new workout
        assertEquals(workout, newWorkout);
    }

}
