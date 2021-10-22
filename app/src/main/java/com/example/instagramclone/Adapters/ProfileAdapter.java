package com.example.instagramclone.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.instagramclone.Models.Post;
import com.example.instagramclone.R;
import com.example.instagramclone.fragments.ProfileDetailFragment;
import com.parse.ParseFile;

import java.util.List;

public class ProfileAdapter extends RecyclerView.Adapter<ProfileAdapter.ViewHolder> {

    private Context context;
    private List<Post> posts;

    public ProfileAdapter(Context context, List<Post> posts) {
        this.context = context;
        this.posts = posts;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_profile_post, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Post post = posts.get(position);
        holder.bind(post);

    }

    @Override
    public int getItemCount() {
        return posts.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {

        private ImageView ivProfilePosts;
        final FragmentManager fragmentManager = ((AppCompatActivity) context).getSupportFragmentManager();

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            ivProfilePosts = itemView.findViewById(R.id.ivProfilePosts);
        }

        public void bind(Post post) {
            ParseFile image = post.getImage();

            if(image != null) {
                Glide.with(context)
                        .load(image.getUrl())
                        .into(ivProfilePosts);
            }

            ivProfilePosts.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Fragment fragment = new ProfileDetailFragment(post.getUser());
                    fragmentManager.beginTransaction().replace(R.id.flLoginContainer, fragment).addToBackStack(null).commit();
                }
            });
        }
    }
}
