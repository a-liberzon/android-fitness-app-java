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

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link SignUpFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link SignUpFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class SignUpFragment extends Fragment {

    EditText emailEditText;
    EditText passEditText;
    EditText confirmPassEditText;
    EditText fullNameEditText;
    Button loginButton;
    Button signupButton;
    private OnFragmentInteractionListener listener;

    public SignUpFragment() {
        // Required empty public constructor
    }

    // TODO: Rename and change types and number of parameters
    public static SignUpFragment newInstance() {
        return new SignUpFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_signup, container, false);

        emailEditText = view.findViewById(R.id.emailEditText);
        passEditText = view.findViewById(R.id.createPasswordEditText);
        confirmPassEditText = view.findViewById(R.id.confirmPasswordEditText);
        //fullNameEditText = view.findViewById(R.id.full_name_edit_text);
        loginButton = view.findViewById(R.id.loginButton);
        signupButton = view.findViewById(R.id.signUpButton);

        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listener.onNavigateToLoginClicked();
            }
        });

        signupButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email = emailEditText.getText().toString().trim();
                String pass = passEditText.getText().toString().trim();
                String confirmPass = confirmPassEditText.getText().toString().trim();
                //String fullName = fullNameEditText.getText().toString();
                User user;
                if (!email.equals("") && !pass.equals("") && pass.toString().equals(confirmPass.toString())) {
                    user = new User(email, pass);
                    //user.setEmail(email);
                    //user.setPassword(pass);
                    //user.setName(fullName);
                    listener.onSignupClicked(user);
                } else if (!pass.equals(confirmPass)) {
                    Toast.makeText(getContext(), "Passwords do not match!", Toast.LENGTH_LONG).show();
                } else {
                    Toast.makeText(getContext(), "Invalid email or password!", Toast.LENGTH_LONG).show();
                }
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
        void onSignupClicked(User user);

        void onNavigateToLoginClicked();
    }
}
