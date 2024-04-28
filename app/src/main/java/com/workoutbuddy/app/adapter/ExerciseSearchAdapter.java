package com.workoutbuddy.app.adapter;

import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.PopupMenu;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.workoutbuddy.app.R;
import com.workoutbuddy.app.model.Exercise;

import java.util.List;

public class ExerciseSearchAdapter extends RecyclerView.Adapter<ExerciseSearchAdapter.
        ExerciseSearchViewHolder> {

    public interface ExerciseItemClickListener {
        void onEditExerciseClicked(Exercise exercise);
        void onDeleteExerciseClicked(Exercise exercise);
        void onExerciseClicked(Exercise exercise);
    }

    private List<Exercise> exercises;
    private ExerciseItemClickListener itemClickListener;

    public ExerciseSearchAdapter(List<Exercise> exercises, ExerciseItemClickListener itemClickListener) {
        this.exercises = exercises;
        this.itemClickListener = itemClickListener;
    }


    public void filterList(List<Exercise> filterList) {
        exercises = filterList;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ExerciseSearchAdapter.ExerciseSearchViewHolder onCreateViewHolder(
            @NonNull ViewGroup parent, int viewType) {
        // below line is to inflate our layout.
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.
                card_exercise_search, parent, false);
        return new ExerciseSearchViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ExerciseSearchAdapter.ExerciseSearchViewHolder holder, int position) {
        Exercise exercise = exercises.get(position);
        holder.exerciseName.setText(exercise.getName());
        holder.exerciseMuscleGroup.setText(exercise.getPrimaryMuscleGroup().toString());
        holder.exerciseOptions.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Create a PopupMenu
                PopupMenu popupMenu = new PopupMenu(view.getContext(), view);
                popupMenu.inflate(R.menu.menu_edit_delete);

                // Set a click listener for menu items
                popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem item) {
                        int itemId = item.getItemId();
                        if (itemId == R.id.menu_item_edit) {
                            // Handle the Edit menu item click
                            itemClickListener.onEditExerciseClicked(exercise);
                            return true;
                        } else if (itemId == R.id.menu_item_delete) {
                            // Handle the Delete menu item click
                            itemClickListener.onDeleteExerciseClicked(exercise);
                            return true;
                        }
                        return false;
                    }
                });

                // Show the PopupMenu
                popupMenu.show();
            }
        });
        holder.exerciseWidget.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                itemClickListener.onExerciseClicked(exercise);
            }
        });
    }


    @Override
    public int getItemCount() {
        return exercises.size();
    }

    public static class ExerciseSearchViewHolder extends RecyclerView.ViewHolder {
        // creating variables for our views
        private final TextView exerciseName;
        private final TextView exerciseMuscleGroup;
        private final TextView exerciseOptions;
        private final View exerciseWidget; // Reference to the exercise widget


        public ExerciseSearchViewHolder(@NonNull View itemView) {
            super(itemView);
            exerciseName = itemView.findViewById(R.id.exercise_name);
            exerciseMuscleGroup = itemView.findViewById(R.id.exercise_muscle_primary);
            exerciseOptions = itemView.findViewById(R.id.exercise_options);
            exerciseWidget = itemView.findViewById(R.id.exercise_widget);

        }
    }


}
