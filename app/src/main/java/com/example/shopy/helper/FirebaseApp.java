package com.example.shopy.helper;

import com.google.firebase.auth.FirebaseAuth;

public class FirebaseApp {

    private final FirebaseAuth mAuth;

    public FirebaseApp() {
        mAuth = FirebaseAuth.getInstance();
    }

    public FirebaseAuth getAuth()
    {
        return mAuth;
    }
}