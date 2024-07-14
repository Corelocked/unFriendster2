package com.example.unfriendster;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.unfriendster.databinding.PostItemBinding;

import java.util.List;
import java.util.Map;

public class PostAdapter extends RecyclerView.Adapter<PostAdapter.PostViewHolder> {

    private final List<Post> posts;
    private final Map<String, HomeActivity.AuthorDetails> authorDetailsMap;

    public PostAdapter(List<Post> posts, Map<String, HomeActivity.AuthorDetails> authorDetailsMap) {
        this.posts = posts;
        this.authorDetailsMap = authorDetailsMap;
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

        // Load post image using Glide
        String photoUrl = currentPost.getPhotoUrl();
        if (photoUrl != null && !photoUrl.isEmpty()) {
            Glide.with(holder.itemView.getContext())
                    .load(photoUrl)
                    .placeholder(R.drawable.blankpfp)
                    .error(R.drawable.blankpfp)
                    .fallback(R.drawable.blankpfp)
                    .into(holder.binding.postImageView);
        } else {
            Glide.with(holder.itemView.getContext())
                    .load(R.drawable.blankpfp)
                    .into(holder.binding.postImageView);
        }

        // Fetch Author Details (Optimized)
        String authorUid = currentPost.getAuthorUid();
        if (authorUid != null) {
            HomeActivity.AuthorDetails authorDetails = authorDetailsMap.get(authorUid);
            if (authorDetails != null) {
                // Use author details from the map
                holder.binding.postAuthorNameTextView.setText(authorDetails.name);
                holder.binding.postAuthorUsernameTextView.setText(authorDetails.username);

                Glide.with(holder.itemView.getContext())
                        .load(authorDetails.profileImageUrl)
                        .placeholder(R.drawable.blankpfp)
                        .error(R.drawable.blankpfp)
                        .into(holder.binding.profileImageView);
            } else {
                // Handle the case where author details are not yet available
                holder.binding.postAuthorNameTextView.setText("Loading...");
                holder.binding.postAuthorUsernameTextView.setText("");
                Glide.with(holder.itemView.getContext())
                        .load(R.drawable.blankpfp)
                        .into(holder.binding.profileImageView);
            }
        }

        // Set click listener (adapt as needed)
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
        Log.d("PostAdapter", "getItemCount: " + posts.size());
        return posts.size();
    }

    public interface OnItemClickListener {
        void onItemClick(Post post);
    }

    private OnItemClickListener listener;

    public void setOnItemClickListener(OnItemClickListener listener) {
        this.listener = listener;
    }
}