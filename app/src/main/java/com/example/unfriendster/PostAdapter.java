package com.example.unfriendster;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.unfriendster.databinding.PostItemBinding;

import java.util.List;

public class PostAdapter extends RecyclerView.Adapter<PostAdapter.PostViewHolder> {

    private final List<Post> posts;

    public PostAdapter(List<Post> posts) {
        this.posts = posts;
    }

    static class PostViewHolder extends RecyclerView.ViewHolder {
        private final PostItemBinding binding;

        PostViewHolder(PostItemBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }
    }

    @NonNull
    @Override
    public PostViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        PostItemBinding binding = PostItemBinding.inflate(inflater, parent, false);
        return new PostViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull PostViewHolder holder, int position) {
        Post currentPost = posts.get(position);
        holder.binding.postTitleTextView.setText(currentPost.getTitle());
        holder.binding.postContentTextView.setText(currentPost.getContent());

        // Load image using Glide
        String photoUrl = currentPost.getPhotoUrl();
        if (photoUrl != null && !photoUrl.isEmpty()) {
            Glide.with(holder.itemView.getContext())
                    .load(photoUrl)
                    .placeholder(R.drawable.blankpfp)
                    .error(R.drawable.blankpfp) // Replace with your actual error image
                    .into(holder.binding.postImageView);
        } else {
            // Handle cases where there's no image URL
            Glide.with(holder.itemView.getContext())
                    .load(R.drawable.blankpfp) // Replace with your placeholder image
                    .into(holder.binding.postImageView);
        }

        // Set click listener for the item
        holder.itemView.setOnClickListener(v -> {
            if (listener != null) {
                int pos = holder.getAdapterPosition();
                if (pos != RecyclerView.NO_POSITION) {
                    listener.onItemClick(posts.get(pos));
                }
            }
        });
    }
    @Override
    public int getItemCount() {
        return posts.size();
    }

    public interface OnItemClickListener {
        void onItemClick(Post post);
    }

    // In your PostAdapter
    private OnItemClickListener listener;

    public void setOnItemClickListener(OnItemClickListener listener) {
        this.listener = listener;
    }

}