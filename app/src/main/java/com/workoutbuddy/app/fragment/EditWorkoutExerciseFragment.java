package com.workoutbuddy.app.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.google.android.material.button.MaterialButtonToggleGroup;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.workoutbuddy.app.R;
import com.workoutbuddy.app.model.DurationExercise;
import com.workoutbuddy.app.model.Exercise;
import com.workoutbuddy.app.model.RepsExercise;
import com.workoutbuddy.app.model.Workout;

import java.net.MalformedURLException;
import java.net.URL;
import java.time.Duration;

public class EditWorkoutExerciseFragment extends Fragment {

    private LinearLayout contentLayout;
    private LayoutInflater inflater;
    private DatabaseReference userWorkoutsRef;

    private Workout workout;
    private Exercise exercise;


    // xml items
    private EditText setsText;
    private EditText repsText;
    private EditText durationText;
    private EditText loadText;
    private EditText urlText;
    // TODO: Implement feature
    private WebView webView;
    private Button saveBtn;

    private boolean showingReps;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_edit_workout_exercise, container, false);

        this.inflater = inflater;

        workout = (Workout) getArguments().getSerializable("workout");

        String userId = FirebaseAuth.getInstance().getUid();
        userWorkoutsRef = FirebaseDatabase.getInstance().getReference().child("users").child(userId).child("workouts");


        Bundle arguments = getArguments();
        if (arguments != null && arguments.containsKey("exercise")) {
            exercise = (Exercise) arguments.getSerializable("exercise");
        }

        setsText = view.findViewById(R.id.edit_text_sets);
        loadText = view.findViewById(R.id.text_load);
        urlText = view.findViewById(R.id.text_exercise_url);


        // Initialize your views
        contentLayout = view.findViewById(R.id.contentLayout);

        MaterialButtonToggleGroup toggleButton = view.findViewById(R.id.toggleButton);

        toggleButton.addOnButtonCheckedListener((group, checkedId, isChecked) -> {
            if (isChecked) {
                // Check which button is selected
                if (checkedId == R.id.reps_toggle_btn) {
                    // Replace content with Reps layout
                    showRepsContent();
                } else if (checkedId == R.id.duration_toggle_btn) {
                    // Replace content with Duration layout
                    showDurationContent();
                }
            }
        });

        // Make sure one of the buttons is initially selected
        toggleButton.check(R.id.reps_toggle_btn);

        // Inflate and show the initial content (Reps layout) when the fragment is first created
        showRepsContent();

        saveBtn = view.findViewById(R.id.save_btn);

        // TODO: Change theme
        saveBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int sets = Integer.parseInt(setsText.getText().toString());

                double load = Integer.parseInt(loadText.getText().toString());

                URL url = null;
                if (!urlText.getText().toString().isEmpty()) {
                    try {
                        url = new URL(urlText.getText().toString());
                    } catch (MalformedURLException e) {
                        Toast.makeText(getContext(), "Invalid URL: " + urlText.getText().toString(),
                                Toast.LENGTH_SHORT).show();
                    }
                }

                if (showingReps) {
                    int reps = Integer.parseInt(repsText.getText().toString());
                    RepsExercise repsExercise = new RepsExercise(exercise, sets, load, url, reps);
                    workout.addExercise(repsExercise);
                } else {
                    Duration duration = Duration.ofSeconds(Integer.parseInt(durationText.getText().toString()));
                    DurationExercise durationExercise = new DurationExercise(exercise, sets, load, url, duration);
                    workout.addExercise(durationExercise);
                }

                //update workout
                userWorkoutsRef.child(workout.getKey()).setValue(workout.toWorkoutDTO());

                returnToWorkoutFragment();
            }
        });

        return view;
    }

    private void showRepsContent() {
        // Inflate and show the Reps content layout
        contentLayout.removeAllViews(); // Remove any existing views
        View repsContentView = inflater.inflate(R.layout.content_layout_reps, contentLayout, false);
        contentLayout.addView(repsContentView); // Add the new content layout
        repsText = repsContentView.findViewById(R.id.edit_text_reps);
        showingReps = true;
    }

    private void showDurationContent() {
        // Inflate and show the Duration content layout
        contentLayout.removeAllViews(); // Remove any existing views
        View durationContentView = inflater.inflate(R.layout.content_layout_duration, contentLayout, false);
        contentLayout.addView(durationContentView);
        durationText = durationContentView.findViewById(R.id.edit_text_duration);
        showingReps = false;
    }

    private void returnToWorkoutFragment() {
        WorkoutFragment workoutFragment = new WorkoutFragment();

        // Pass workout to the WorkoutFragment
        Bundle args = new Bundle();
        args.putSerializable("workout", workout);
        workoutFragment.setArguments(args);

        // Replace the current fragment with the WorkoutFragment
        FragmentTransaction transaction = requireActivity().getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.workoutFrameLayout, workoutFragment);
        transaction.commit();
    }

}
