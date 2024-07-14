package com.example.unfriendster;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.Network;
import android.net.NetworkCapabilities;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class HomeActivity extends AppCompatActivity {

    private PostAdapter adapter;
    public List<Post> posts = new ArrayList<>();
    private final Map<String, AuthorDetails> authorDetailsMap = new HashMap<>();
    private DatabaseReference postsRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.home_page);
        FirebaseFirestore.getInstance().enableNetwork();

        Button notificationButton = findViewById(R.id.notification_btn);
        Button profileButton = findViewById(R.id.profile_btn);
        Button homeButton = findViewById(R.id.home_btn);
        Button settingsButton = findViewById(R.id.settings_btn);
        Button newpost_btn = findViewById(R.id.newpost_btn);

        RecyclerView postRecyclerView = findViewById(R.id.postRecyclerView);
        postRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        postRecyclerView.addItemDecoration(new DividerItemDecoration(this, DividerItemDecoration.VERTICAL));

        posts = new ArrayList<>();
        adapter = new PostAdapter(posts, authorDetailsMap);
        postRecyclerView.setAdapter(adapter);

        postsRef = FirebaseDatabase.getInstance().getReference("posts");
        listenForPostChanges();
        fetchPostsAndAuthorDetailsFromFirebase();

        newpost_btn.setOnClickListener(view -> {
            Intent intent = new Intent(HomeActivity.this, CreatePostActivity.class);
            startActivity(intent);});

        notificationButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(HomeActivity.this, NotificationActivity.class);
                startActivity(intent);
            }
        });

        profileButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(HomeActivity.this, ProfileActivity.class);
                startActivity(intent);
            }
        });

        homeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // You might not need this, as you're already in HomeActivity
                // Intent intent = new Intent(HomeActivity.this, HomeActivity.class);
                // startActivity(intent);
            }
        });

        settingsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(HomeActivity.this, SettingsActivity.class);
                startActivity(intent);
            }
        });

    }

    private void listenForPostChanges() {
        postsRef.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot snapshot, String previousChildName) {
                Post newPost = snapshot.getValue(Post.class);
                if (newPost != null) {
                    posts.add(newPost);
                    fetchAuthorDetailsForPost(newPost, () -> runOnUiThread(() -> adapter.notifyItemInserted(posts.size() - 1)));
                }
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot snapshot, String previousChildName) {
                Post changedPost = snapshot.getValue(Post.class);
                if (changedPost != null) {
                    int index = posts.indexOf(changedPost);
                    if (index != -1) {
                        posts.set(index, changedPost);
                        runOnUiThread(() -> adapter.notifyItemChanged(index));
                    }
                }
            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot snapshot) {
                Post removedPost = snapshot.getValue(Post.class);
                if (removedPost != null) {
                    int index = posts.indexOf(removedPost);
                    if (index != -1) {
                        posts.remove(index);
                        authorDetailsMap.remove(removedPost.getAuthorUid());
                        runOnUiThread(() -> adapter.notifyItemRemoved(index));
                    }
                }
            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot snapshot, String previousChildName) {
                runOnUiThread(() -> adapter.notifyDataSetChanged());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e("HomeActivity", "Error listening for post changes: ", error.toException());
                Toast.makeText(HomeActivity.this, "Failed toload posts", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void fetchAuthorDetailsForPost(Post post, Runnable onCompletion) {
        String authorUid = post.getAuthorUid();
        if (authorUid != null && !authorDetailsMap.containsKey(authorUid)) {
            FirebaseFirestore db = FirebaseFirestore.getInstance();DocumentReference userRef = db.collection("users").document(authorUid);
            userRef.get().addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {
                        String authorName = document.getString("name");
                        String authorUsername = document.getString("username");
                        String authorProfileImageUrl = document.getString("profileImageUrl");
                        AuthorDetails details = new AuthorDetails(authorName, authorUsername, authorProfileImageUrl);
                        authorDetailsMap.put(authorUid, details);
                    }
                } else {
                    Log.e("HomeActivity", "Error fetching author details: ", task.getException());
                }
                onCompletion.run(); // Notify adapter even if author details fetch fails
            });
        } else {
            onCompletion.run(); // Author details already cached, notify adapter
        }
    }

    private void fetchPostsAndAuthorDetailsFromFirebase() {
        DatabaseReference postsRef = FirebaseDatabase.getInstance().getReference("posts");
        postsRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                posts.clear();
                authorDetailsMap.clear();
                List<String> authorUids = new ArrayList<>();

                for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                    Post post = postSnapshot.getValue(Post.class);
                    if (post != null) {
                        posts.add(post);
                        authorUids.add(post.getAuthorUid());
                    }
                }

                fetchAuthorDetails(authorUids, () -> {
                    runOnUiThread(() -> adapter.notifyDataSetChanged());
                });
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.e("HomeActivity", "Error fetching posts: ", databaseError.toException());
                Toast.makeText(HomeActivity.this, "Failed to load posts", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void fetchAuthorDetails(List<String> authorUids, Runnable onCompletion) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        int authorCount = authorUids.size();
        int[] fetchedCount = {0};

        Log.d("HomeActivity", "Fetching author details. Count: " + authorCount);

        for (String authorUid : authorUids) {
            if (authorUid !=null) {
                Log.d("HomeActivity", "Fetching details for authorUid: " + authorUid);

                DocumentReference userRef = db.collection("users").document(authorUid);
                userRef.addSnapshotListener((documentSnapshot, error) -> {
                    if (error != null) {
                        Log.e("HomeActivity", "Error getting user details: ", error);
                        if (error instanceof FirebaseFirestoreException &&
                                ((FirebaseFirestoreException) error).getCode() == FirebaseFirestoreException.Code.UNAVAILABLE) {
                            showOfflineMessage();
                            retryFetchAuthorDetails(authorUids, onCompletion);
                        }
                        return;
                    }

                    if (documentSnapshot != null && documentSnapshot.exists()) {
                        String authorName = documentSnapshot.getString("name");
                        String authorUsername = documentSnapshot.getString("username");
                        String authorProfileImageUrl = documentSnapshot.getString("profileImageUrl");

                        Log.d("HomeActivity", "Fetched details: name=" + authorName +
                                ", username=" + authorUsername + ", profileImageUrl=" + authorProfileImageUrl);

                        AuthorDetails details = new AuthorDetails(authorName, authorUsername, authorProfileImageUrl);
                        authorDetailsMap.put(authorUid, details);
                    } else {
                        Log.w("HomeActivity", "User data not found for uid: " + authorUid);
                    }

                    fetchedCount[0]++;
                    if (fetchedCount[0] == authorCount) {
                        Log.d("HomeActivity", "Finished fetching author details.");
                        new Handler(Looper.getMainLooper()).post(onCompletion);
                    }
                });
            }
        }
    }

    private void retryFetchAuthorDetails(List<String> authorUids, Runnable onCompletion) {
        final int MAX_RETRIES = 3;
        final long RETRY_DELAY = 2000; // 2 seconds

        // Wrap retryCount in an array to make it modifiable from the inner class
        final int[] retryCount = {0};
        Handler handler = new Handler(Looper.getMainLooper());
        Runnable retryRunnable = new Runnable() {
            @Override
            public void run() {
                if (isNetworkAvailable() || retryCount[0] >= MAX_RETRIES) {
                    if (isNetworkAvailable()) {
                        fetchAuthorDetails(authorUids, onCompletion); // Retry fetching
                    } else{
                        showOfflineMessage(); // Network still unavailable after retries
                    }
                    handler.removeCallbacks(this); // Stop retrying
                } else {
                    retryCount[0]++; // Increment retry count
                    handler.postDelayed(this, RETRY_DELAY); // Retry after delay
                }
            }
        };
        handler.post(retryRunnable); // Start the retry process
    }

    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        Network activeNetwork = connectivityManager.getActiveNetwork();
        if (activeNetwork != null) {
            NetworkCapabilities networkCapabilities = connectivityManager.getNetworkCapabilities(activeNetwork);
            if (networkCapabilities != null) {
                return networkCapabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) ||
                        networkCapabilities.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) ||
                        networkCapabilities.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET);
            }
        }
        return false;
    }

    private void showOfflineMessage() {
        // Use the activity's content view as the anchor for the Snackbar
        View rootView = findViewById(android.R.id.content);
        Snackbar.make(rootView, "Some features are unavailable offline.", Snackbar.LENGTH_LONG)
                .show();
    }

    public static class AuthorDetails {
        public String name;
        public String username;
        public String profileImageUrl;

        public AuthorDetails(String name, String username, String profileImageUrl) {
            this.name = name;
            this.username = username;
            this.profileImageUrl = profileImageUrl;
        }
    }
}