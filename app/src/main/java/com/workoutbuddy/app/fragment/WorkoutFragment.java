package com.workoutbuddy.app.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.workoutbuddy.app.R;
import com.workoutbuddy.app.activity.SelectExerciseActivity;
import com.workoutbuddy.app.adapter.WorkoutExercisesAdapter;
import com.workoutbuddy.app.model.ThemeManager;
import com.workoutbuddy.app.model.Utilities;
import com.workoutbuddy.app.model.Workout;
import com.workoutbuddy.app.model.WorkoutExercise;


public class WorkoutFragment extends Fragment implements WorkoutExercisesAdapter.ExerciseItemClickListener {

    private View view;
    private Workout workout;
    private Utilities utils;

    private FirebaseFirestore db;
    private CollectionReference workoutsCollection;

    private RecyclerView workoutRV;
    private WorkoutExercisesAdapter adapter;

    private DatabaseReference workoutRef;
    private ThemeManager themeManager;

    private boolean viewingFriendsWorkouts;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_workout, container, false);
        workoutRV = view.findViewById(R.id.workout_recycler_view);
        themeManager = new ThemeManager(getContext());

        // Create utilities object
        utils = new Utilities(view.getContext());

        // Handle start button, by switching to routine fragment
        setUpStartButton();
        setUpAddButton();

        // Retrieve the Workout object from the Intent
        workout = (Workout) getArguments().getSerializable("workout");
        viewingFriendsWorkouts = getArguments().getBoolean("viewingFriendsWorkouts");

        if (viewingFriendsWorkouts) {
            //workoutRef = getWorkoutRef(workout);
        }
        else {
            workoutRef = getWorkoutRef(workout);
        }

        buildRecyclerMenu();
        //displayExercises(workout);

        // Return the inflated view
        return view;
    }

    // Handler to swap this fragment with the routine fragment
    public void setUpStartButton() {
        // Set button color depending on night mode setting
        FloatingActionButton startImageButton = view.findViewById(R.id.play_fab);

        // Color start workout button according to night/day theme
        themeManager.setFloatingButtonTheme(startImageButton, R.drawable.play_arrow);
        startImageButton.setOnClickListener(v -> {

            RoutineFragment routineFragment = new RoutineFragment();
            Bundle args = new Bundle();
            args.putSerializable("workout", workout);
            routineFragment.setArguments(args);

            requireActivity().getSupportFragmentManager().beginTransaction()
                    .replace(R.id.workoutFrameLayout, routineFragment)
                    .addToBackStack(null).commit();
        });
    }

    private void setUpAddButton() {
        FloatingActionButton addWorkoutBtn = view.findViewById(R.id.add_fab);

        // If on a friend's workout, add button saves it to users workouts, update image and save it
        if (viewingFriendsWorkouts) {
            // Set icon and color according to night/day theme
            themeManager.setFloatingButtonTheme(addWorkoutBtn, R.drawable.ic_save);

            //utils.setFloatingBtnColorAuto(getContext(), addWorkoutBtn, getResources());
            addWorkoutBtn.setOnClickListener(view -> {
                // TODO: Save current workout on user's database here
                Toast.makeText(getContext(), "TODO: make this save current workout", Toast.LENGTH_SHORT).show();
                //Toast.makeText(getContext(), "Saved workout", Toast.LENGTH_SHORT).show();
            });
        }
        // Else, the add button allows adding a new routine
        else {
            // Set icon and color according to night/day theme
            themeManager.setFloatingButtonTheme(addWorkoutBtn, R.drawable.plus_button);
            addWorkoutBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    // Start the SelectExerciseActivity
                    Intent intent = new Intent(getActivity(), SelectExerciseActivity.class);
                    intent.putExtra("workout", workout);
                    startActivity(intent);
                }
            });
        }
    }

    // TODO: static method may need to move?
    public DatabaseReference getWorkoutRef(Workout workout) {
        String userId = FirebaseAuth.getInstance().getUid();
        String workoutKey = workout.getKey();
        return FirebaseDatabase.getInstance().getReference().child("users")
                .child(userId).child("workouts").child(workoutKey);
    }

    private void buildRecyclerMenu() {
        adapter = new WorkoutExercisesAdapter(workout.getExerciseList(), this);
        workoutRV.setAdapter(adapter);
    }

    @Override
    public void onEditExerciseClicked(WorkoutExercise exercise) {

    }

    @Override
    public void onDeleteExerciseClicked(WorkoutExercise exercise) {

    }

    @Override
    public void onExerciseClicked(WorkoutExercise exercise) {

    }
}