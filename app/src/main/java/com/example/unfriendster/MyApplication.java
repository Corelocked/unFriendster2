package com.example.unfriendster;

import android.app.Application;
import com.google.firebase.FirebaseApp;
import com.google.firebase.database.FirebaseDatabase;

public class MyApplication extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        FirebaseDatabase database = FirebaseDatabase.getInstance("https://unfriendster-92076-default-rtdb.asia-southeast1.firebasedatabase.app/");
        FirebaseApp.initializeApp(this);

    }
}