package com.example.unfriendster; // Replace with your actual package name

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class EditProfileActivity extends AppCompatActivity {

    private EditText nameEditText;
    private EditText usernameEditText;
    private EditText bioEditText;
    private Button saveButton;
    private Button cancelButton;

    private DatabaseReference databaseReference;
    private FirebaseUser currentUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile_edit);

        nameEditText = findViewById(R.id.name_edit_text);
        usernameEditText = findViewById(R.id.username_edit_text);
        bioEditText = findViewById(R.id.bio_edit_text);
        saveButton = findViewById(R.id.save_profile_button);
        cancelButton = findViewById(R.id.cancel_edit_button);

        // Initialize Firebase
        currentUser = FirebaseAuth.getInstance().getCurrentUser();
        databaseReference = FirebaseDatabase.getInstance().getReference("users").child(currentUser.getUid());

        // Load existing profile data
        loadExistingProfileData();

        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String newName = nameEditText.getText().toString();
                String newUsername = usernameEditText.getText().toString();
                String newBio = bioEditText.getText().toString();
                
                // Save the changes to Firebase
                saveProfileData(newName, newUsername, newBio);
            }
        });

        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
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
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // Handle errors
                Toast.makeText(EditProfileActivity.this, "Failed to load profile.", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void saveProfileData(String newName, String newUsername, String newBio) {
        databaseReference.child("name").setValue(newName);
        databaseReference.child("username").setValue(newUsername);
        databaseReference.child("bio").setValue(newBio)
                .addOnSuccessListener(aVoid -> {
                    Toast.makeText(EditProfileActivity.this, "Profile updated!", Toast.LENGTH_SHORT).show();
                    finish(); // Optionally, close the activity
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(EditProfileActivity.this, "Failed to update profile.", Toast.LENGTH_SHORT).show();
                });
    }
}