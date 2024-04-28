package com.workoutbuddy.app.activity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.FirebaseDatabase;
import com.workoutbuddy.app.R;
import com.workoutbuddy.app.model.User;

public class SplashActivity extends AppCompatActivity {

    // Key to use everytime we use shared preferences
    public static final String PREFERENCES_KEY = "com.workoutbuddy.app.PREFERENCES_KEY";

    FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // Restore theme
        int theme = getSavedThemeFromSharedPreferences();
        AppCompatDelegate.setDefaultNightMode(theme);

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        mAuth = FirebaseAuth.getInstance();
        FirebaseUser firebaseUser = mAuth.getCurrentUser();
        FirebaseDatabase.getInstance().setPersistenceEnabled(true);
        Intent intent;
        if (firebaseUser != null) {
            User user = new User(firebaseUser);
            intent = new Intent(this, MainActivity.class);
        } else {
            intent = new Intent(this, AuthActivity.class);
        }
        navigateDelayed(intent);
    }

    private void navigateDelayed(final Intent intent) {
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                startActivity(intent);
                finish();
            }
        }, 500);
    }

    /**
     * Method to get the theme saved in preferences, to use it in the app. If not set will
     * default to follow system.
     */
    private int getSavedThemeFromSharedPreferences() {
        SharedPreferences sharedPreferences = getSharedPreferences(SplashActivity.PREFERENCES_KEY, Context.MODE_PRIVATE);
        int retrieved = sharedPreferences.getInt("selectedTheme", AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM);
        return retrieved;
    }
}
