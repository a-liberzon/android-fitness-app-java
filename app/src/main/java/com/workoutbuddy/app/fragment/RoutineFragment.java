package com.workoutbuddy.app.fragment;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.SystemClock;
import android.text.InputType;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.webkit.WebView;
import android.widget.Chronometer;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.fragment.app.Fragment;

import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.YouTubePlayer;
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.listeners.AbstractYouTubePlayerListener;
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.views.YouTubePlayerView;
import com.workoutbuddy.app.R;
import com.workoutbuddy.app.activity.SplashActivity;
import com.workoutbuddy.app.activity.WorkoutComplete;
import com.workoutbuddy.app.model.DurationExercise;
import com.workoutbuddy.app.model.RepsExercise;
import com.workoutbuddy.app.model.Workout;
import com.workoutbuddy.app.model.WorkoutController;
import com.workoutbuddy.app.model.WorkoutExercise;

import org.checkerframework.checker.nullness.qual.NonNull;

import java.util.Locale;
import java.util.Optional;

/**
 *
 */
public class RoutineFragment extends Fragment {

    private View view;
    private Chronometer globalChronometer;
    private TextView timerView;
    private boolean isChronometerRunning = false;
    private boolean routineRunning = true;
    private long pauseOffset = 0;
    private boolean restingNow = false;
    private Workout selectedWorkout;
    private CountDownTimer restCountDownTimer;
    private CountDownTimer exerciseCountDownTimer;

    private long restTimer;
    private long timeLeft;
    private boolean restCountDownTimerRunning = false;
    private boolean exerciseCountDownTimerRunning = false;
    private WorkoutController workoutController;

    YouTubePlayerView youTubePlayerView;


    private WebView webView;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        // Inflate the layout for this fragment
        view =  inflater.inflate(R.layout.fragment_routine, container, false);

        //Retrieve the Workout object from the Intent and create the workout controller
        selectedWorkout = (Workout) getArguments().getSerializable("workout");
        workoutController = new WorkoutController(selectedWorkout);
        convertUnitsIfNeeded(selectedWorkout);

        // Set up chronometer and start it
        globalChronometer = view.findViewById(R.id.chronometer);
        globalChronometer.setFormat("%s");
        globalChronometer.setBase(SystemClock.elapsedRealtime());
        globalChronometer.start();
        isChronometerRunning = true;

        // Get a reference to the timer view
        timerView = view.findViewById(R.id.restTimerView);

        // Start GUI
        setUpStartingGUI();

        youTubePlayerView = view.findViewById(R.id.youtube_player_view);
        getLifecycle().addObserver(youTubePlayerView);
        youTubePlayerView.addYouTubePlayerListener(new AbstractYouTubePlayerListener() {
            @Override
            public void onReady(@NonNull YouTubePlayer youTubePlayer) {
                String videoId = workoutController.getCurrentExercise().getVideoId();
                youTubePlayer.loadVideo(videoId, 0);
            }
        });

        //webView = view.findViewById(R.id.webView);
        //webView.getSettings().setJavaScriptEnabled(true);

        // Return inflated view
        return view;
    }

    // Sets the starting look for GUI and functionality
    private void setUpStartingGUI(){
        // Set exercise name
        TextView exerciseName = view.findViewById(R.id.exerciseNameText);
        exerciseName.setText(workoutController.getCurrentExercise().getName());

        // Set up next exercise name, if there are at least 2 exercises and there are no sets left
        TextView nextExerciseText = view.findViewById(R.id.nextExerciseTextView);
        if (workoutController.getListSize() >= 2 && workoutController.currentlyOnLastSet())
            nextExerciseText.setText("Next: " + selectedWorkout.getExerciseList().get(1).getName());

        // Set up reps value if there is more than one set
        if (! workoutController.currentlyOnLastSet()) {
            TextView setsText = view.findViewById(R.id.setsView);
            setsText.setText(workoutController.getExerciseSetsText());
            setsText.setVisibility(View.VISIBLE);
        }

        // Configure all buttons
        setUpButtonHandlers();
    }

    // Higher-level method to configure buttons
    private void setUpButtonHandlers() {
        // Set listeners for weight and reps/duration views/button
        setPopUpForWeight();
        setActionsForRepsOrDurationViewer();

        // Set up handlers for control buttons (prev, pause, next)
        setUpExerciseControlButtons();
    }

    // Set either pop-up for button  to modify the reps values or initialize timer of duration
    private void setActionsForRepsOrDurationViewer() {
        WorkoutExercise currentExercise = workoutController.getCurrentExercise();
        TextView repsTextChanger = view.findViewById(R.id.repsOrTimeView);
        TextView repsOrTimeTitle = view.findViewById(R.id.repsOrTimeTitle);

        // If on a RepBasedExercise
        if (currentExercise instanceof RepsExercise) {
            repsOrTimeTitle.setText("Reps");
            RepsExercise repsExercise = (RepsExercise) currentExercise;
            repsTextChanger.setText(String.valueOf(repsExercise.getReps()));
            setPopUpForReps(repsTextChanger);
        }
        // Else on a DurationBasedExercise
        else if (currentExercise instanceof DurationExercise) {
            repsOrTimeTitle.setText("Duration:");
            startExerciseTimer(view.findViewById(R.id.nextButton), false);
        }
        // This should never happen
        else {
            Toast.makeText(view.getContext(), "Error: Exercise is neither duration or rep based", Toast.LENGTH_SHORT).show();
        }
    }

    // Set up the pop up for changing reps
    private void setPopUpForReps(TextView repsTextChanger) {
        // Set pop up on click for reps button
        repsTextChanger.setOnClickListener(v -> {
            // Build pop up
            AlertDialog.Builder builder = new AlertDialog.Builder(view.getContext());
            builder.setTitle("Set new number of reps");

            // Get input
            final EditText input = new EditText(view.getContext());
            input.setInputType(InputType.TYPE_CLASS_NUMBER);
            builder.setView(input);

            // Set up the ok button
            builder.setPositiveButton("OK", (dialog, which) -> {
                // Get the entered reps value and update based on that
                String newReps = input.getText().toString().trim();
                repsTextChanger.setText(newReps);

                // Hide the keyboard
                InputMethodManager imm = (InputMethodManager) view.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(input.getWindowToken(), 0);
                });

                // Allow to cancel
                builder.setNegativeButton("Cancel", (dialog, which) -> dialog.cancel());

                // Show
                builder.show();
            });
    }

    // Set up pop-up for buttons to modify the weight values
    private void setPopUpForWeight() {
        // Get buttons
        TextView weightTextChanger = view.findViewById(R.id.weightTextChanger);
        TextView weightText = view.findViewById(R.id.weightText);

        // Initialize them to their initial values
        //TODO: require workouts to have at least one exercise when creating new workouts
        WorkoutExercise currentExercise = workoutController.getCurrentExercise();
        weightTextChanger.setText(String.valueOf(currentExercise.getLoad()));
        weightText.setText(selectedWorkout.usingImperial() ? "Weight (lbs)" : "Weight (kgs)");


        // Set pop up on click for weight button
        weightTextChanger.setOnClickListener(v -> {
            // Build pop up
            AlertDialog.Builder builder = new AlertDialog.Builder(view.getContext());
            builder.setTitle("Set new weight");

            // Get input
            final EditText input = new EditText(view.getContext());
            input.setInputType(InputType.TYPE_CLASS_NUMBER);
            builder.setView(input);

            // Set up the ok button
            builder.setPositiveButton("OK", (dialog, which) -> {
                // Get the entered weight value and update based on that
                String newWeight = input.getText().toString().trim();
                weightTextChanger.setText(newWeight);

                // Hide the keyboard
                InputMethodManager imm = (InputMethodManager) view.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(input.getWindowToken(), 0);
            });
            // Allow to cancel
            builder.setNegativeButton("Cancel", (dialog, which) -> dialog.cancel());

            // Show
            builder.show();
        });
    }

    // Set up handlers for control buttons (prev, pause, next)
    private void setUpExerciseControlButtons() {
        // Get buttons and start with disappeared first button
        ImageButton prevButton = view.findViewById(R.id.prevButton);
        ImageButton pausePlayButton = view.findViewById(R.id.pauseOrPlayButton);
        ImageButton nextButton = view.findViewById(R.id.nextButton);
        prevButton.setVisibility(View.INVISIBLE);

        // Handle pause/play button. Control chronometer and change button's image to play/pause
        pausePlayButton.setOnClickListener(v -> {
            // If pausing
            if (routineRunning) {
                pauseWorkout();
                // If on rest activity, cancel the timer
                if (restingNow)
                    cancelRestTimer();
                // If not resting and on a duration exercise cancel exercise timer
                else if (workoutController.onDurationExercise())
                    cancelExerciseTimer();
            }
            // If playing
            else {
                resumeWorkout();
                // If on rest activity, resume timer from where it left of
                if (restingNow)
                    startRestTimer(nextButton, true);
                // If not resting and on a duration exercise resume exercise timer
                if (!restingNow && workoutController.onDurationExercise()) {
                    startExerciseTimer(nextButton, true);
                }
            }
        });

        // Set up action when next is clicked
        nextButton.setOnClickListener(v -> {
            // Enable prev button
            prevButton.setVisibility(View.VISIBLE);

            // If currently on routine view
            if (!restingNow) {
                Optional<WorkoutExercise> nextExerciseOpt = workoutController.getNextExerciseOrSetAndMove();

                // If this is the last exercise, value will be empty, go to completed view
                if (!nextExerciseOpt.isPresent())
                    goToWorkoutCompleteActivity();

                    // Else switch to rest view and start timer
                else {
                    switchToRestView();
                    restingNow = true;
                    startRestTimer(nextButton, false);
                }
            }
            // Else we are on rest view. Move to next exercise, cancel timer
            else {
                cancelRestTimer();
                setGUItoCurrentExercise();
            }
        });

        // Set up previous button. Update GUI to previous exercise's information
        prevButton.setOnClickListener(v -> {
            nextButton.setVisibility(View.VISIBLE);
            Optional<WorkoutExercise> prevExerciseOpt = workoutController.getPrevExerciseOrSetAndMove();
            // If optional is empty, this should not have happened
            if (!prevExerciseOpt.isPresent()) {
                showErrorDialog("Error: Attempting to move to prev exercise, but there is no prev");
            }
            setGUItoCurrentExercise();
            // Disable previous button if back to first exercise
            if (workoutController.isFirstExerciseAndSet())
                prevButton.setVisibility(View.INVISIBLE);

            // Cancel timer
            cancelRestTimer();
        });
    }

    // Handle switching the GUI to rest view
    private void switchToRestView() {
        // Show rest timer and show rest label. Hide global chronometer
        timerView.setVisibility(View.VISIBLE);
        globalChronometer.setVisibility(View.INVISIBLE);
        TextView restLabelView =  view.findViewById(R.id.restLabel);
        restLabelView.setText(workoutController.isNewRoundComing(-1) ?
                "Round completed!\n\nRest" : "Rest");
        restLabelView.setVisibility(View.VISIBLE);

        // Hide exercise-related views
        view.findViewById(R.id.youtube_player_view).setVisibility(View.GONE);
        view.findViewById(R.id.exerciseNameText).setVisibility(View.INVISIBLE);
        view.findViewById(R.id.weightText).setVisibility(View.INVISIBLE);
        view.findViewById(R.id.repsOrTimeTitle).setVisibility(View.INVISIBLE);
        view.findViewById(R.id.weightTextChanger).setVisibility(View.INVISIBLE);
        view.findViewById(R.id.repsOrTimeView).setVisibility(View.INVISIBLE);
        view.findViewById(R.id.setsView).setVisibility(View.INVISIBLE);
    }

    /**
     * Method changes GUI to current exercise as defined by the workout controller.
     */
    private void setGUItoCurrentExercise() {
        // Get views to update


        TextView exerciseNameText = view.findViewById(R.id.exerciseNameText);
        TextView weightText = view.findViewById(R.id.weightText);
        TextView repsText = view.findViewById(R.id.repsOrTimeTitle);
        TextView weightButton = view.findViewById(R.id.weightTextChanger);
        TextView repsButton = view.findViewById(R.id.repsOrTimeView);
        TextView nextExerciseText = view.findViewById(R.id.nextExerciseTextView);
        TextView setsText = view.findViewById(R.id.setsView);

        // If this was called during a rest period
        if (restingNow) {
            // Stop and hide rest timer view and rest label. Make global chronometer visible
            view.findViewById(R.id.restLabel).setVisibility(View.GONE);
            timerView.setVisibility(View.GONE);
            globalChronometer.setVisibility(View.VISIBLE);

            // Show exercise-related views
            youTubePlayerView.setVisibility(View.VISIBLE);
            //webView.loadData(workoutController.getCurrentExercise().getVideoURL(), "text/html","utf-8");
            exerciseNameText.setVisibility(View.VISIBLE);
            weightText.setVisibility(View.VISIBLE);
            repsText.setVisibility(View.VISIBLE);
            weightButton.setVisibility(View.VISIBLE);
            repsButton.setVisibility(View.VISIBLE);
            setsText.setVisibility(View.VISIBLE);

            // Set not resting anymore
            restingNow = false;
        }
        // Get exercise at index and update views to match current exercise
        setActionsForRepsOrDurationViewer(); // Set duration or reps info

        // Set next exercise text
        String nextText = workoutController.getNextExerciseText(this.getContext());
        nextExerciseText.setText(nextText);

        // Configure sets text box.
        if (workoutController.getCurrentExercise().getSets() < 2){ // If 1 set, just hide it
            setsText.setVisibility(View.INVISIBLE);
        }
        // Else set the textview to the current set
        else {
            setsText.setText(workoutController.getExerciseSetsText());
            setsText.setVisibility(View.VISIBLE);
        }
    }

    // Method to start the timer
    private void startRestTimer(ImageButton nextButton, boolean resumeRestTimer) {
        // Set rest time to either resume, or start from current exercise's rest value
        restTimer = resumeRestTimer ? restTimer : workoutController.getRestTime() * 1000L;

        // Set up timer
        restCountDownTimer = new CountDownTimer(restTimer, 1000) {

            // Every second (on tick) update the text to show new time
            @Override
            public void onTick(long millisUntilFinished) {
                // Update time left
                restTimer = millisUntilFinished;

                // Show max time, and stop right after 1 to avoid it seem like a lag
                updateCountDownText(restTimer + 1000, timerView);
            }

            // When timer finishes click the next button
            @Override
            public void onFinish() {
                nextButton.performClick();
                cancelRestTimer();
                restingNow = false;
            }
        }.start();

        // Mark count down timer is running
        restCountDownTimerRunning = true;
    }

    // Method to start countdown for exercise
    private void startExerciseTimer(ImageButton nextButton, boolean resumeExerciseTimer) {
        TextView textView = view.findViewById(R.id.repsOrTimeView);
        WorkoutExercise currentExercise = workoutController.getCurrentExercise();
        long seconds = 5L;
        if (! (currentExercise instanceof DurationExercise)) {
           Toast.makeText(view.getContext(), "Error: Called start exercise timer outside of" +
                   " a duration exercise", Toast.LENGTH_SHORT).show();
        } else {
            seconds = ((DurationExercise) currentExercise).getDuration().getSeconds();
        }
        // Set rest time to either resume, or start from current exercise's rest value
        timeLeft = resumeExerciseTimer ? timeLeft : seconds * 1000L;

        // Set up timer
        exerciseCountDownTimer = new CountDownTimer(timeLeft, 1000) {

            // Every second (on tick) update the text to show new time
            @Override
            public void onTick(long millisUntilFinished) {
                // Update time left
                timeLeft = millisUntilFinished;

                // Start with duration time, and stop right after 1 to avoid it seem like a lag
                updateCountDownText(timeLeft + 1000, textView);
            }

            // When timer finishes click the next button
            @Override
            public void onFinish() {
                nextButton.performClick();
                cancelExerciseTimer();
            }
        }.start();

        // Mark count down timer is running
        exerciseCountDownTimerRunning = true;
    }


    // Method to pause rest timer
    private void cancelRestTimer() {
        if (restCountDownTimerRunning) {
            restCountDownTimer.cancel();
        }
        restCountDownTimerRunning = false;
    }

    // Method to exercise timer
    private void cancelExerciseTimer() {
        if (exerciseCountDownTimerRunning) {
            exerciseCountDownTimer.cancel();
        }
        exerciseCountDownTimerRunning = false;
    }

    // Method to update the count down text view
    private void updateCountDownText(long timeInMillis, TextView textViewToUpdate) {
        int minutes = (int) (timeInMillis / 1000) / 60;
        int seconds = (int) (timeInMillis / 1000) % 60;
        textViewToUpdate.setText(String.format(Locale.getDefault(), "%02d:%02d", minutes, seconds));
    }

    // Method to pause workout
    private void pauseWorkout() {
        // Update button to play
        routineRunning = false;
        ImageButton pauseOrPlayButton = view.findViewById(R.id.pauseOrPlayButton);
        pauseOrPlayButton.setImageResource(R.drawable.play_button);

        // Disable next and prev buttons
        view.findViewById(R.id.nextButton).setVisibility(View.INVISIBLE);
        view.findViewById(R.id.prevButton).setVisibility(View.INVISIBLE);

        // Pause global chronometer
        if (isChronometerRunning) {
            globalChronometer.stop();
            pauseOffset = SystemClock.elapsedRealtime() - globalChronometer.getBase();
            isChronometerRunning = false;
        }
    }

    // Method to resume workout
    private void resumeWorkout() {
        // Update button to pause
        routineRunning = true;
        ImageButton pauseOrPlayButton = view.findViewById(R.id.pauseOrPlayButton);
        pauseOrPlayButton.setImageResource(R.drawable.pause_button);

        // Re-enable back button, as long as it is not the first exercise
        if (workoutController.isFirstExerciseAndSet())
            view.findViewById(R.id.prevButton).setVisibility(View.VISIBLE);

        // Enable next button
        view.findViewById(R.id.nextButton).setVisibility(View.VISIBLE);

        // Resume global chronometer
        if (!isChronometerRunning) {
            globalChronometer.setBase(SystemClock.elapsedRealtime() - pauseOffset);
            globalChronometer.start();
            isChronometerRunning = true;
        }
    }

    // Method to show an error message
    private void showErrorDialog(String errorMessage) {
        AlertDialog.Builder builder = new AlertDialog.Builder(view.getContext());
        builder.setTitle("Error")
                .setMessage(errorMessage)
                .setPositiveButton("OK", null)
                .setIcon(android.R.drawable.ic_dialog_alert)
                .show();
    }

    // Navigate to workout complete activity
    private void goToWorkoutCompleteActivity() {
        Intent intent = new Intent(view.getContext(), WorkoutComplete.class);
        Bundle bundle = new Bundle();
        intent.putExtras(bundle);
        startActivity(intent);
    }

    // TODO: Define on pause, on stop etc.

    /**
     * Method to get the theme saved in preferences, to use it in the app. If not set will
     * default to follow system.
     * TODO: Save new units in database to avoid redoing this process.
     */
    private String convertUnitsIfNeeded(Workout selectedWorkout) {
        SharedPreferences sharedPreferences = getContext().getSharedPreferences(SplashActivity.PREFERENCES_KEY, Context.MODE_PRIVATE);
        String retrieved = sharedPreferences.getString("selectedUnit", "imperial");
        if (retrieved.equals("imperial") && !selectedWorkout.usingImperial()) {
            selectedWorkout.changeUnitsToImperial();

        } else if (retrieved.equals("metric") && selectedWorkout.usingImperial()) {
            selectedWorkout.changeUnitsToMetric();
        }
        return retrieved;
    }
}