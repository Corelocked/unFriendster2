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
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import org.checkerframework.checker.nullness.qual.NonNull;

public class CreatePostActivity extends AppCompatActivity{

    private static final int PICK_IMAGE_REQUEST = 1;
    EditText postTitleEditText, postContentEditText;
    ImageView postImagePreview;
    Button selectImageButton, postButton;
    Uri selectedImageUri; // To store the selected image URI

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_post);

        postTitleEditText = findViewById(R.id.postTitleEditText);
        postContentEditText = findViewById(R.id.postContentEditText);postImagePreview = findViewById(R.id.postImagePreview);
        selectImageButton = findViewById(R.id.selectImageButton);
        postButton = findViewById(R.id.postButton);

        selectImageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(intent, PICK_IMAGE_REQUEST);
            }
        });

        postButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String title = postTitleEditText.getText().toString();
                String content = postContentEditText.getText().toString();

                // Create a Post object
                Post newPost = new Post();
                newPost.setTitle(title);
                newPost.setContent(content);
                // ... set other fieldslike author, timestamp, etc. ...

                DatabaseReference postsRef = FirebaseDatabase.getInstance().getReference("posts");
                String postId = postsRef.push().getKey(); // Generate a unique post ID
                Log.d("CreatePostActivity", "Generated Post ID: " + postId);

                if (selectedImageUri != null) {
                    // Upload image to Firebase Storage
                    StorageReference storageRef = FirebaseStorage.getInstance().getReference("post_images/" + postId + ".jpg");
                    storageRef.putFile(selectedImageUri)
                            .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                                @Override
                                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                                    // Get download URL of uploaded image
                                    storageRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                        @Override
                                        public void onSuccess(Uri uri) {
                                            newPost.setPhotoUrl(uri.toString());
                                            // Save postto Realtime Database
                                            postsRef.child(postId).setValue(newPost)
                                                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                        @Override
                                                        public void onSuccess(Void aVoid) {
                                                            Log.d("CreatePostActivity", "Post with image saved successfully");
                                                            finish();
                                                        }
                                                    })
                                                    .addOnFailureListener(new OnFailureListener() { // Add error handling here
                                                        @Override
                                                        public void onFailure(@NonNull Exception e) {
                                                            Log.e("CreatePostActivity", "Error saving post with image", e);
                                                            // Handle the error appropriately (e.g., show a message to the user)
                                                        }
                                                    });
                                        }
                                    }).addOnFailureListener(new OnFailureListener() { // Add error handling here as well
                                        @Override
                                        public void onFailure(@NonNull Exception e) {
                                            Log.e("CreatePostActivity", "Error getting download URL", e);
                                            // Handle the error appropriately
                                        }
                                    });
                                }
                            })
                            .addOnFailureListener(new OnFailureListener() { // Add error handling for image upload
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    Log.e("CreatePostActivity", "Error uploading image", e);
                                    // Handle the error appropriately
                                }
                            });
                }
            }
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