package com.example.instagramclone.Adapters;

import android.content.Context;
import android.text.format.DateUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.instagramclone.Models.Post;
import com.example.instagramclone.R;
import com.example.instagramclone.fragments.UserProfileFragment;
import com.parse.ParseFile;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Locale;

public class PostAdapter extends RecyclerView.Adapter<PostAdapter.ViewHolder> {

    private Context context;
    private List<Post> posts;

    public PostAdapter(Context context, List<Post> post) {
        this.context = context;
        this.posts = post;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_post, parent, false);
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

    class ViewHolder extends  RecyclerView.ViewHolder {

        private TextView tvUsername;
        private ImageView ivImage;
        private TextView tvDescription;
        private ImageView ivPostProfileImg;
        private TextView tvTime;
        private LinearLayout topBar;
        final FragmentManager fragmentManager = ((AppCompatActivity) context).getSupportFragmentManager();

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvUsername = itemView.findViewById(R.id.tvUsername);
            ivImage = itemView.findViewById(R.id.ivImage);
            tvDescription = itemView.findViewById(R.id.tvDescription);
            ivPostProfileImg = itemView.findViewById(R.id.ivPostProfileImg);
            tvTime = itemView.findViewById(R.id.tvTime);
            topBar = itemView.findViewById(R.id.topBar);
        }

        public void bind(Post post) {
            // bind the data to view elements
            tvDescription.setText(post.getDescription());
            tvUsername.setText(post.getUser().getUsername());
            tvTime.setText(getTimestamp(post.getCreatedAt().toString()));

            ParseFile profileImage = post.getUser().getParseFile("profilePicture");

            ParseFile image = post.getImage();

            if(image != null) {
                Glide.with(context)
                        .load(image.getUrl())
                        .into(ivImage);
            }

            if(profileImage != null) {
                Glide.with(context)
                        .load(profileImage.getUrl())
                        .centerCrop()
                        .circleCrop()
                        .into(ivPostProfileImg);
            }

            topBar.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Fragment fragment = new UserProfileFragment(post.getUser());
                    fragmentManager.beginTransaction().replace(R.id.flContainer, fragment).addToBackStack(null).commit();
                }
            });
        }
    }

    private String getTimestamp(String rawJsonDate) {
        String twitterFormat = "EEE MMM dd HH:mm:ss ZZZ yyyy";
        SimpleDateFormat sf = new SimpleDateFormat(twitterFormat, Locale.ENGLISH);
        sf.setLenient(true);

        String relativeDate = "";
        try {
            long dateMillis = sf.parse(rawJsonDate).getTime();
            relativeDate = DateUtils.getRelativeTimeSpanString(dateMillis,
                    System.currentTimeMillis(), DateUtils.SECOND_IN_MILLIS).toString();
        } catch (ParseException e) {
            e.printStackTrace();
        }

        return relativeDate;
    }
}
