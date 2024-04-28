package com.workoutbuddy.app.fragment;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.fragment.app.Fragment;

import com.google.firebase.auth.FirebaseAuth;
import com.workoutbuddy.app.R;
import com.workoutbuddy.app.activity.AuthActivity;
import com.workoutbuddy.app.activity.SplashActivity;
import com.workoutbuddy.app.databinding.FragmentSettingsBinding;
import com.workoutbuddy.app.model.ThemeManager;
import com.workoutbuddy.app.model.Workout;

import java.util.LinkedList;

public class SettingsFragment extends Fragment {
    private FragmentSettingsBinding binding;
    private LinkedList<Workout> workouts = new LinkedList<>();
    private SharedPreferences sharedPreferences;

    private FirebaseAuth mAuth;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate view
        binding = FragmentSettingsBinding.inflate(inflater, container, false);
        View root = binding.getRoot();
        ThemeManager themeManager = new ThemeManager(getContext());

        mAuth = FirebaseAuth.getInstance();

        // Adjust button colors to adapt to theme
        themeManager.setThemeForSettingsButtons(binding);

        binding.signOutButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mAuth.signOut();
                Intent intent = new Intent(getContext(), AuthActivity.class);
                startActivity(intent);
            }
        });

        // Retrieve the list of workouts from arguments
        Bundle args = getArguments();
        if (args != null) {

            // TODO: Why is this here??
            workouts = (LinkedList<Workout>) args.getSerializable("workouts");
        }

        // Save user selected preferences
        sharedPreferences = requireActivity().getSharedPreferences(
                SplashActivity.PREFERENCES_KEY, Context.MODE_PRIVATE);
        setUpControlsForUnits(root);
        setUpControlsForThemes(root);
        restoreSelectedUnitButton();
        restoreSelectedThemeButton();
        return root;
    }

    // Handle radio buttons for unit conversion
    private void setUpControlsForUnits(View root) {
        RadioGroup unitsRadioGroup = root.findViewById(R.id.units_radio_group);
        unitsRadioGroup.setOnCheckedChangeListener((group, checkedId) -> {
            RadioButton radioButton = root.findViewById(checkedId);
            if (radioButton.getId() == R.id.radio_metric) {
                workouts.forEach(Workout::changeUnitsToMetric);
                saveSelectedUnit("metric");
            } else if (radioButton.getId() == R.id.radio_imperial) {
                workouts.forEach(Workout::changeUnitsToImperial);
                saveSelectedUnit("imperial");
            }
        });
    }

    private void setUpControlsForThemes(View root) {
        RadioGroup themeRadioGroup = root.findViewById(R.id.theme_radio_group);
        themeRadioGroup.setOnCheckedChangeListener((group, checkedId) -> {
            RadioButton radioButton = root.findViewById(checkedId);
            int theme;
            if (radioButton.getId() == R.id.radio_light_theme)
                theme = AppCompatDelegate.MODE_NIGHT_NO;  // 1 = light
            else if (radioButton.getId() == R.id.radio_dark_theme)
                theme = AppCompatDelegate.MODE_NIGHT_YES; // 2 = dark
            else
                theme = AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM; // -1 = auto

            AppCompatDelegate.setDefaultNightMode(theme);
            saveSelectedTheme(theme);
        });
    }

    // Save the selected theme to SharedPreferences
    private void saveSelectedTheme(int theme) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putInt("selectedTheme", theme);
        editor.apply();
    }

    // Save the selected unit to SharedPreferences
    private void saveSelectedUnit(String unit) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("selectedUnit", unit);
        editor.apply();
    }

    /**
     * Theme has to be restored when app launches but the button has to be restored to what was set
     * here.
     */
    private void restoreSelectedThemeButton() {
        int selectedTheme = sharedPreferences.getInt("selectedTheme", AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM);
        if (selectedTheme == AppCompatDelegate.MODE_NIGHT_NO) {
            binding.radioLightTheme.setChecked(true);
        } else if (selectedTheme == AppCompatDelegate.MODE_NIGHT_YES) {
            binding.radioDarkTheme.setChecked(true);
        } else {
            binding.radioAutoTheme.setChecked(true);
        }
    }

    // Restore the selected unit from SharedPreferences
    private void restoreSelectedUnitButton() {
        String selectedUnit = sharedPreferences.getString("selectedUnit", "imperial");
        if (selectedUnit.equals("metric")) {
            binding.radioMetric.setChecked(true);
        } else {
            binding.radioImperial.setChecked(true);
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}