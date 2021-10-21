package com.example.instagramclone.fragments;

import android.util.Log;

import com.bumptech.glide.Glide;
import com.example.instagramclone.Models.Post;
import com.example.instagramclone.Models.User;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import java.util.List;

public class UserProfileFragment extends ProfileFragment{

    private static final String TAG = "UserProfileFragment";
    private ParseUser selectedUser;

    public UserProfileFragment(ParseUser selectedUser) {
        super();
        this.selectedUser = selectedUser;
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
