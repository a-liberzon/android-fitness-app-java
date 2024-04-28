package com.workoutbuddy.app.activity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import com.bumptech.glide.Glide;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.workoutbuddy.app.R;
import com.workoutbuddy.app.databinding.ActivityMainBinding;
import com.workoutbuddy.app.model.ThemeManager;

import java.io.File;
import java.util.Objects;

import de.hdodenhof.circleimageview.CircleImageView;

public class MainActivity extends AppCompatActivity {

    private AppBarConfiguration drawerOptions;
    private ActivityMainBinding binding;
    private TextView userText;
    private TextView emailText;
    private FirebaseAuth mAuth;
    private ThemeManager themeManager;

    //TODO: Move to enum
    public final static int TEXT1 = 35;
    public final static int TEXT2 = 30;
    public final static int TEXT3 = 25;
    public final static int TEXT4 = 20;
    private CircleImageView profileImage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mAuth = FirebaseAuth.getInstance();
        FirebaseUser firebaseUser = mAuth.getCurrentUser();
        themeManager = new ThemeManager(getBaseContext());

        // Set navigation drawer, layout, and toolbar
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        setSupportActionBar(binding.appBarMain.toolbar);
        DrawerLayout drawer = binding.drawerLayout;
        NavigationView navigationView = binding.navView;

        // Set up drawer pages
        drawerOptions = new AppBarConfiguration.Builder(R.id.nav_workouts, R.id.nav_exercises,
                R.id.nav_history, R.id.nav_connections, R.id.nav_settings)
                .setOpenableLayout(drawer)
                .build();

        // Set up navigation functionality
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_content_main);
        NavigationUI.setupWithNavController(navigationView, navController);

        // Set up toolbar
        Toolbar toolbar = binding.appBarMain.toolbar;
        themeManager.setToolbarTheme(toolbar, R.drawable.ic_hamburger);
        setSupportActionBar(toolbar);

        // Start by navigating to WorkoutsFragment
        navController.navigate(R.id.nav_workouts);

        // Fetch user information
        userText = binding.navView.getHeaderView(0).findViewById(R.id.userTextView);
        emailText = binding.navView.getHeaderView(0).findViewById(R.id.emailTextView);
        profileImage = binding.navView.getHeaderView(0).findViewById(R.id.image_profile);

        // Set theme for drawer header
        themeManager.setDrawerHeaderTheme(binding.navView.getHeaderView(0));

        //TODO: Should display name not User's ID
        if (firebaseUser != null) {
            userText.setText(firebaseUser.getDisplayName());
            emailText.setText(firebaseUser.getEmail());
            // Load and display the user's profile image if it exists
            loadProfileImage();
        }

        //TODO: navigate to ProfileActivity.java
        ImageView imageProfile = binding.navView.getHeaderView(0).findViewById(R.id.image_profile);
        imageProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Start ProfileActivity
                Intent intent = new Intent(MainActivity.this, ProfileActivity.class);
                startActivity(intent);
            }
        });

        // Set the title based on the selected destination
        navController.addOnDestinationChangedListener((controller, destination, arguments) -> {
            toolbar.setTitle(destination.getLabel());
        });
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.settings_layout, menu);
        return true;
    }

    @Override
    public boolean onSupportNavigateUp() {
        return NavigationUI.navigateUp(Navigation.findNavController(this,
                R.id.nav_host_fragment_content_main), drawerOptions) || super.onSupportNavigateUp();
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        ThemeManager themeManager1 = new ThemeManager(getBaseContext());
        themeManager1.configureOverFlowText(menu, R.id.action_settings);
        return super.onPrepareOptionsMenu(menu);
    }

    /**
     * Method to load and display the user's profile image in the drawer header if it exists.
     */
    private void loadProfileImage() {
        Glide.with(MainActivity.this)
                .load(Objects.requireNonNull(mAuth.getCurrentUser()).getPhotoUrl())
                .into(profileImage);

    }

    @Override
    protected void onResume() {
        super.onResume();
        // Update the profile image in case it changed
        loadProfileImage();
    }

}