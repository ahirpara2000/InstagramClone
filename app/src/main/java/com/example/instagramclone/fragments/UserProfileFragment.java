package com.example.instagramclone.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.GridLayoutManager;

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

public class UserProfileFragment extends ProfileFragment{

    private static final String TAG = "UserProfileFragment";
    private ParseUser selectedUser;
    private LinearLayout btnContainer;

    public UserProfileFragment(ParseUser selectedUser) {
        super();
        this.selectedUser = selectedUser;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        btnLogout = view.findViewById(R.id.btnLogout);
        rvUserPosts = view.findViewById(R.id.rvUserPosts);
        ivUserProfile = view.findViewById(R.id.ivUserProfile);
        tvProfileFullname = view.findViewById(R.id.tvProfileFullname);
        tvProfileUsername = view.findViewById(R.id.tvProfileUsername);
        tvBio = view.findViewById(R.id.tvBio);
        btnContainer = view.findViewById(R.id.btnContainer);

        allPosts = new ArrayList<>();
        adapter = new ProfileAdapter(getContext(), allPosts);

        GridLayoutManager gridLayoutManager = new GridLayoutManager(getContext(), 3, GridLayoutManager.VERTICAL, false);

        rvUserPosts.setAdapter(adapter);
        rvUserPosts.setLayoutManager(gridLayoutManager);

        queryPosts();
        queryUser();

        if(selectedUser.getUsername().equals(ParseUser.getCurrentUser().getUsername()))
        {
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
        }
        else
        {
            btnContainer.setVisibility(View.GONE);
        }

    }

    @Override
    protected void queryUser() {
        ParseQuery<User> query = ParseQuery.getQuery(User.class);
        query.whereEqualTo(User.KEY_USERNAME, selectedUser.getUsername());

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

    @Override
    protected void queryPosts() {
        ParseQuery<Post> query = ParseQuery.getQuery(Post.class);
        query.include(Post.KEY_USER);
        query.whereEqualTo(Post.KEY_USER, selectedUser);
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
}
