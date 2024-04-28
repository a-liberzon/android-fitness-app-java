package com.workoutbuddy.app.fragment;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import androidx.fragment.app.Fragment;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.workoutbuddy.app.R;
import com.workoutbuddy.app.model.Equipment;
import com.workoutbuddy.app.model.Exercise;
import com.workoutbuddy.app.model.MuscleGroup;
import com.workoutbuddy.app.model.ThemeManager;

import org.checkerframework.checker.nullness.qual.NonNull;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

public class CustomExerciseFragment extends Fragment {

    private DatabaseReference userExercisesRef;
    private String userId;
    private Exercise existingExercise;
    private ThemeManager themeManager;

    // Inputs
    private String name;
    private String primaryMuscle;
    private String secondaryMuscles;
    private String difficultyStr;
    private String equipment;

    //xml items
    private EditText exerciseEditText;
    private TextView textDifficulty;
    private TextView textPrimaryMuscle;
    private TextView textSecondaryMuscles;
    private TextView textEquipment;
    private Button saveBtn;

    //private boolean fromWorkout
    public CustomExerciseFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_custom_exercise, container, false);

        // TODO: place in separate method
        userId = FirebaseAuth.getInstance().getUid();
        userExercisesRef = FirebaseDatabase.getInstance().getReference().child("users").child(userId).child("exercises");


        themeManager = new ThemeManager(getContext());
        // Retrieve the Workout object from the Intent
        Bundle arguments = getArguments();
        if (arguments != null && arguments.containsKey("exercise")) {
            existingExercise = (Exercise) arguments.getSerializable("exercise");
        }

        // TODO: Refactor
        exerciseEditText = view.findViewById(R.id.text_exercise_name);

        textDifficulty = view.findViewById(R.id.text_difficulty);
        textDifficulty.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDifficultyDialog();
            }
        });

        textPrimaryMuscle = view.findViewById(R.id.text_primary_muscle);
        textPrimaryMuscle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showMusclePrimaryDialogue();
            }
        });

        textSecondaryMuscles = view.findViewById(R.id.text_secondary_muscles);
        textSecondaryMuscles.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showMusclesSecondaryDialogue();
            }
        });

        textEquipment = view.findViewById(R.id.text_equipment);
        textEquipment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showEquipmentDialog();
            }
        });

        saveBtn = view.findViewById(R.id.save_btn);
        // TODO: Change theme
        //themeManager.setThemeForSettingsButtons(saveBtn);

        saveBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                name = exerciseEditText.getText().toString().trim();
                String exerciseKey;
                if (existingExercise == null) {
                    exerciseKey = userExercisesRef.push().getKey();
                }
                else {
                    exerciseKey = existingExercise.getKey();
                }
                Exercise exercise = new Exercise(exerciseKey, name, getMuscleGroupByName(primaryMuscle),
                        convertStringToMuscleGroupSet(secondaryMuscles),
                        convertStringToEquipmentSet(equipment), getDifficultyScale(difficultyStr));

                userExercisesRef.child(exerciseKey).setValue(exercise.toExerciseDTO());

                requireActivity().onBackPressed();

            }
        });

        if (existingExercise != null) {
            exerciseEditText.setText(existingExercise.getName());
            primaryMuscle = existingExercise.getPrimaryMuscleGroup().toString();
            textPrimaryMuscle.setText(primaryMuscle);
            secondaryMuscles = existingExercise.convertMuscleGroupSetToString();
            textSecondaryMuscles.setText(secondaryMuscles);
            difficultyStr = existingExercise.getDifficultyAsString();
            textDifficulty.setText(difficultyStr);
            equipment = existingExercise.convertEquipmentSetToString();
            textEquipment.setText(equipment);
        }

        return view;
    }

    private void showDifficultyDialog() {
        LayoutInflater inflater = requireActivity().getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.dialog_difficulty, null);
        RadioGroup radioGroupDifficulty = dialogView.findViewById(R.id.radio_group_difficulty);

        // Get the currently selected difficulty
        String currentDifficulty = difficultyStr; // Assuming 'difficulty' is the variable storing the selected difficulty

        if (currentDifficulty != null) {
            // Find the index of the currently selected difficulty
            int selectedRadioButtonId = -1;
            for (int i = 0; i < radioGroupDifficulty.getChildCount(); i++) {
                RadioButton radioButton = (RadioButton) radioGroupDifficulty.getChildAt(i);
                if (radioButton.getText().toString().equals(currentDifficulty)) {
                    selectedRadioButtonId = radioButton.getId();
                    break;
                }
            }

            // Check the corresponding radio button
            if (selectedRadioButtonId != -1) {
                radioGroupDifficulty.check(selectedRadioButtonId);
            }
        }

        AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
        builder.setTitle("Select Difficulty")
                .setView(dialogView)
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        int checkedRadioButtonId = radioGroupDifficulty.getCheckedRadioButtonId();
                        RadioButton selectedRadioButton = dialogView.findViewById(checkedRadioButtonId);
                        if (selectedRadioButton != null) {
                            String selectedDifficulty = selectedRadioButton.getText().toString();
                            TextView textDifficulty = requireView().findViewById(R.id.text_difficulty);
                            textDifficulty.setText(selectedDifficulty);
                            difficultyStr = selectedDifficulty;
                        }
                    }
                })
                .setNegativeButton("Cancel", null);

        AlertDialog dialog = builder.create();
        dialog.show();
    }

    private void showMusclePrimaryDialogue() {
        LayoutInflater inflater = requireActivity().getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.dialogue_muscle_primary, null);
        RadioGroup radioGroup = dialogView.findViewById(R.id.radio_group);

        // Get the currently selected muscle
        String currentPrimaryMuscle = primaryMuscle;

        if (currentPrimaryMuscle != null) {
            int selectedRadioButtonId = -1;
            for (int i = 0; i < radioGroup.getChildCount(); i++) {
                if (radioGroup.getChildAt(i) instanceof RadioButton) {
                    RadioButton radioButton = (RadioButton) radioGroup.getChildAt(i);
                    if (radioButton.getText().toString().equals(currentPrimaryMuscle)) {
                        selectedRadioButtonId = radioButton.getId();
                        break;
                    }
                }
            }

            // Check the corresponding radio button
            if (selectedRadioButtonId != -1) {
                radioGroup.check(selectedRadioButtonId);
            }
        }

        AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
        builder.setTitle("Select Primary Muscle")
                .setView(dialogView)
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        int checkedRadioButtonId = radioGroup.getCheckedRadioButtonId();
                        RadioButton selectedRadioButton = dialogView.findViewById(checkedRadioButtonId);
                        if (selectedRadioButton != null) {
                            String selectedMuscle = selectedRadioButton.getText().toString();
                            TextView textMuscle = requireView().findViewById(R.id.text_primary_muscle);
                            textMuscle.setText(selectedMuscle);
                            primaryMuscle = selectedMuscle;
                        }
                    }
                })
                .setNegativeButton("Cancel", null);

        AlertDialog dialog = builder.create();
        dialog.show();
    }

    private void showMusclesSecondaryDialogue() {
        View dialogView = getLayoutInflater().inflate(R.layout.dialogue_muscle_secondary, null);
        AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
        builder.setView(dialogView);

        ViewGroup checkboxContainer = dialogView.findViewById(R.id.checkbox_container);
        List<CheckBox> checkBoxes = findCheckBoxes(checkboxContainer);

        builder.setTitle("Select Secondary Muscles");
        TextView textMuscles = requireView().findViewById(R.id.text_secondary_muscles);

        // Check the previously selected secondary muscles
        if (secondaryMuscles != null) {
            String[] selectedMuscles = secondaryMuscles.split(", ");
            for (CheckBox checkBox : checkBoxes) {
                if (Arrays.asList(selectedMuscles).contains(checkBox.getText().toString())) {
                    checkBox.setChecked(true);
                }
            }
        }

        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                StringBuilder selectedMuscles = new StringBuilder();

                for (CheckBox checkBox : checkBoxes) {
                    if (checkBox.isChecked() && selectedMuscles.length() > 0) {
                        selectedMuscles.append(", ").append(checkBox.getText());
                    } else if (checkBox.isChecked()) {
                        selectedMuscles.append(checkBox.getText());
                    }
                }

                textMuscles.setText(selectedMuscles.toString());
                secondaryMuscles = selectedMuscles.toString();
            }
        });

        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });

        builder.show();
    }


    private void showEquipmentDialog() {
        View dialogView = getLayoutInflater().inflate(R.layout.dialog_equipment, null);
        AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
        builder.setView(dialogView);

        ViewGroup checkboxContainer = dialogView.findViewById(R.id.checkbox_container);
        List<CheckBox> checkBoxes = findCheckBoxes(checkboxContainer);

        builder.setTitle("Equipment");
        TextView textEquipment = requireView().findViewById(R.id.text_equipment);

        if (equipment != null) {
            String[] selectedEquipment = equipment.split(", ");
            for (CheckBox checkBox : checkBoxes) {
                if (Arrays.asList(selectedEquipment).contains(checkBox.getText().toString())) {
                    checkBox.setChecked(true);
                }
            }
        }

        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                StringBuilder selectedEquipment = new StringBuilder();

                for (CheckBox checkBox : checkBoxes) {
                    if (checkBox.isChecked() && selectedEquipment.length() > 0) {
                        selectedEquipment.append(", " + checkBox.getText());
                    } else if (checkBox.isChecked()) {
                        selectedEquipment.append(checkBox.getText());
                    }
                }

                textEquipment.setText(selectedEquipment.toString());
                equipment = selectedEquipment.toString();
            }
        });

        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });

        builder.show();
    }

    private List<CheckBox> findCheckBoxes(ViewGroup viewGroup) {
        List<CheckBox> checkBoxesList = new ArrayList<>();

        for (int i = 0; i < viewGroup.getChildCount(); i++) {
            View child = viewGroup.getChildAt(i);
            if (child instanceof CheckBox) {
                checkBoxesList.add((CheckBox) child);
            }
        }
        return checkBoxesList;
    }

    // TODO: Perhaps move them into another class?



    private Set<Equipment> convertStringToEquipmentSet(String equipmentString) {
        Set<Equipment> equipmentSet = new LinkedHashSet<>();
        if (equipmentString != null && !equipmentString.isEmpty()) {
            String[] equipmentArray = equipmentString.split(",");
            for (String equipmentName : equipmentArray) {
                Equipment equipment = getEquipmentByName(equipmentName.trim());
                if (equipment != null) {
                    equipmentSet.add(equipment);
                }
            }
        }
        return equipmentSet;
    }

    private Equipment getEquipmentByName(String equipmentName) {
        for (Equipment equipment : Equipment.values()) {
            if (equipment.toString().equalsIgnoreCase(equipmentName)) {
                return equipment;
            }
        }
        return null;
    }

    private Set<MuscleGroup> convertStringToMuscleGroupSet(String muscleGroupString) {
        Set<MuscleGroup> muscleGroupSet = new LinkedHashSet<>();
        if (muscleGroupString != null && !muscleGroupString.isEmpty()) {
            String[] muscleGroupArray = muscleGroupString.split(",");
            for (String muscleGroupName : muscleGroupArray) {
                MuscleGroup muscleGroup = getMuscleGroupByName(muscleGroupName.trim());
                if (muscleGroup != null) {
                    muscleGroupSet.add(muscleGroup);
                }
            }
        }
        return muscleGroupSet;
    }

    private MuscleGroup getMuscleGroupByName(String muscleGroupName) {
        for (MuscleGroup muscleGroup : MuscleGroup.values()) {
            if (muscleGroup.toString().equalsIgnoreCase(muscleGroupName)) {
                return muscleGroup;
            }
        }
        return null;
    }

    private int getDifficultyScale(String difficultyStr) {
        int difficulty;
        switch (difficultyStr) {
            case "Beginner":
                difficulty = 1;
                break;
            case "Intermediate":
                difficulty = 2;
                break;
            case "Advanced":
                difficulty = 3;
                break;
            default:
                difficulty = -1;
                break;
        }
        return difficulty;
    }
}