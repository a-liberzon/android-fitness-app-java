package com.workoutbuddy.app.activity;

import android.os.Bundle;
import android.view.MenuItem;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.FragmentManager;

import com.workoutbuddy.app.R;
import com.workoutbuddy.app.fragment.CustomExerciseFragment;
import com.workoutbuddy.app.model.Exercise;

public class CreateExerciseActivity extends AppCompatActivity {

    //private Workout workout;
    private Exercise exercise;
    //private boolean addingToWorkout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_exercise);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        // Retrieve the Workout object from the Intent
        //workout = (Workout) getIntent().getSerializableExtra("workout");
        exercise = (Exercise) getIntent().getSerializableExtra("exercise");
        //addingToWorkout = workout != null;

        // Customize the toolbar as needed
        if (getSupportActionBar() != null) {
            // Set the title
            if (exercise == null) {
                getSupportActionBar().setTitle("Create Exercise");
            } else {
                getSupportActionBar().setTitle("Edit Exercise");
            }
            // Enable the Up button for navigation
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        CustomExerciseFragment customExerciseFragment = new CustomExerciseFragment();

        // Pass workout to fragment
        if (exercise != null) {
            Bundle args = new Bundle();
            args.putSerializable("exercise", exercise);
            customExerciseFragment.setArguments(args);
        }

        // Get the fragment manager, and begin the transaction, to start with the workout frame
        FragmentManager fragmentManager = getSupportFragmentManager();
        fragmentManager.beginTransaction().replace(R.id.frame_layout, customExerciseFragment).commit();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            // Handle the Up button click here
            onBackPressed();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}