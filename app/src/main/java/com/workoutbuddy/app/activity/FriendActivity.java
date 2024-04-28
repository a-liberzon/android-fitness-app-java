package com.workoutbuddy.app.activity;

import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.FragmentManager;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.workoutbuddy.app.R;
import com.workoutbuddy.app.fragment.WorkoutsFragment;
import com.workoutbuddy.app.model.Utilities;
import com.workoutbuddy.app.model.Workout;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class FriendActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private List<Workout> friendsWorkouts;
    private FirebaseFirestore db;
    private CollectionReference workoutsCollection;
    private Utilities utils;
    private ArrayList<Workout> friendWorkouts;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_friend);
        utils  = new Utilities(getBaseContext());

        // Get friends workout
        friendsWorkouts = Utilities.getDummyWorkouts();

        // Set up the toolbar
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        Bundle bundle = getIntent().getExtras();
        if (getSupportActionBar() != null && bundle != null && bundle.containsKey("friend")) {
            String username = bundle.getString("friend");
            getSupportActionBar().setTitle(username + "'s workouts");
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        Bundle args = new Bundle();
        args.putSerializable("workouts", (Serializable) friendsWorkouts);

        WorkoutsFragment workoutsFragment = new WorkoutsFragment();
        workoutsFragment.setArguments(args);

        FragmentManager fragmentManager = getSupportFragmentManager();
        fragmentManager.beginTransaction().replace(R.id.workouts_frame_layout, workoutsFragment).commit();
    }


    // TODO: Keep?
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            // Handle the Up button click here
            onBackPressed();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    // TODO: Keep?
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_workout, menu);
        return true;
    }
}
