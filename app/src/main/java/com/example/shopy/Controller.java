package com.example.shopy;

import com.google.firebase.auth.FirebaseAuth;

public class Controller {

    private FirebaseAuth mAuth;

    public FirebaseAuth getmAuth() {
        return mAuth;
    }

    public void setmAuth(FirebaseAuth mAuth) {
        this.mAuth = mAuth;
    }
}
