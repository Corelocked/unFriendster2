package com.example.unfriendster;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import java.text.CollationElementIterator;
import java.util.List;

public class PostAdapter extends RecyclerView.Adapter<PostAdapter.PostViewHolder> {

    private List<Post> posts;

    public PostAdapter(List<Post> posts) {
        this.posts = posts;
    }

    static class PostViewHolder extends RecyclerView.ViewHolder {
        TextView titleTextView;
        TextView contentTextView;
        ImageView postImageView; // If you added this for images

        PostViewHolder(View itemView) {
            super(itemView);
            titleTextView = itemView.findViewById(R.id.postTitleTextView); // Make sure this ID matches your layout
            contentTextView = itemView.findViewById(R.id.postContentTextView); // Make sure this ID matches your layout
            postImageView = itemView.findViewById(R.id.postImageView); // If you have an ImageView for images
        }
    }

    @NonNull
    @Override
    public PostViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.post_item, parent, false); // Use your post item layout
        return new PostViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull PostViewHolder holder, int position) {
        Post currentPost = posts.get(position);
        holder.titleTextView.setText(currentPost.getTitle());
        holder.contentTextView.setText(currentPost.getContent());

        // Load image using Glide (or Picasso)
        String photoUrl = currentPost.getPhotoUrl();
        if (photoUrl != null && !photoUrl.isEmpty()) {
            Glide.with(holder.itemView.getContext())
                    .load(photoUrl)
                    .into(holder.postImageView);
        }else {
            // Handle cases where there's no image URL (e.g., set a placeholder image)
            // Glide.with(holder.itemView.getContext()).load(R.drawable.placeholder).into(holder.postImageView);

        }
    }
    @Override
    public int getItemCount() {
        return posts.size();
    }
}