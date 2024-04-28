package com.workoutbuddy.app.adapter;

import android.util.SparseBooleanArray;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.PopupMenu;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.YouTubePlayer;
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.listeners.AbstractYouTubePlayerListener;
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.views.YouTubePlayerView;
import com.workoutbuddy.app.R;
import com.workoutbuddy.app.model.DurationExercise;
import com.workoutbuddy.app.model.Exercise;
import com.workoutbuddy.app.model.RepsExercise;
import com.workoutbuddy.app.model.WorkoutExercise;

import java.util.List;

public class WorkoutExercisesAdapter extends
        RecyclerView.Adapter<WorkoutExercisesAdapter.WorkoutExercisesViewHolder> {

    public interface ExerciseItemClickListener {
        void onEditExerciseClicked(WorkoutExercise exercise);
        void onDeleteExerciseClicked(WorkoutExercise exercise);
        void onExerciseClicked(WorkoutExercise exercise);
    }

    private List<WorkoutExercise> exercises;
    private ExerciseItemClickListener itemClickListener;
    private SparseBooleanArray videoVisibilityMap;
    private int openVideoPosition = RecyclerView.NO_POSITION; // Initialize to NO_POSITION to indicate no open video initially

    public WorkoutExercisesAdapter(List<WorkoutExercise> exercises,
                                   ExerciseItemClickListener itemClickListener) {
        this.exercises = exercises;
        this.itemClickListener = itemClickListener;
        this.videoVisibilityMap = new SparseBooleanArray();
    }

    @NonNull
    @Override
    public WorkoutExercisesViewHolder onCreateViewHolder(
            @NonNull ViewGroup parent, int viewType) {
        // below line is to inflate our layout.
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.
                card_workout_exercise, parent, false);
        return new WorkoutExercisesViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull WorkoutExercisesViewHolder holder, int position) {
        WorkoutExercise exercise = exercises.get(position);
        holder.exerciseName.setText(exercise.getName());

        String detailsStr;
        if (exercise instanceof DurationExercise) {
            detailsStr = "Duration: " + ((DurationExercise) exercise).getDuration().getSeconds() + " sec";
        }
        else {
            detailsStr = "Reps: " + ((RepsExercise) exercise).getReps();
        }
        holder.exerciseDetails.setText(detailsStr);
        holder.exerciseSets.setText("Sets: " + exercise.getSets());
        if (exercise.getUrl() != null) {
            holder.exerciseVideo.addYouTubePlayerListener(new AbstractYouTubePlayerListener() {
                @Override
                public void onReady(@org.checkerframework.checker.nullness.qual.NonNull YouTubePlayer youTubePlayer) {
                    String videoId = exercise.getVideoId();
                    youTubePlayer.loadVideo(videoId, 0);
                }
            });
        }


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

        // Set the visibility of the video view based on the visibility map and openVideoPosition
        boolean isVideoVisible = videoVisibilityMap.get(position, false);


        if (videoVisibilityMap.get(position, false) && holder.getAdapterPosition() == openVideoPosition) {
            holder.exerciseVideo.setVisibility(View.VISIBLE);
        } else {
            holder.exerciseVideo.setVisibility(View.GONE);
        }

        holder.exerciseWidget.setOnClickListener(view -> {
            // Toggle the visibility of the video view and update the visibility map
            if (exercise.getUrl() != null) {
                boolean isCurrentVideoVisible = videoVisibilityMap.get(position, false);
                videoVisibilityMap.put(position, !isCurrentVideoVisible);

                // If a video is opened in this click, update the openVideoPosition
                if (!isCurrentVideoVisible) {
                    openVideoPosition = holder.getAdapterPosition();
                } else {
                    openVideoPosition = RecyclerView.NO_POSITION; // No video is open now
                }

                notifyDataSetChanged(); // Notify the adapter to update the video views
            }

            // Notify the click listener
            itemClickListener.onExerciseClicked(exercise);
        });
    }


    @Override
    public int getItemCount() {
        return exercises.size();
    }

    public static class WorkoutExercisesViewHolder extends RecyclerView.ViewHolder {
        // creating variables for our views
        private final TextView exerciseName;
        private final TextView exerciseDetails;
        private final TextView exerciseSets;
        private final TextView exerciseOptions;
        private final YouTubePlayerView exerciseVideo;
        final View exerciseWidget; // Reference to the exercise widget


        public WorkoutExercisesViewHolder(@NonNull View itemView) {
            super(itemView);
            exerciseName = itemView.findViewById(R.id.workout_exercise_name);
            exerciseDetails = itemView.findViewById(R.id.workout_exercise_details);
            exerciseSets = itemView.findViewById(R.id.workout_exercise_sets);
            exerciseOptions = itemView.findViewById(R.id.workout_exercise_options);
            exerciseVideo = itemView.findViewById(R.id.workout_exercise_video);
            exerciseWidget = itemView.findViewById(R.id.workout_exercise_widget);
        }
    }

}
