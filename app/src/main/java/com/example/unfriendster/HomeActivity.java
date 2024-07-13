package com.example.unfriendster;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DividerItemDecoration;
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
    public List<Post> posts = new ArrayList<>();

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
        postRecyclerView.addItemDecoration(new DividerItemDecoration(this, DividerItemDecoration.VERTICAL));
        // Set layout manager

        posts = new ArrayList<>();
        adapter = new PostAdapter(posts); // You're passing the local list to the adapter
        fetchPostsFromFirebase();
        postRecyclerView.setAdapter(adapter);

        newpost_btn.setOnClickListener(view -> {
            Intent intent = new Intent(HomeActivity.this, CreatePostActivity.class);
            startActivity(intent);
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
                posts.clear();
                for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                    Post post = postSnapshot.getValue(Post.class);
                    if (post != null) {
                        posts.add(post);
                        Log.d("HomeActivity", "Fetched Post: " + post.getTitle());
                    }
                }
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.e("HomeActivity", "Error fetching posts: ", databaseError.toException());
                Toast.makeText(HomeActivity.this, "Failed to load posts", Toast.LENGTH_SHORT).show();
                // Or show a Snackbar, a dialog, or implement a retry mechanism
            }
        });
    }
}