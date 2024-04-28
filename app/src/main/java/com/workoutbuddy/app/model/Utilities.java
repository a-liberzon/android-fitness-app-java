package com.workoutbuddy.app.model;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.view.Gravity;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.core.content.res.ResourcesCompat;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.workoutbuddy.app.R;
import com.workoutbuddy.app.activity.MainActivity;
import com.workoutbuddy.app.activity.WorkoutActivity;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.IntStream;

public class Utilities {

    private Context context;
    private ThemeManager themeManager;
    public Utilities(Context context) {
        this.context = context;
        themeManager = new ThemeManager(context);
    }

    /**
     * Method to check whether or not night mode is on.
     */
    public boolean isNighModeOn(Context context){
        return  ((context.getResources().getConfiguration().uiMode
                & Configuration.UI_MODE_NIGHT_MASK) == Configuration.UI_MODE_NIGHT_YES);
    }

    /**
     * Method to set color of floating buttons
     * TODO: Fix, method does not work for fragments.
     */
    public void setFloatingBtnColorAuto(Context context, FloatingActionButton btn, Resources resources) {
        int btnColor = isNighModeOn(context) ? R.color.my_dark_primary :  R.color.my_light_primary;
        btn.setBackgroundTintList(ColorStateList.valueOf(ResourcesCompat.getColor(resources, btnColor, null)));
    }

    /**
     * Temporary method for testing.
     */
    public static List<Workout> getDummyWorkouts() {
        ArrayList workoutList = new ArrayList();
        IntStream.range(1, 7).forEach(i -> workoutList.add(new Workout(i)));
        return workoutList;
    }

    /**
     * Method to display all workouts on provided LinearLayout
     */
    public void displayWorkouts(List<Workout> workouts, Context context,
                                LinearLayout workoutsContainer, boolean displayingFriendWorkouts) {
        int screenWidth = context.getResources().getDisplayMetrics().widthPixels;
        int screenHeight = context.getResources().getDisplayMetrics().heightPixels;
        int margin = (int) Math.round(screenWidth * 0.05);
        int width = (int) Math.round(screenWidth * 0.80);
        int height = screenHeight / 4;

        // Add all workouts
        workouts.forEach(workout -> {
            // Create box holding a workout's info
            LinearLayout workoutLayout = new LinearLayout(context);
            LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(width, height);
            layoutParams.setMargins(0, margin, 0, margin); // Don't set horizontal margins
            layoutParams.gravity = Gravity.CENTER_HORIZONTAL; // Center horizontally automatically
            workoutLayout.setLayoutParams(layoutParams);
            workoutLayout.setOrientation(LinearLayout.VERTICAL);
            workoutLayout.setGravity(Gravity.CENTER);

            // Set a colored box with border and round edges
            GradientDrawable borderDrawable = new GradientDrawable();
            borderDrawable.setStroke(10, Color.BLACK);
            borderDrawable.setCornerRadius(40);
            workoutLayout.setBackground(borderDrawable);
            borderDrawable.setColor(isNighModeOn(context) ? Color.DKGRAY : Color.LTGRAY);

            // Add workout title text
            TextView workoutNameView = new TextView(context);
            workoutNameView.setText(workout.getName());
            workoutNameView.setGravity(Gravity.LEFT);
            workoutNameView.setTextSize(MainActivity.TEXT3);
            workoutNameView.setPadding(50, 25, 25, 25);

            // Add workout details text
            TextView workoutDetailsView = new TextView(context);
            workoutDetailsView.setText(workout.getWorkoutTime().toMinutes()+ " min - " +
                    workout.getDifficultyAsString() + " - " +  workout.getCategory() + "\n\n"+workout.getEquipment());
            workoutDetailsView.setGravity(Gravity.LEFT);
            workoutDetailsView.setTextSize(MainActivity.TEXT4);
            workoutDetailsView.setPadding(50, 25, 25, 25);

            // Add listener to go into a workout
            workoutLayout.setOnClickListener(v -> {
                // Start WorkoutActivity passing the selected Workout object
                Intent intent = new Intent(context, WorkoutActivity.class);
                intent.putExtra("selectedWorkout", workout);
                intent.putExtra("viewingFriendsWorkouts", displayingFriendWorkouts);
                context.startActivity(intent);
            });

            // Add all views to GUI
            workoutLayout.addView(workoutNameView);
            workoutLayout.addView(workoutDetailsView);
            workoutsContainer.addView(workoutLayout);
        });
    }

    public static Equipment convertToEquipment(String equipmentString) {
        try {
            return Equipment.valueOf(equipmentString.toUpperCase());
        } catch (IllegalArgumentException e) {
            // Handle the exception when the string value does not match any enum constant
            return Equipment.NO_EQUIPMENT;  // or provide another fallback option
        }
    }

    public static MuscleGroup convertToMuscleGroup(String muscleGroupString) {
        try {
            return MuscleGroup.valueOf(muscleGroupString.toUpperCase());
        } catch (IllegalArgumentException e) {
            // Handle the exception when the string value does not match any enum constant
            return null;  // or provide another fallback option
        }
    }

    /**
     * Method to show a pop that request numerical input and updates given text view with the input.
     */
    public void getInput(String title, View actionView, TextView textViewToUpdate, int inputType) {
        actionView.setOnClickListener(v -> {
            // Create a new AlertDialog Builder
            AlertDialog.Builder builder = new AlertDialog.Builder(context);
            builder.setTitle(title);

            // Get input
            final EditText input = new EditText(context);
            input.setInputType(inputType);
            builder.setView(input);

            // Set up the OK button and Cancel buttons
            builder.setPositiveButton("OK", null);
            builder.setNegativeButton("Cancel", null);

            // Configure listeners
            AlertDialog alertDialog = builder.create();
            alertDialog.setOnShowListener(dialog -> {
                // Configure buttons
                Button positiveButton = alertDialog.getButton(AlertDialog.BUTTON_POSITIVE);
                Button negativeButton = alertDialog.getButton(AlertDialog.BUTTON_NEGATIVE);
                themeManager.configureDialogButtons(positiveButton, negativeButton);

                // Set click listener for the OK button
                positiveButton.setOnClickListener(v1 -> {
                    // Get the entered weight value and update based on that
                    String newVal = input.getText().toString().trim();
                    textViewToUpdate.setText(newVal);

                    // Hide the keyboard and pop up
                    InputMethodManager imm = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(input.getWindowToken(), 0);
                    alertDialog.dismiss();
                });

                // Set click listener for the Cancel button
                negativeButton.setOnClickListener(v1 -> {
                    alertDialog.dismiss();
                });
            });
            alertDialog.show();
        });
    }
}

