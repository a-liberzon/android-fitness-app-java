package com.workoutbuddy.app.activity;

import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import com.workoutbuddy.app.R;

// Temporary code until we design what to do here
public class WorkoutComplete extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_workout_complete);

        Button returnButton = findViewById(R.id.returnButton);
        returnButton.setOnClickListener(view -> {
            Intent intent = new Intent(this, MainActivity.class);
            Bundle bundle = new Bundle();
            intent.putExtras(bundle);
            startActivity(intent);
        });
    }
}