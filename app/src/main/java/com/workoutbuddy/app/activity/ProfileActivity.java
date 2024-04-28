package com.workoutbuddy.app.activity;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.workoutbuddy.app.R;
import com.workoutbuddy.app.model.ThemeManager;
import com.workoutbuddy.app.model.Utilities;

import org.checkerframework.checker.nullness.qual.NonNull;

import de.hdodenhof.circleimageview.CircleImageView;

public class ProfileActivity extends AppCompatActivity {

    // Themes
    private int contrastColor;
    private ThemeManager themeManager;
    private Utilities utils;
    private CircleImageView profilePicture;
    TextView userNameTextView;
    ImageButton editNameButton;
    TextView userEmailTextView;
    ImageButton editEmailButton;
    // request code
    private final int REQUEST_IMAGE_UPLOAD = 1;

    // instance for firebase storage and StorageReference
    FirebaseStorage storage;
    StorageReference storageRef;
    FirebaseUser firebaseUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Set up theme
        setContentView(R.layout.activity_profile);
        themeManager = new ThemeManager(getBaseContext());
        utils = new Utilities(ProfileActivity.this);
        contrastColor = themeManager.getContrastColor();

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        // Customize the toolbar as needed
        if (getSupportActionBar() != null) {
            // Set the title
            getSupportActionBar().setTitle("Profile");
            // Enable the Up button for navigation
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        // Initialize Firebase Storage
        storage = FirebaseStorage.getInstance();
        storageRef = storage.getReference();

        // Handle authentication
        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();

        // Set up the profile picture if any
        profilePicture = findViewById(R.id.profile_picture);

        if (firebaseUser.getPhotoUrl() != null) {
            processImage(firebaseUser.getPhotoUrl());
        }

        profilePicture.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                selectImage();
            }
        });

        // Set up the rest of the UI components
        setupProfileUI();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void selectImage() {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("image/*");
        startActivityForResult(intent, REQUEST_IMAGE_UPLOAD);
    }

    /**
     * Method to handle the result of the image upload.
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_IMAGE_UPLOAD && resultCode == RESULT_OK && data != null) {
            Uri filePath = data.getData();
            uploadImage(filePath);
        }
    }

    /**
     * Method to process the selected image.
     */
    private void processImage(Uri uri) {
        Glide.with(ProfileActivity.this)
                .load(uri)
                .into(profilePicture);

    }

    /**
     * Method to upload the image to cloud storage
     */
    private void uploadImage(Uri filePath) {
        if (filePath != null) {
            // Code for showing progressDialog while uploading
            ProgressDialog progressDialog = new ProgressDialog(this);
            progressDialog.setTitle("Uploading...");
            progressDialog.show();

            // Get the user's UID to create the appropriate folder
            String userId = firebaseUser.getUid();

            // Defining the child of storageReference with the new folder structure
            StorageReference ref = storageRef.child("userdata/" + userId + "/profile_pic.jpg");

            // Adding listeners on upload or failure of the image
            ref.putFile(filePath)
                    .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            // Image uploaded successfully
                            // Dismiss dialog
                            progressDialog.dismiss();
                            updateFirebaseUserURI(ref);
                            Toast.makeText(ProfileActivity.this, "Image Uploaded!", Toast.LENGTH_SHORT).show();
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            // Error, Image not uploaded
                            progressDialog.dismiss();
                            Toast.makeText(ProfileActivity.this, "Failed " + e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    })
                    .addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                        // Progress Listener for loading percentage on the dialog box
                        @Override
                        public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
                            double progress = (100.0 * taskSnapshot.getBytesTransferred() / taskSnapshot.getTotalByteCount());
                            progressDialog.setMessage("Uploaded " + (int) progress + "%");
                        }
                    });
        }
    }

    private void updateFirebaseUserURI(StorageReference storageReference) {
        // Get the download URL of the uploaded image
        storageReference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri downloadUri) {
                // Update the firebase user URI with the new download URL
                if (firebaseUser != null) {
                    UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder()
                            .setPhotoUri(downloadUri)
                            .build();

                    firebaseUser.updateProfile(profileUpdates)
                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if (task.isSuccessful()) {
                                        // Profile updated successfully
                                        // Now you can display the new profile picture using the downloadUri
                                        processImage(downloadUri);

                                        Toast.makeText(ProfileActivity.this, "Image Uploaded!", Toast.LENGTH_SHORT).show();
                                    } else {
                                        // Failed to update the profile picture URI
                                        Toast.makeText(ProfileActivity.this, "Failed to update profile picture.", Toast.LENGTH_SHORT).show();
                                    }
                                }
                            });
                }
            }
        });
    }

    private void showAlertDialog(String title, String message, TextView textView) {
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
        alertDialogBuilder.setTitle(title);
        alertDialogBuilder.setMessage(message);
        alertDialogBuilder.setCancelable(false);

        // Set up the custom layout for the AlertDialog
        View view = getLayoutInflater().inflate(R.layout.dialog_input_layout, null);
        EditText inputEditText = view.findViewById(R.id.dialog_input_text);
        alertDialogBuilder.setView(view);

        alertDialogBuilder.setPositiveButton("OK", null);
        alertDialogBuilder.setNegativeButton("Cancel", null);

        // Configure listeners
        AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.setOnShowListener(dialog -> {
            // Configure buttons
            Button positiveButton = alertDialog.getButton(AlertDialog.BUTTON_POSITIVE);
            Button negativeButton = alertDialog.getButton(AlertDialog.BUTTON_NEGATIVE);
            themeManager.configureDialogButtons(positiveButton, negativeButton);


            // Set click listener for the OK button
            positiveButton.setOnClickListener(v1 -> {
                String inputText = inputEditText.getText().toString().trim();
                textView.setText(inputText);

                // Update Firebase user display name or email based on which TextView is being updated
                if (textView == userNameTextView) {
                    FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                    if (user != null) {
                        UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder()
                                .setDisplayName(inputText)
                                .build();

                        user.updateProfile(profileUpdates)
                                .addOnCompleteListener(task -> {
                                    if (task.isSuccessful()) {
                                        // User display name updated successfully
                                        Toast.makeText(ProfileActivity.this, "User name updated!", Toast.LENGTH_SHORT).show();
                                    } else {
                                        // Failed to update user display name
                                        Toast.makeText(ProfileActivity.this, "Failed to update user name.", Toast.LENGTH_SHORT).show();
                                    }
                                });
                    }
                } else if (textView == userEmailTextView) {
                    FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                    if (user != null) {
                        user.updateEmail(inputText)
                                .addOnCompleteListener(task -> {
                                    if (task.isSuccessful()) {
                                        // User email updated successfully
                                        Toast.makeText(ProfileActivity.this, "Email updated!", Toast.LENGTH_SHORT).show();
                                    } else {
                                        // Failed to update user email
                                        Toast.makeText(ProfileActivity.this, "Failed to update email.", Toast.LENGTH_SHORT).show();
                                    }
                                });
                    }
                }
                alertDialog.dismiss();
            });

            // Set click listener for the Cancel button
            negativeButton.setOnClickListener(v1 -> dialog.cancel());

        });
        alertDialog.show();
    }

    private void setupProfileUI() {
        userNameTextView = findViewById(R.id.user_name);
        editNameButton = findViewById(R.id.edit_name_button);
        userEmailTextView = findViewById(R.id.user_email);
        editEmailButton = findViewById(R.id.edit_email_button);

        userEmailTextView.setText(firebaseUser.getEmail());
        userNameTextView.setText(firebaseUser.getDisplayName());

        editNameButton.setOnClickListener(view -> {
            showAlertDialog("Enter new username", "", userNameTextView);
        });

        editEmailButton.setOnClickListener(view -> {
            showAlertDialog("Enter new email", "", userEmailTextView);
        });
    }


}
