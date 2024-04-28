package com.workoutbuddy.app.activity;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.FragmentManager;

import com.workoutbuddy.app.R;
import com.workoutbuddy.app.fragment.ExercisesFragment;
import com.workoutbuddy.app.model.Workout;

public class SelectExerciseActivity extends AppCompatActivity {

    private Workout workout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_exercise);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        // Retrieve the Workout object from the Intent
        workout = (Workout) getIntent().getSerializableExtra("workout");

        // Customize the toolbar as needed
        if (getSupportActionBar() != null) {
            // Set the title
            getSupportActionBar().setTitle("Select Exercise");
            // Enable the Up button for navigation
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        // Pass workout to routine and workout
        ExercisesFragment exercisesFragment = new ExercisesFragment();
        Bundle args = new Bundle();
        args.putSerializable("workout", workout);
        exercisesFragment.setArguments(args);

        // Get the fragment manager, and begin the transaction, to start with the workout frame
        FragmentManager fragmentManager = getSupportFragmentManager();
        fragmentManager.beginTransaction().replace(R.id.frame_layout, exercisesFragment).commit();

    }


    /*
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            // Handle the Up button click here
            onBackPressed();
            return true;
        } else if (item.getItemId() == R.id.action_custom_exercise) {
            Intent intent = new Intent(this, CreateExerciseActivity.class);
            intent.putExtra("workout", workout);
            startActivity(intent);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_select_exercise, menu);
        return true;
    }*/
}