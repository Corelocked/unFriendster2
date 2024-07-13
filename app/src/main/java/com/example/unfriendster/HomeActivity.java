package com.example.unfriendster;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.google.firebase.database.DataSnapshot;import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import java.util.ArrayList;
import java.util.List;



public class HomeActivity extends AppCompatActivity {

    private PostAdapter adapter;
    private List<Post> posts = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.home_page);

        Button notificationButton = findViewById(R.id.notification_btn);
        Button profileButton = findViewById(R.id.profile_btn);
        Button homeButton = findViewById(R.id.home_btn);
        Button settingsButton = findViewById(R.id.settings_btn);
        Button logoButton = findViewById(R.id.logo_btn);
        Button newpost_btn = findViewById(R.id.newpost_btn);

        RecyclerView postRecyclerView = findViewById(R.id.postRecyclerView);
        postRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        // Set layout manager

        List<Post> posts = new ArrayList<>(); // This creates a new local 'posts' list
        posts.add(new Post());
        posts.add(new Post());
        adapter = new PostAdapter(posts); // You're passing the local list to the adapter
        postRecyclerView.setAdapter(adapter);

        fetchPostsFromFirebase();

        newpost_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(HomeActivity.this, CreatePostActivity.class);
                startActivity(intent);
            }
        });

        notificationButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(HomeActivity.this, NotificationActivity.class);
                startActivity(intent);
            }
        });

        profileButton.setOnClickListener(new View.OnClickListener() {

            public void onClick(View v) {
                Intent intent = new Intent(HomeActivity.this, ProfileActivity.class);
                startActivity(intent);
            }
        });

        homeButton.setOnClickListener(new View.OnClickListener() {

            public void onClick(View v) {
                Intent intent = new Intent(HomeActivity.this, HomeActivity.class);
                startActivity(intent);
            }
        });

        settingsButton.setOnClickListener(new View.OnClickListener() {

            public void onClick(View v) {
                Intent intent = new Intent(HomeActivity.this, SettingsActivity.class);
                startActivity(intent);
            }
        });

        logoButton.setOnClickListener(new View.OnClickListener() {

            public void onClick(View v) {
                Intent intent = new Intent(HomeActivity.this, HomeActivity.class);
                startActivity(intent);
            }
        });
    }
    private void fetchPostsFromFirebase() {
        DatabaseReference postsRef = FirebaseDatabase.getInstance().getReference("posts");
        postsRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                posts.clear(); // Clear existing posts
                for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                    Post post = postSnapshot.getValue(Post.class);
                    if (post != null) { // Check for null posts
                        posts.add(post);
                        Log.d("HomeActivity", "Fetched Post: " + post.getTitle());
                    }
                }
                adapter.notifyDataSetChanged(); // Update the adapter
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // Handle errors, e.g., log the error
            }
        });
    }
}