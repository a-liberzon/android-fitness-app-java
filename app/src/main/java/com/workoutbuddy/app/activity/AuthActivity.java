package com.workoutbuddy.app.activity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.workoutbuddy.app.R;
import com.workoutbuddy.app.fragment.LoginFragment;
import com.workoutbuddy.app.fragment.SignUpFragment;
import com.workoutbuddy.app.fragment.WelcomeFragment;
import com.workoutbuddy.app.model.User;

public class AuthActivity extends AppCompatActivity implements LoginFragment.OnFragmentInteractionListener,
        SignUpFragment.OnFragmentInteractionListener, WelcomeFragment.OnFragmentInteractionListener {

    private WelcomeFragment welcomeFragment;
    private LoginFragment loginFragment;
    private SignUpFragment signUpFragment;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_auth);

        welcomeFragment = WelcomeFragment.newInstance();
        loginFragment = LoginFragment.newInstance();
        signUpFragment = SignUpFragment.newInstance();
        onNavigateToStartUp();

        mAuth = FirebaseAuth.getInstance();
        //updateUI(currentUser);
    }

    @Override
    public void onLoginClicked(User user) {
        signIn(user.getEmail(), user.getPassword());
    }

    private void loginAndNavigate(User user) {
        signIn(user.getEmail(), user.getPassword());
    }

    private void signIn(String email, String password) {
        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            FirebaseUser user = mAuth.getCurrentUser();
                            Toast.makeText(AuthActivity.this, "Authentication success.",
                                    Toast.LENGTH_SHORT).show();
                            navigateToMainActivity();
                        } else {
                            Toast.makeText(AuthActivity.this, "Authentication failed.",
                                    Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    private void navigateToMainActivity() {
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
        finish();
    }

    @Override
    public void onNavigationToSignupClicked() {
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.container, signUpFragment)
                .commitNow();
    }

    @Override
    public void onSignupClicked(User user) {
        createAccount(user.getEmail(), user.getPassword());
    }

    private void createAccount(String email, String password) {
        final User user = new User();
        user.setEmail(email);
        user.setPassword(password);
        mAuth.createUserWithEmailAndPassword(user.getEmail(), user.getPassword())
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            Log.d("TAG", "Create user with password success");
                            FirebaseUser firebaseUser = mAuth.getCurrentUser();
                            loginAndNavigate(user);
//                            updateUI(user);
                        } else {
                            Log.w("TAG", "Create user with password failure", task.getException());
                            Toast.makeText(AuthActivity.this, "Authentication failed.",
                                    Toast.LENGTH_SHORT).show();
//                            updateUI(null);
                        }
                    }
                });
    }

    @Override
    public void onNavigateToLoginClicked() {
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.container, loginFragment)
                .commitNow();
    }

    private void onNavigateToStartUp() {
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.container, welcomeFragment)
                .commitNow();
    }
}
