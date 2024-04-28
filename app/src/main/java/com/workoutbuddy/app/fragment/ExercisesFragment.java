package com.workoutbuddy.app.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.SearchView;
import androidx.core.view.MenuProvider;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Lifecycle;
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
import com.workoutbuddy.app.activity.CreateExerciseActivity;
import com.workoutbuddy.app.activity.WorkoutActivity;
import com.workoutbuddy.app.adapter.ExerciseSearchAdapter;
import com.workoutbuddy.app.databinding.FragmentExercisesBinding;
import com.workoutbuddy.app.dto.ExerciseDTO;
import com.workoutbuddy.app.model.Exercise;
import com.workoutbuddy.app.model.Workout;

import java.util.ArrayList;
import java.util.List;

public class ExercisesFragment extends Fragment implements ExerciseSearchAdapter.ExerciseItemClickListener {

    private FragmentExercisesBinding binding;
    private RecyclerView exercisesRV;
    private ExerciseSearchAdapter adapter;
    private String userId;
    private DatabaseReference userExercisesRef;
    private List<Exercise> exercises;
    private View view;

    private boolean addingToWorkout;
    private Workout workout;

    public ExercisesFragment() {
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        view = inflater.inflate(R.layout.fragment_exercises, container, false);
        exercisesRV = view.findViewById(R.id.exercises_recycler_view);
        SearchView searchView = view.findViewById(R.id.search_bar);

        userId = FirebaseAuth.getInstance().getUid();
        userExercisesRef = FirebaseDatabase.getInstance().getReference().child("users").child(userId).child("exercises");

        // Retrieve the Workout object from the Intent
        Bundle arguments = getArguments();
        if (arguments != null && arguments.containsKey("workout")) {
            workout = (Workout) arguments.getSerializable("workout");
            addingToWorkout = true;
        } else {
            addingToWorkout = false;
        }

        // Initialize the exercises list
        exercises = new ArrayList<>();

        // Call a method to populate the exercises list
        //populateExercises();
        fetchExercisesFromDB();

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
                filterExercises(newText);
                return false;
            }
        });

        setToolbarMenu();

        return view;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    private void buildRecyclerMenu() {
        adapter = new ExerciseSearchAdapter(exercises, this);
        exercisesRV.setAdapter(adapter);
    }

    private void filterExercises(String query) {
        List<Exercise> filteredList = new ArrayList<>();

        // Iterate through all exercises and check if the exercise name contains the query
        for (Exercise exercise : exercises) {
            if (exercise.getName().toLowerCase().contains(query.toLowerCase())) {
                filteredList.add(exercise);
            }
        }

        // Update the adapter with the filtered list
        adapter.filterList(filteredList);
    }

    private void fetchExercisesFromDB() {
        userExercisesRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                exercises.clear();

                for (DataSnapshot exerciseSnapshot : snapshot.getChildren()) {
                    ExerciseDTO exerciseDTO = exerciseSnapshot.getValue(ExerciseDTO.class);
                    Exercise exercise = exerciseDTO.toExercise();
                    exercise.setKey(exerciseSnapshot.getKey());
                    exercises.add(exercise);
                }

                // Update the adapter with the new exercises list
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // Handle the error
                Toast.makeText(getContext(), "The read failed: " + databaseError.getCode(), Toast.LENGTH_LONG).show();
            }
        });
    }

    private void setToolbarMenu() {
        requireActivity().addMenuProvider(new MenuProvider() {
            @Override
            public void onCreateMenu(@NonNull Menu menu, @NonNull MenuInflater menuInflater) {
                menuInflater.inflate(R.menu.menu_select_exercise, menu);
            }

            @Override
            public boolean onMenuItemSelected(@NonNull MenuItem menuItem) {
                if (menuItem.getItemId() == android.R.id.home) {
                    // Handle the Up button click here
                    requireActivity().onBackPressed();
                    return true;
                } else if (menuItem.getItemId() == R.id.action_custom_exercise) {
                    Intent intent = new Intent(getContext(), CreateExerciseActivity.class);
                    intent.putExtra("workout", workout);
                    startActivity(intent);
                    return true;
                }
                return false;
            }
        }, getViewLifecycleOwner(), Lifecycle.State.RESUMED);
    }

    @Override
    public void onEditExerciseClicked(Exercise exercise) {
        // Handle the Edit menu item click
        Intent intent = new Intent(getContext(), CreateExerciseActivity.class);
        intent.putExtra("exercise", exercise);
        startActivity(intent);
    }

    @Override
    public void onDeleteExerciseClicked(Exercise exercise) {
        // Handle the Delete menu item click
        String exerciseId = exercise.getKey();
        DatabaseReference exerciseRef = userExercisesRef.child(exerciseId);
        exerciseRef.removeValue()
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Toast.makeText(getContext(), "Exercise deleted successfully", Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(getContext(), "Failed to delete exercise: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }

    @Override
    public void onExerciseClicked(Exercise exercise) {
        if (addingToWorkout) {
            Intent intent = new Intent(getContext(), WorkoutActivity.class);
            intent.putExtra("selectedWorkout", workout);
            intent.putExtra("selectedExercise", exercise);
            startActivity(intent);
        }
    }

}