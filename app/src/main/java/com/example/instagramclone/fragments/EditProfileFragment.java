package com.example.instagramclone.fragments;

import static android.app.Activity.RESULT_OK;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import com.bumptech.glide.Glide;
import com.example.instagramclone.Models.User;
import com.example.instagramclone.R;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.List;

public class EditProfileFragment extends Fragment {

    private static final String TAG = "EditProfileFragment";
    private static final int RESULT_LOAD_IMG = 402;
    private ImageView ivProfilePicture;
    private EditText etFullname;
    private EditText etBio;
    private Button btnSubmit;

    private String full_name = "";
    private String bio = "";

    Bitmap selectedImage;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_edit_profile, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        final FragmentManager fragmentManager = ((AppCompatActivity) getContext()).getSupportFragmentManager();

        ivProfilePicture = view.findViewById(R.id.ivProfilePicture);
        etFullname = view.findViewById(R.id.etFullname);
        etBio = view.findViewById(R.id.etBio);
        btnSubmit = view.findViewById(R.id.submitBtn);

        queryUser();

        btnSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                full_name = etFullname.getText().toString();
                bio = etBio.getText().toString();
                updateUserInfo(full_name, bio, fragmentManager);
            }
        });

        ivProfilePicture.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "Clicked on image");
                Intent photoPickerIntent = new Intent(Intent.ACTION_PICK);
                photoPickerIntent.setType("image/*");
                startActivityForResult(photoPickerIntent, RESULT_LOAD_IMG);
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
                        .into(ivProfilePicture);

            } catch (FileNotFoundException e) {
                e.printStackTrace();
                Toast.makeText(getContext(), "Something went wrong", Toast.LENGTH_LONG).show();
            }

        }else {
            Toast.makeText(getContext(), "You haven't picked Image",Toast.LENGTH_LONG).show();
        }
    }

    private void updateUserInfo(String full_name, String bio, FragmentManager fragmentManager) {
        ParseQuery<User> query = ParseQuery.getQuery(User.class);
        query.whereEqualTo(User.KEY_USERNAME, ParseUser.getCurrentUser().getUsername());

        Log.d(TAG, "In user update");

        query.findInBackground(new FindCallback<User>() {
            @Override
            public void done(List<User> user, ParseException e) {

                if (e != null) {
                    Log.e(TAG, "Issue with updating posts", e);
                    return;
                }

                user.get(0).setFullName(full_name);
                user.get(0).setBio(bio);

                if(selectedImage == null) {
                    user.get(0).saveInBackground(new SaveCallback() {
                        @Override
                        public void done(ParseException e) {
                            fragmentManager.popBackStack();
                        }
                    });
                }
                else {
                    ByteArrayOutputStream stream = new ByteArrayOutputStream();
                    selectedImage.compress(Bitmap.CompressFormat.PNG, 3, stream);
                    byte[] image = stream.toByteArray();

                    final ParseFile file = new ParseFile("petImage.png", image);

                    file.saveInBackground(new SaveCallback() {
                        @Override
                        public void done(ParseException e) {
                            if (e == null) {
                                user.get(0).setProfileImage(file);

                                user.get(0).saveInBackground(new SaveCallback() {
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
            }
        });
    }

    protected void queryUser() {
        ParseQuery<User> query = ParseQuery.getQuery(User.class);
        query.whereEqualTo(User.KEY_USERNAME, ParseUser.getCurrentUser().getUsername());

        query.findInBackground(new FindCallback<User>() {
            @Override
            public void done(List<User> user, ParseException e) {
                Log.i(TAG, "Query user");
                if (e != null) {
                    Log.e(TAG, "Issue with getting posts", e);
                    return;
                }
                etFullname.setText(user.get(0).getFullname());
                etBio.setText(user.get(0).getBio());

                Glide.with(getContext())
                        .load(user.get(0).getProfileImage().getUrl())
                        .centerCrop()
                        .circleCrop()
                        .into(ivProfilePicture);
            }
        });
    }

}