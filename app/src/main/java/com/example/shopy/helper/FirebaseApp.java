package com.example.shopy.helper;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;

public class FirebaseApp {

    private final FirebaseAuth mAuth;
    private final FirebaseDatabase rootRef;

    public FirebaseApp() {
        mAuth = FirebaseAuth.getInstance();
        rootRef = FirebaseDatabase.getInstance();
    }

    public FirebaseAuth getAuth()
    {
        return mAuth;
    }

    public FirebaseDatabase getFirebaseDB()
    {
        return rootRef;
    }
}