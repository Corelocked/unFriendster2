package com.example.unfriendster;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import com.google.firebase.auth.FirebaseAuth;

public class SettingsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.settings_page);

        Button signoutButton = findViewById(R.id.signout_btn);

        signoutButton.setOnClickListener(new View.OnClickListener() {
            @Override

            public void onClick(View v) {
                // Show confirmation dialog
                new AlertDialog.Builder(SettingsActivity.this)
                        .setTitle("Confirm Sign Out")
                        .setMessage("Are you sure you want to sign out?")
                        .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                // User confirmed, sign out using Firebase
                                FirebaseAuth.getInstance().signOut();

                                // Navigate to MainActivity after sign-out
                                Intent intent = new Intent(SettingsActivity.this, MainActivity.class);
                                startActivity(intent);
                            }
                        })
                        .setNegativeButton(android.R.string.no, null)
                        .setIcon(android.R.drawable.ic_dialog_alert)
                        .show();
            }
        });
    }
}