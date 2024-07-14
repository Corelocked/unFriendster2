package com.example.unfriendster;

import android.app.Application;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreSettings;

public class MyApplication extends Application {
    @Override
    public void onCreate() {
        super.onCreate();


        FirebaseDatabase database = FirebaseDatabase.getInstance("https://unfriendster-92076-default-rtdb.asia-southeast1.firebasedatabase.app/");
        database.setPersistenceEnabled(true);


        FirebaseFirestore db = FirebaseFirestore.getInstance();
        FirebaseFirestoreSettings settings = new FirebaseFirestoreSettings.Builder()
                .setPersistenceEnabled(true)
                .build();
        db.setFirestoreSettings(settings);
    }
}
