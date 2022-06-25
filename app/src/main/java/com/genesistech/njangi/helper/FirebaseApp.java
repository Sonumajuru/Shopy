package com.genesistech.njangi.helper;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;
public class FirebaseApp {
    private final FirebaseAuth mAuth;
    private final FirebaseDatabase mDbReference;
    public FirebaseApp() {
        mAuth = FirebaseAuth.getInstance();
        mDbReference = FirebaseDatabase.getInstance("https://genesistecg-njangi-default-rtdb.europe-west1.firebasedatabase.app/");
    }
    public FirebaseAuth getAuth()
    {
        return mAuth;
    }
    public FirebaseDatabase getFirebaseDB() {
        return mDbReference;
    }
}