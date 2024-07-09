package com.example.unfriendster;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;


public class SignupActivity extends AppCompatActivity {

    private EditText usernameInput, emailInput, passwordInput;
    private Button signupButton;
    private FirebaseAuth mAuth;
    private DatabaseReference mDatabase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.signup_screen);

        // Initialize Firebase Auth
        mAuth = FirebaseAuth.getInstance();

        // Initialize Firebase Realtime Database reference
        mDatabase = FirebaseDatabase.getInstance().getReference("users");

        usernameInput = findViewById(R.id.username_input);
        emailInput = findViewById(R.id.email_input);
        passwordInput = findViewById(R.id.password_input);
        signupButton = findViewById(R.id.signup_btn);

        signupButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String username = usernameInput.getText().toString();
                String email = emailInput.getText().toString();
                String password = passwordInput.getText().toString();

                if (validateInput(username, email, password)) {
                    // Create user with Firebase Authentication
                    mAuth.createUserWithEmailAndPassword(email, password)
                            .addOnCompleteListener(SignupActivity.this, new OnCompleteListener<AuthResult>() {
                                @Override
                                public void onComplete(@NonNull Task<AuthResult> task) {
                                    if (task.isSuccessful()) {
                                        // Signup success
                                        Toast.makeText(SignupActivity.this, "Signup successful!", Toast.LENGTH_SHORT).show();

                                        // Automatically sign in the user
                                        FirebaseUser user = mAuth.getCurrentUser();
                                        if (user != null) {
                                            // Write user data to Realtime Database
                                            writeNewUser(user.getUid(), username, email);

                                            // User is signed in, navigate to ProfileActivity
                                            Intent intent = new Intent(SignupActivity.this, ProfileActivity.class);
                                            startActivity(intent);
                                            finish(); // Optional: Finish SignupActivity
                                        }
                                    } else {
                                        if (task.getException() instanceof FirebaseAuthUserCollisionException) {
                                            // Email already in use
                                            Toast.makeText(SignupActivity.this, "This email is already registered. Try logging in or use a different email.", Toast.LENGTH_SHORT).show();
                                        } else {
                                            // Other signup errors
                                            String errorMessage= task.getException() != null ? task.getException().getMessage() : "Signup failed";
                                            Toast.makeText(SignupActivity.this, errorMessage, Toast.LENGTH_SHORT).show();
                                        }
                                    }
                                }
                            });
                }
            }
        });
    }

    private boolean validateInput(String username, String email, String password) {
        if (username.isEmpty() || email.isEmpty() || password.isEmpty()) {
            Toast.makeText(this, "Please fill in all fields", Toast.LENGTH_SHORT).show();
            return false;
        }

        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            Toast.makeText(this, "Please enter a valid email address", Toast.LENGTH_SHORT).show();
            return false;
        }

        if (password.length() < 8) {
            Toast.makeText(this, "Password must be at least 8 characters long", Toast.LENGTH_SHORT).show();
            return false;
        }

        return true;
    }

    private void writeNewUser(String userId, String username, String email) {
        Log.d("SignupActivity", "Writing new user to database");
        User user = new User(username,email);
        mDatabase.child(userId).setValue(user)
                .addOnSuccessListener(aVoid -> {
                    // Write was successful!
                    Log.d("SignupActivity", "User data written successfully for userId: " + userId);
                })
                .addOnFailureListener(e -> {
                    // Handle error
                    Log.e("SignupActivity", "Failed to write user data for userId: " + userId, e);
                    Toast.makeText(SignupActivity.this, "Failed to save data. Please try again later.", Toast.LENGTH_SHORT).show();
                });
    }
}