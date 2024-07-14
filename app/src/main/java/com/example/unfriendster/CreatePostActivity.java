package com.example.unfriendster;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

public class CreatePostActivity extends AppCompatActivity {

    private static final int PICK_IMAGE_REQUEST = 1;
    EditText postTitleEditText, postContentEditText;
    ImageView postImagePreview;
    Button selectImageButton, postButton;
    Uri selectedImageUri;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_post);

        postTitleEditText = findViewById(R.id.postTitleEditText);
        postContentEditText = findViewById(R.id.postContentEditText);
        postImagePreview = findViewById(R.id.postImagePreview);
        selectImageButton = findViewById(R.id.selectImageButton);
        postButton = findViewById(R.id.postButton);

        selectImageButton.setOnClickListener(view -> {
            Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
            startActivityForResult(intent, PICK_IMAGE_REQUEST);
        });

        postButton.setOnClickListener(view -> {
            String title = postTitleEditText.getText().toString().trim();
            String content = postContentEditText.getText().toString().trim();

            if (title.isEmpty() || content.isEmpty()) {
                Snackbar.make(view, "Please enter a title and content", Snackbar.LENGTH_SHORT).show();
                return;
            }

            Toast.makeText(CreatePostActivity.this, "Creating post...", Toast.LENGTH_SHORT).show();

            FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
            if (currentUser != null) {
                fetchUserDataAndCreatePost(currentUser.getUid(), title, content);
            } else {
                Toast.makeText(CreatePostActivity.this, "You need to be logged in to create a post", Toast.LENGTH_SHORT).show();
                // Consider redirecting to login screen here
            }
        });
    }

    private void fetchUserDataAndCreatePost(String currentUserId, String title, String content) {
        DatabaseReference usersRef = FirebaseDatabase.getInstance().getReference("users").child(currentUserId);
        usersRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    String userName = dataSnapshot.child("name").getValue(String.class);
                    String userUsername = dataSnapshot.child("username").getValue(String.class);
                    String userProfilePictureUrl = dataSnapshot.child("profileImageUrl").getValue(String.class);

                    Post newPost = new Post();
                    newPost.setAuthorUid(currentUserId);
                    newPost.setAuthorUsername(userUsername != null ? userUsername : "");
                    newPost.setAuthorProfilePictureUrl(userProfilePictureUrl != null ? userProfilePictureUrl : "");
                    newPost.setTitle(title);
                    newPost.setContent(content);
                    // ... set other fields like timestamp, etc. ...



                    DatabaseReference postsRef = FirebaseDatabase.getInstance().getReference("posts");
                    String postId = postsRef.push().getKey();
                    Log.d("CreatePostActivity", "Generated Post ID: " + postId);

                    if (selectedImageUri != null) {
                        uploadImageAndSavePost(postsRef, postId, newPost, selectedImageUri);
                    } else {
                        savePostToDatabase(postsRef, postId, newPost);
                    }

                } else {
                    Toast.makeText(CreatePostActivity.this, "Error: User data not found", Toast.LENGTH_SHORT).show();
                    Log.e("CreatePostActivity", "User data not found for user ID: " + currentUserId);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(CreatePostActivity.this, "Error fetching user data", Toast.LENGTH_SHORT).show();
                Log.e("CreatePostActivity", "Error fetching user data", databaseError.toException());
            }
        });
    }

    private void uploadImageAndSavePost(DatabaseReference postsRef, String postId, Post newPost, Uri imageUri) {
        StorageReference storageRef = FirebaseStorage.getInstance().getReference("post_images/" + postId + ".jpg");
        storageRef.putFile(imageUri)
                .addOnSuccessListener(taskSnapshot ->
                        storageRef.getDownloadUrl().addOnSuccessListener(uri -> {
                            newPost.setPhotoUrl(uri.toString());
                            savePostToDatabase(postsRef, postId, newPost);
                        }).addOnFailureListener(e -> {
                            Toast.makeText(CreatePostActivity.this, "Error getting download URL", Toast.LENGTH_SHORT).show();
                            Log.e("CreatePostActivity", "Error getting download URL", e);
                        })
                )
                .addOnFailureListener(e -> {
                    Toast.makeText(CreatePostActivity.this, "Error uploading image", Toast.LENGTH_SHORT).show();
                    Log.e("CreatePostActivity", "Error uploading image", e);
                });
    }

    private void savePostToDatabase(DatabaseReference postsRef, String postId, Post newPost) {
        postsRef.child(postId).setValue(newPost)
                .addOnSuccessListener(aVoid -> {
                    Toast.makeText(CreatePostActivity.this, "Post created successfully", Toast.LENGTH_SHORT).show();
                    Log.d("CreatePostActivity", "Post saved successfully");
                    finish(); // Close the activity after successful post creation
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(CreatePostActivity.this, "Error creating post", Toast.LENGTH_SHORT).show();
                    Log.e("CreatePostActivity", "Error saving post", e);
                });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {
            selectedImageUri = data.getData();
            postImagePreview.setImageURI(selectedImageUri);
            postImagePreview.setVisibility(View.VISIBLE);
        }
    }
}