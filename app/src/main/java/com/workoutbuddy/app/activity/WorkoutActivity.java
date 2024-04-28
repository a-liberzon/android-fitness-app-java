package com.workoutbuddy.app.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.FragmentManager;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.workoutbuddy.app.R;
import com.workoutbuddy.app.fragment.EditWorkoutExerciseFragment;
import com.workoutbuddy.app.fragment.WorkoutFragment;
import com.workoutbuddy.app.model.Exercise;
import com.workoutbuddy.app.model.ThemeManager;
import com.workoutbuddy.app.model.Workout;

import java.util.Objects;

public class WorkoutActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private Workout workout;
    private Exercise exercise;
    private FirebaseFirestore db;
    private CollectionReference workoutsCollection;
    boolean viewingFriendsWorkouts;
    ThemeManager themeManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_workout);
        themeManager = new ThemeManager(getBaseContext());

        // Handle authentication
        mAuth = FirebaseAuth.getInstance();
        FirebaseUser firebaseUser = mAuth.getCurrentUser();
        db = FirebaseFirestore.getInstance();
        workoutsCollection = db.collection("workouts");

        // Retrieve the Workout object from the Intent
        workout = (Workout) getIntent().getSerializableExtra("selectedWorkout");
        viewingFriendsWorkouts = Objects.requireNonNull(getIntent().getExtras()).getBoolean("viewingFriendsWorkouts", false);
        exercise = (Exercise) getIntent().getSerializableExtra("selectedExercise");

        Toolbar toolbar = findViewById(R.id.toolbar);
        themeManager.setToolbarTheme(toolbar, R.drawable.arrow_back);
        setSupportActionBar(toolbar);

        // Customize the toolbar as needed
        if (getSupportActionBar() != null) {
            // Set the title
            getSupportActionBar().setTitle(workout.getName());
            // Enable the Up button for navigation
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        // Get the value of whether showing friends workouts or not
        viewingFriendsWorkouts = getIntent().getBooleanExtra("viewingFriendsWorkouts", false);

        Bundle args = new Bundle();
        args.putSerializable("workout", workout);
        args.putBoolean("viewingFriendsWorkouts", viewingFriendsWorkouts);

        FragmentManager fragmentManager = getSupportFragmentManager();

        if (exercise != null) {
            args.putSerializable("exercise", exercise);
            EditWorkoutExerciseFragment editWorkoutExerciseFragment = new EditWorkoutExerciseFragment();
            editWorkoutExerciseFragment.setArguments(args);
            fragmentManager.beginTransaction().replace(R.id.workoutFrameLayout, editWorkoutExerciseFragment).commit();

        }
        else {
            WorkoutFragment workoutFragment = new WorkoutFragment();
            workoutFragment.setArguments(args);
            fragmentManager.beginTransaction().replace(R.id.workoutFrameLayout, workoutFragment).commit();
        }
    }

    // Handler to go to workouts activities
    private void returnToChooseWorkoutActivity() {
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
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

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_workout, menu);
        return true;
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        ThemeManager themeManager1 = new ThemeManager(getBaseContext());
        themeManager1.configureOverFlowText(menu, R.id.menu_item1);
        themeManager1.configureOverFlowText(menu, R.id.menu_item2);
        return super.onPrepareOptionsMenu(menu);
    }
}
