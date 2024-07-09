package com.example.unfriendster;

import android.os.Bundle;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;


public class ProfileActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.profile_page);

        ImageView profileImageView = findViewById(R.id.profile_image);

        profileImageView.setImageResource(R.drawable.blankpfp);
    }
}