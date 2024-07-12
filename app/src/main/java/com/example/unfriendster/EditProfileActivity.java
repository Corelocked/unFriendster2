package com.example.unfriendster;

import android.app.Activity;
import android.content.Intent;

import android.net.Uri;
import android.os.Bundle;
import android.util.Log;

import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.github.dhaval2404.imagepicker.ImagePicker;

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

public class EditProfileActivity extends AppCompatActivity {

    private EditText nameEditText;
    private EditText usernameEditText;
    private EditText bioEditText;
    private Button saveButton;
    private Button cancelButton;
    private ImageView profileImageView;
    private Button chooseImageButton;

    private DatabaseReference databaseReference;
    private FirebaseUser currentUser;
    private Uri selectedImageUri;

    private final ActivityResultLauncher<Intent> startForProfileImageResult =
            registerForActivityResult(new ActivityResultContracts.StartActivityForResult(),
                    result -> {
                        if (result.getResultCode() == Activity.RESULT_OK) {
                            Intent data = result.getData();
                            if (data != null && data.getData() != null) {
                                selectedImageUri = data.getData();
                                profileImageView.setImageURI(selectedImageUri);
                            }
                        }
                    });

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile_edit);

        nameEditText = findViewById(R.id.name_edit_text);
        usernameEditText = findViewById(R.id.username_edit_text);
        bioEditText = findViewById(R.id.bio_edit_text);
        saveButton = findViewById(R.id.save_profile_button);
        cancelButton = findViewById(R.id.cancel_edit_button);
        profileImageView = findViewById(R.id.profile_image_view);
        chooseImageButton = findViewById(R.id.choose_image_button);

        currentUser = FirebaseAuth.getInstance().getCurrentUser();
        databaseReference = FirebaseDatabase.getInstance().getReference("users").child(currentUser.getUid());

        loadExistingProfileData();

        chooseImageButton.setOnClickListener(v -> {
            ImagePicker.with(this)
                    .crop()
                    .compress(1024)
                    .maxResultSize(1080, 1080)
                    .createIntent(intent -> {
                        startForProfileImageResult.launch(intent);
                        return null;
                    });
        });

        saveButton.setOnClickListener(v -> {
            String newName = nameEditText.getText().toString();
            String newUsername = usernameEditText.getText().toString();
            String newBio = bioEditText.getText().toString();

            saveProfileData(newName, newUsername, newBio, selectedImageUri);
        });

        cancelButton.setOnClickListener(v -> finish());
    }

    private void loadExistingProfileData() {
        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    String existingName = dataSnapshot.child("name").getValue(String.class);
                    String existingUsername = dataSnapshot.child("username").getValue(String.class);
                    String existingBio = dataSnapshot.child("bio").getValue(String.class);

                    nameEditText.setText(existingName);
                    usernameEditText.setText(existingUsername);
                    bioEditText.setText(existingBio);

                    String existingImageUrl = dataSnapshot.child("profileImageUrl").getValue(String.class);
                    if (existingImageUrl != null) {
                        Glide.with(EditProfileActivity.this).load(existingImageUrl).into(profileImageView);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(EditProfileActivity.this, "Failed to load profile.", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void saveProfileData(String newName, String newUsername, String newBio, Uri imageUri) {
        databaseReference.child("name").setValue(newName);
        databaseReference.child("username").setValue(newUsername);
        databaseReference.child("bio").setValue(newBio);

        if (imageUri != null) {
            uploadProfilePicture(imageUri);
        } else {
            Toast.makeText(EditProfileActivity.this, "Profile updated!", Toast.LENGTH_SHORT).show();
            finish();
        }
    }

    private void uploadProfilePicture(Uri imageUri) {
        FirebaseStorage storage = FirebaseStorage.getInstance();
        StorageReference storageRef = storage.getReference().child("profile_images/" + currentUser.getUid());

        UploadTask uploadTask = storageRef.putFile(imageUri);

        uploadTask.addOnSuccessListener(taskSnapshot -> {
                    Log.d("EditProfileActivity", "Image uploaded successfully");
                    storageRef.getDownloadUrl().addOnSuccessListener(uri -> {
                                String downloadUrl = uri.toString();
                                Log.d("EditProfileActivity", "Download URL: " + downloadUrl);

                                databaseReference.child("profileImageUrl").setValue(downloadUrl)
                                        .addOnSuccessListener(aVoid -> {
                                            Toast.makeText(EditProfileActivity.this, "Profile updated with image!", Toast.LENGTH_SHORT).show();

                                            Glide.with(EditProfileActivity.this)
                                                    .load(downloadUrl)
                                                    .into(profileImageView);


                                            Intent intent = new Intent(EditProfileActivity.this, ProfileActivity.class);
                                            intent.putExtra("profileImageUrl", downloadUrl);
                                            startActivity(intent);

                                            finish();
                                        })
                                        .addOnFailureListener(e -> {
                                            Log.e("EditProfileActivity", "Failed to update profile image URL.", e);
                                            Toast.makeText(EditProfileActivity.this, "Failed to update profile image URL.", Toast.LENGTH_SHORT).show();
                                        });
                            })
                            .addOnFailureListener(e -> {
                                Log.e("EditProfileActivity", "Failed to get download URL.", e);
                                Toast.makeText(EditProfileActivity.this, "Failed to get download URL.", Toast.LENGTH_SHORT).show();
                            });
                })
                .addOnFailureListener(e -> {
                    Log.e("EditProfileActivity", "Failed to upload profile image.", e);
                    Toast.makeText(EditProfileActivity.this, "Failed to upload profile image.", Toast.LENGTH_SHORT).show();
                });
    }
}