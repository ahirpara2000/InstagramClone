package com.example.instagramclone.fragments;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.instagramclone.LoginActivity;
import com.example.instagramclone.MainActivity;
import com.example.instagramclone.R;
import com.parse.LogInCallback;
import com.parse.ParseException;
import com.parse.ParseUser;
import com.parse.SignUpCallback;


public class LoginFragment extends Fragment {

    private static final String TAG = "LoginFragment";
    private EditText etUsername;
    private EditText etPassword;

    private ProgressBar loginProgressBar;
    private TextView loginTextView;
    private ProgressBar signupProgressBar;
    private TextView signupTextView;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_signin, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        final FragmentManager fragmentManager = ((AppCompatActivity) getContext()).getSupportFragmentManager();

        if(ParseUser.getCurrentUser() != null) {
            goMainActivity();
        }

        etUsername = view.findViewById(R.id.etUsername);
        etPassword = view.findViewById(R.id.etPassword);

        loginProgressBar = view.findViewById(R.id.login_progressBar);
        loginTextView = view.findViewById(R.id.login_textView);
        signupProgressBar = view.findViewById(R.id.signup_progressBar);
        signupTextView = view.findViewById(R.id.signup_textView);

        view.findViewById(R.id.btnLogin).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.i(TAG, "onClick login button");
                String username = etUsername.getText().toString();
                String password = etPassword.getText().toString();
                loginUser(username, password);
                loginProgressBar.setVisibility(View.VISIBLE);
                loginTextView.setVisibility(View.GONE);
            }
        });

        view.findViewById(R.id.btnSignup).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Fragment fragment = new SignupFragment();
                fragmentManager.beginTransaction().replace(R.id.flLoginContainer, fragment).addToBackStack(null).commit();
            }
        });
    }

    private void signupUser(String username, String password) {
        Log.i(TAG, "Attempting to signup: " + username + " " + password);

        ParseUser user = new ParseUser();

        user.setUsername(username);
        user.setPassword(password);

        user.signUpInBackground(new SignUpCallback() {
            @Override
            public void done(ParseException e) {
                if(e != null) {
                    signupProgressBar.setVisibility(View.GONE);
                    signupTextView.setVisibility(View.VISIBLE);
                    Log.e(TAG, "Issue with login", e);
                    Toast.makeText(getContext(), "Invalid username or password", Toast.LENGTH_SHORT).show();
                    return;
                }
                goMainActivity();
            }
        });
    }

    private void loginUser(String username, String password) {
        Log.i(TAG, "Attempting to login: " + username + " " + password);

        ParseUser.logInInBackground(username, password, new LogInCallback() {
            @Override
            public void done(ParseUser user, ParseException e) {
                if(e != null) {
                    loginProgressBar.setVisibility(View.GONE);
                    loginTextView.setVisibility(View.VISIBLE);
                    // Improve error handling here
                    Log.e(TAG, "Issue with login", e);
                    Toast.makeText(getContext(), "Invalid username or password", Toast.LENGTH_SHORT).show();
                    return;
                }
                goMainActivity();
            }
        });
    }

    private void goMainActivity() {
        Intent i = new Intent(getContext(), MainActivity.class);
        startActivity(i);
        ((AppCompatActivity) getContext()).finish();
    }
}