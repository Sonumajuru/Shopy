package com.genesistech.njangi.ui.home;

import android.app.Application;
import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import com.genesistech.njangi.R;
import com.genesistech.njangi.helper.FirebaseApp;
import com.genesistech.njangi.model.User;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.*;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;

public class HomeViewModel extends AndroidViewModel {

    private final MutableLiveData<String> mTitle;
    private final Application app;
    private FirebaseApp firebaseApp;

    public HomeViewModel(@NonNull @NotNull Application application) {
        super(application);
        app = (Application) application.getApplicationContext();
        mTitle = new MutableLiveData<>();
        firebaseApp = new FirebaseApp();
    }

    public LiveData<String> getText() {
        return mTitle;
    }

    public void getUserName()
    {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user == null) return;
        String userid = Objects.requireNonNull(user).getUid();
        firebaseApp.getFirebaseDB()
                .getReference()
                .child("User")
                .child(userid).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NotNull DataSnapshot dataSnapshot)
            {
                mTitle.setValue(app.getString(R.string.welcome_back) + " " + Objects.requireNonNull(dataSnapshot.getValue(User.class)).getFirstName());
            }

            @Override
            public void onCancelled(@NotNull DatabaseError databaseError) {
            }
        });
    }
}