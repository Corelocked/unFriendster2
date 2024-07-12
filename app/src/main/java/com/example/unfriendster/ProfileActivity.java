package com.example.unfriendster;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class ProfileActivity extends AppCompatActivity {

    private TextView nameTextView;
    private TextView usernameTextView;
    private TextView bioTextView;
    private ImageView profileImageView;
    private DatabaseReference databaseReference;
    private FirebaseUser currentUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.profile_page);

        Button notificationButton = findViewById(R.id.notification_btn);
        Button profileButton = findViewById(R.id.profile_btn);
        Button homeButton = findViewById(R.id.home_btn);
        Button settingsButton = findViewById(R.id.settings_btn);
        Button logoButton = findViewById(R.id.logo_btn);
        Button editProfileButton = findViewById(R.id.editprofile_btn);

        nameTextView = findViewById(R.id.name_txt);
        usernameTextView = findViewById(R.id.username_txt);
        bioTextView = findViewById(R.id.bio_txt);
        profileImageView = findViewById(R.id.profile_image_view);


        currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if (currentUser != null) {
            databaseReference = FirebaseDatabase.getInstance().getReference("users").child(currentUser.getUid());loadAndDisplayProfileData();
        } else {

            Toast.makeText(this, "User not logged in", Toast.LENGTH_SHORT).show();

        }

        databaseReference.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot snapshot, String previousChildName) { }

            @Override
            public void onChildChanged(@NonNull DataSnapshot snapshot, String previousChildName) {
                if (snapshot.getKey().equals("profileImageUrl")) {
                    String updatedImageUrl = snapshot.getValue(String.class);
                    Log.d("ProfileActivity", "Profile image URL changed: " + updatedImageUrl);
                    loadImageIntoImageView(updatedImageUrl);
                } else if (snapshot.getKey().equals("name")) {
                    String newName = snapshot.getValue(String.class);
                    nameTextView.setText(newName);
                } else if (snapshot.getKey().equals("username")) {
                    String newUsername = snapshot.getValue(String.class);
                    usernameTextView.setText("@" + newUsername);
                } else if (snapshot.getKey().equals("bio")) {
                    String newBio = snapshot.getValue(String.class);
                    bioTextView.setText(newBio);
                }
            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot snapshot) { }

            @Override
            public void onChildMoved(@NonNull DataSnapshot snapshot, String previousChildName) { }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

                Log.e("ProfileActivity", "Database error: " + error.getMessage());
            }
        });

        notificationButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ProfileActivity.this, NotificationActivity.class);
                startActivity(intent);
            }
        });

        profileButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                loadAndDisplayProfileData();
            }
        });

        homeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ProfileActivity.this, HomeActivity.class);
                startActivity(intent);
            }
        });

        settingsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ProfileActivity.this, SettingsActivity.class);
                startActivity(intent);
            }
        });

        logoButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ProfileActivity.this, HomeActivity.class);
                startActivity(intent);
            }
        });

        editProfileButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ProfileActivity.this, EditProfileActivity.class);
                startActivity(intent);
            }
        });
    }

    private void loadAndDisplayProfileData() {
        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    String name = dataSnapshot.child("name").getValue(String.class);
                    String username = dataSnapshot.child("username").getValue(String.class);
                    String bio = dataSnapshot.child("bio").getValue(String.class);

                    nameTextView.setText(name);
                    usernameTextView.setText("@" + username);
                    bioTextView.setText(bio);

                    databaseReference.child("profileImageUrl").addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            if (snapshot.exists()) {
                                String imageUrl= snapshot.getValue(String.class);
                                loadImageIntoImageView(imageUrl);
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                            Toast.makeText(ProfileActivity.this, "Failed to load profile image.", Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

                Toast.makeText(ProfileActivity.this, "Failed to load profile.", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void loadImageIntoImageView(String imageUrl) {
        if (imageUrl != null && !imageUrl.isEmpty()) {
            Glide.with(ProfileActivity.this)
                    .load(imageUrl)
                    .into(profileImageView);
        }
    }
}