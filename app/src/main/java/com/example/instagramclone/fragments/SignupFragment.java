package com.example.instagramclone.fragments;

import static android.app.Activity.RESULT_OK;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
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
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.instagramclone.Models.User;
import com.example.instagramclone.R;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.SaveCallback;
import com.parse.SignUpCallback;

import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;

public class SignupFragment extends Fragment {

    private final String TAG = "SignupFragment";
    private static final int RESULT_LOAD_IMG = 402;
    private ImageView ivAddPicture;
    private EditText etAddUsername;
    private EditText etAddFullname;
    private EditText etAddBio;
    private EditText etAddPassword;
    private EditText etConfirmPassword;
    private ProgressBar signupProgressBar;
    private TextView signupTextView;

    private Bitmap selectedImage;

    private String username;
    private String fullName;
    private String bio;
    private String password;
    private String confirmPassword;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_signup, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        ivAddPicture = view.findViewById(R.id.ivAddPicture);
        etAddUsername = view.findViewById(R.id.etAddUsername);
        etAddFullname = view.findViewById(R.id.etAddUsername);
        etAddBio = view.findViewById(R.id.etAddBio);
        etAddPassword = view.findViewById(R.id.etAddPassword);
        etConfirmPassword = view.findViewById(R.id.etConfirmPassword);

        signupProgressBar = view.findViewById(R.id.signup_progressBar);
        signupTextView = view.findViewById(R.id.signup_textView);

        final FragmentManager fragmentManager = ((AppCompatActivity) getContext()).getSupportFragmentManager();

        Glide.with(getContext())
                .load(R.drawable.default_image)
                .centerCrop()
                .circleCrop()
                .into(ivAddPicture);

        ivAddPicture.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "Clicked on image");
                Intent photoPickerIntent = new Intent(Intent.ACTION_PICK);
                photoPickerIntent.setType("image/*");
                startActivityForResult(photoPickerIntent, RESULT_LOAD_IMG);
            }
        });

        view.findViewById(R.id.btnSignup).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                username = etAddUsername.getText().toString();
                fullName = etAddFullname.getText().toString();
                bio = etAddBio.getText().toString();
                password = etAddPassword.getText().toString();
                confirmPassword = etConfirmPassword.getText().toString();

                if(username.isEmpty() || fullName.isEmpty() || bio.isEmpty() || password.isEmpty() || confirmPassword.isEmpty()) {
                    Toast.makeText(getContext(), "Please fill all the information", Toast.LENGTH_SHORT).show();
                    return;
                }
                if(!password.equals(confirmPassword)) {
                    Toast.makeText(getContext(), "Your password does not match", Toast.LENGTH_SHORT).show();
                    return;
                }
                addUser(username, fullName, bio, password, fragmentManager);
                signupTextView.setVisibility(View.GONE);
                signupProgressBar.setVisibility(View.VISIBLE);
            }
        });

    }

    private void addUser(String username, String fullName, String bio, String password, FragmentManager fragmentManager) {

        User user = new User();
        user.setUsername(username);
        user.setFullName(fullName);
        user.setBio(bio);
        user.setPassword(password);


        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        selectedImage.compress(Bitmap.CompressFormat.PNG, 3, stream);
        byte[] image = stream.toByteArray();

        final ParseFile file = new ParseFile("petImage.png", image);

        file.saveInBackground(new SaveCallback() {
            @Override
            public void done(ParseException e) {
                if (e == null) {
                    user.setProfileImage(file);

                    user.signUpInBackground(new SignUpCallback() {
                        @Override
                        public void done(ParseException e) {
                            if (e != null) {
                                Log.e(TAG, "Error while saving", e);
                                Toast.makeText(getContext(), "Error while saving!", Toast.LENGTH_SHORT).show();
                            }
                            Log.i(TAG, "Post save was successful");
                            fragmentManager.popBackStack();
                        }
                    });
                }
            }
        });
    }

    @Override
    public void onActivityResult(int reqCode, int resultCode, Intent data) {
        super.onActivityResult(reqCode, resultCode, data);

        if (resultCode == RESULT_OK && reqCode == RESULT_LOAD_IMG) {
            Log.d(TAG, "Result Code Inside: " + reqCode);
            try {
                final Uri imageUri = data.getData();
                final InputStream imageStream = ((AppCompatActivity) getContext()).getContentResolver().openInputStream(imageUri);
                selectedImage = BitmapFactory.decodeStream(imageStream);

                Glide.with(getContext())
                        .asBitmap()
                        .load(selectedImage)
                        .centerCrop()
                        .circleCrop()
                        .into(ivAddPicture);

            } catch (FileNotFoundException e) {
                e.printStackTrace();
                Toast.makeText(getContext(), "Something went wrong", Toast.LENGTH_LONG).show();
            }

        }else {
            Toast.makeText(getContext(), "You haven't picked Image",Toast.LENGTH_LONG).show();
        }
    }
}