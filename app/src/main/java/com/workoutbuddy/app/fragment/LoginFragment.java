package com.workoutbuddy.app.fragment;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.workoutbuddy.app.R;
import com.workoutbuddy.app.model.User;

public class LoginFragment extends Fragment {

    EditText emailEditText;
    EditText passEditText;
    Button loginButton;
    Button signupButton;

    private OnFragmentInteractionListener listener;

    public LoginFragment() {
        // Required empty public constructor
    }

    // TODO: Rename and change types and number of parameters
    public static LoginFragment newInstance() {
        return new LoginFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_login, container, false);
        emailEditText = view.findViewById(R.id.emailEditText);
        passEditText = view.findViewById(R.id.passwordEditText);
        loginButton = view.findViewById(R.id.loginButton);
        signupButton = view.findViewById(R.id.signUpButton);

        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                User user = new User();
                String email = emailEditText.getText().toString().trim();
                String pass = passEditText.getText().toString().trim();
                if (!email.equals("") && !pass.equals("")) {
                    user.setEmail(email);
                    user.setPassword(pass);
                    listener.onLoginClicked(user);
                } else {
                    Toast.makeText(getContext(), "invalid email or password", Toast.LENGTH_LONG).show();
                }
            }
        });

        signupButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listener.onNavigationToSignupClicked();
            }
        });
        return view;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            listener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        listener = null;
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        // void onFragmentInteraction(Uri uri);
        void onLoginClicked(User user);

        void onNavigationToSignupClicked();

    }
}
