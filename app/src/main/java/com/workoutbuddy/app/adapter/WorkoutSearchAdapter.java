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
import com.workoutbuddy.app.model.Workout;

import java.util.List;

public class WorkoutSearchAdapter extends RecyclerView.Adapter<WorkoutSearchAdapter.
        WorkoutSearchViewHolder> {

    public interface WorkoutItemClickListener {
        void onEditWorkoutClicked(Workout workout);

        void onDeleteWorkoutClicked(Workout workout);

        void onWorkoutClicked(Workout workout);
    }

    private List<Workout> workouts;

    private WorkoutItemClickListener itemClickListener;

    public WorkoutSearchAdapter(List<Workout> workouts, WorkoutItemClickListener itemClickListener) {
        this.workouts = workouts;
        this.itemClickListener = itemClickListener;
    }

    public void filterList(List<Workout> filterList) {
        workouts = filterList;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public WorkoutSearchAdapter.WorkoutSearchViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.
                card_workout_private, parent, false);
        return new WorkoutSearchViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull WorkoutSearchViewHolder holder, int position) {
        Workout workout = workouts.get(position);
        holder.workoutName.setText(workout.getName());
        holder.workoutDetails.setText(workout.getDetails());
        holder.workoutTime.setText(workout.getMinutesString());
        holder.workoutOptions.setOnClickListener(new View.OnClickListener() {
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
                            itemClickListener.onEditWorkoutClicked(workout);
                            return true;
                        } else if (itemId == R.id.menu_item_delete) {
                            // Handle the Delete menu item click
                            itemClickListener.onDeleteWorkoutClicked(workout);
                            return true;
                        }
                        return false;
                    }
                });

                // Show the PopupMenu
                popupMenu.show();
            }
        });
        holder.workoutWidget.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                itemClickListener.onWorkoutClicked(workout);
            }
        });
    }

    @Override
    public int getItemCount() {
        return workouts.size();
    }

    public static class WorkoutSearchViewHolder extends RecyclerView.ViewHolder {
        private final TextView workoutName;
        private final TextView workoutDetails;
        private final TextView workoutTime;
        private final TextView workoutOptions;
        private final View workoutWidget;

        public WorkoutSearchViewHolder(@NonNull View itemView) {
            super(itemView);
            workoutName = itemView.findViewById(R.id.workout_name);
            workoutDetails = itemView.findViewById(R.id.workout_details);
            workoutTime = itemView.findViewById(R.id.workout_time);
            workoutOptions = itemView.findViewById(R.id.workout_options);
            workoutWidget = itemView.findViewById(R.id.workout_widget);
        }
    }
}
