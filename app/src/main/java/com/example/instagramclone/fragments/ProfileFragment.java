package com.example.instagramclone.fragments;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.instagramclone.Adapters.ProfileAdapter;
import com.example.instagramclone.LoginActivity;
import com.example.instagramclone.Models.Post;
import com.example.instagramclone.Models.User;
import com.example.instagramclone.R;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import java.util.ArrayList;
import java.util.List;

public class ProfileFragment extends Fragment {

    private static final String TAG = "ProfileFragment";
    protected Button btnLogout;
    protected Button btnEdit;

    protected RecyclerView rvUserPosts;
    protected ProfileAdapter adapter;
    protected List<Post> allPosts;
    protected ImageView ivUserProfile;
    protected TextView tvProfileFullname;
    protected TextView tvProfileUsername;
    protected TextView tvBio;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_profile, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        final FragmentManager fragmentManager = ((AppCompatActivity) getContext()).getSupportFragmentManager();

        btnLogout = view.findViewById(R.id.btnLogout);
        btnEdit = view.findViewById(R.id.btnEdit);
        rvUserPosts = view.findViewById(R.id.rvUserPosts);
        ivUserProfile = view.findViewById(R.id.ivUserProfile);
        tvProfileFullname = view.findViewById(R.id.tvProfileFullname);
        tvProfileUsername = view.findViewById(R.id.tvProfileUsername);
        tvBio = view.findViewById(R.id.tvBio);

        allPosts = new ArrayList<>();
        adapter = new ProfileAdapter(getContext(), allPosts);

        GridLayoutManager gridLayoutManager = new GridLayoutManager(getContext(), 3, GridLayoutManager.VERTICAL, false);

        rvUserPosts.setAdapter(adapter);
        rvUserPosts.setLayoutManager(gridLayoutManager);

        queryPosts();
        queryUser();

        btnLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ParseUser.logOut();
                ParseUser currentUser = ParseUser.getCurrentUser(); // this will now be null

                Intent i = new Intent(getContext(), LoginActivity.class);
                startActivity(i);
                getActivity().finish();
            }
        });

        btnEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Fragment fragment = new EditProfileFragment();
                fragmentManager.beginTransaction().replace(R.id.flLoginContainer, fragment).addToBackStack(null).commit();
            }
        });
    }

    protected void queryPosts() {
        ParseQuery<Post> query = ParseQuery.getQuery(Post.class);
        query.include(Post.KEY_USER);
        query.whereEqualTo(Post.KEY_USER, ParseUser.getCurrentUser());
        query.setLimit(20);
        query.addDescendingOrder(Post.KEY_CREATED_KEY);

        query.findInBackground(new FindCallback<Post>() {
            @Override
            public void done(List<Post> posts, ParseException e) {
                if (e != null) {
                    Log.e(TAG, "Issue with getting posts", e);
                    return;
                }
                allPosts.addAll(posts);
                adapter.notifyDataSetChanged();
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
                tvProfileUsername.setText(user.get(0).getKeyUsername());
                tvProfileFullname.setText(user.get(0).getFullname());

                String bio = user.get(0).getBio();
                if(bio == null)
                    tvBio.setVisibility(View.GONE);
                else
                    tvBio.setText(user.get(0).getBio());

                Glide.with(getContext())
                        .load(user.get(0).getProfileImage().getUrl())
                        .centerCrop()
                        .circleCrop()
                        .into(ivUserProfile);
            }
        });
    }
}