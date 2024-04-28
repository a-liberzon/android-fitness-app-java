package com.workoutbuddy.app.fragment;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.SearchView;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.workoutbuddy.app.R;
import com.workoutbuddy.app.activity.WorkoutActivity;
import com.workoutbuddy.app.adapter.WorkoutSearchAdapter;
import com.workoutbuddy.app.databinding.FragmentWorkoutsBinding;
import com.workoutbuddy.app.dto.WorkoutDTO;
import com.workoutbuddy.app.model.ThemeManager;
import com.workoutbuddy.app.model.Utilities;
import com.workoutbuddy.app.model.Workout;

import java.util.ArrayList;
import java.util.List;

public class WorkoutsFragment extends Fragment implements WorkoutSearchAdapter.WorkoutItemClickListener {

    private FragmentWorkoutsBinding binding;
    private RecyclerView workoutsRV;
    private WorkoutSearchAdapter adapter;
    private String userId;
    private View view;
    private Utilities utils;
    private DatabaseReference userWorkoutsRef;
    private List<Workout> workouts;
    boolean viewingFriendsWorkouts;
    ThemeManager themeManager;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate view
        binding = FragmentWorkoutsBinding.inflate(inflater, container, false);
        View view = binding.getRoot();
        utils = new Utilities(getContext());
        workoutsRV = view.findViewById(R.id.workouts_recycler_view);
        SearchView searchView = view.findViewById(R.id.search_bar);

        userId = FirebaseAuth.getInstance().getUid();
        userWorkoutsRef = FirebaseDatabase.getInstance().getReference().child("users").child(userId).child("workouts");

        // Retrieve the Workout object from the Intent
        Bundle arguments = getArguments();
        if (arguments != null && arguments.containsKey("workouts")) {
            workouts = (List<Workout>) arguments.getSerializable("workouts");
            viewingFriendsWorkouts = true;
        } else {
           viewingFriendsWorkouts = false;
            // Initialize workouts list
            workouts = new ArrayList<>();

            // Fetch workouts from the database
            fetchWorkoutsFromDB();
        }

        // Build the recycler view
        buildRecyclerMenu();

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                // inside on query text change method we are
                // calling a method to filter our recycler view.
                filterWorkouts(newText);
                return false;
            }
        });

        themeManager = new ThemeManager(getContext());
        themeManager.setFloatingButtonTheme(binding.addWorkout, R.drawable.plus_button);

        if (viewingFriendsWorkouts) {
            binding.addWorkout.setVisibility(View.GONE);
        }

        binding.addWorkout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showInputDialog();
            }
        });

        return view;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    private void showInputDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext(), R.style.ThemeOverlayAlertDialog);
        builder.setTitle("New Workout");

        // Inflate the dialog layout
        View dialogView = getLayoutInflater().inflate(R.layout.dialog_add_workout, null);
        builder.setView(dialogView);

        EditText nameInput = dialogView.findViewById(R.id.input_workout_name);
        EditText numRoundsInput = dialogView.findViewById(R.id.input_num_rounds);

        // Set up the buttons
        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String workoutName = nameInput.getText().toString();
                int numRounds = Integer.parseInt(numRoundsInput.getText().toString());

                // Generate a unique key for the new workout
                String workoutKey = userWorkoutsRef.push().getKey();
                Workout workout = new Workout(workoutKey ,workoutName, numRounds);

                // Set the new workout object under the generated key
                userWorkoutsRef.child(workoutKey).setValue(workout.toWorkoutDTO());

                // Start WorkoutActivity passing the selected Workout object
                Intent intent = new Intent(getContext(), WorkoutActivity.class);
                intent.putExtra("selectedWorkout", workout);
                // Pass the generated key if needed
                intent.putExtra("selectedWorkoutKey", workoutKey);

                Toast.makeText(getContext(), "Workout Name:" + workoutName, Toast.LENGTH_SHORT).show();

                startActivity(intent);
            }
        });
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });

        // Display the dialog
        builder.show();
    }

    private void fetchWorkoutsFromDB() {
        userWorkoutsRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                workouts.clear(); // Clear the existing workouts in case of updates

                for (DataSnapshot workoutSnapshot : snapshot.getChildren()) {
                    WorkoutDTO workoutDTO = workoutSnapshot.getValue(WorkoutDTO.class);
                    Workout workout = workoutDTO.toWorkout();
                    workout.setKey(workoutSnapshot.getKey());
                    workouts.add(workout);
                }

                // Update the adapter with the new workout list
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // Handle the error
                Toast.makeText(getContext(), "The read failed: " + databaseError.getCode(), Toast.LENGTH_LONG).show();
            }
        });
    }

    private void buildRecyclerMenu() {
        adapter = new WorkoutSearchAdapter(workouts, this);
        workoutsRV.setAdapter(adapter);
    }

    private void filterWorkouts(String query) {
        if (query.isEmpty()) {
            // If the search query is empty, display all workouts without filtering
            adapter.filterList(workouts);
        } else {
            List<Workout> filteredList = new ArrayList<>();

            // Iterate through all exercises and check if the exercise name contains the query
            for (Workout workout : workouts) {
                if (workout.getName().toLowerCase().contains(query.toLowerCase())) {
                    filteredList.add(workout);
                }
            }

            // Update the adapter with the filtered list
            adapter.filterList(filteredList);
        }
    }


    @Override
    public void onEditWorkoutClicked(Workout workout) {
        Toast.makeText(getContext(), "edit", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onDeleteWorkoutClicked(Workout workout) {
        if (!viewingFriendsWorkouts) {

            String workoutID = workout.getKey();
            DatabaseReference workoutRef = userWorkoutsRef.child(workoutID);
            workoutRef.removeValue()
                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            Toast.makeText(getContext(), "Workout deleted successfully", Toast.LENGTH_SHORT).show();
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(getContext(), "Failed to delete workout: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    });
        }

    }

    @Override
    public void onWorkoutClicked(Workout workout) {
        Intent intent = new Intent(getContext(), WorkoutActivity.class);
        intent.putExtra("selectedWorkout", workout);
        intent.putExtra("viewingFriendsWorkouts", viewingFriendsWorkouts);
        startActivity(intent);
    }
}