package com.njangi.shop.ui.profile;

import android.app.Application;
import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import com.njangi.shop.R;
import com.njangi.shop.helper.FirebaseApp;
import com.njangi.shop.model.User;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;
import org.jetbrains.annotations.NotNull;

public class ProfileViewModel extends AndroidViewModel {

    private final Application app;
    private final FirebaseApp firebaseApp;
    private final MutableLiveData<String> idProfileName;
    private final MutableLiveData<String> idEmail;
    private final MutableLiveData<String> idNumber;
    private final MutableLiveData<String> idCountryText;
    private final MutableLiveData<String> idAddress;
    private final MutableLiveData<String> idSince;

    public ProfileViewModel(@NonNull @NotNull Application application) {
        super(application);
        app = (Application) application.getApplicationContext();
        firebaseApp = new FirebaseApp();
        idProfileName = new MutableLiveData<>();
        idEmail = new MutableLiveData<>();
        idNumber = new MutableLiveData<>();
        idCountryText = new MutableLiveData<>();
        idAddress = new MutableLiveData<>();
        idSince = new MutableLiveData<>();
    }

    public void getUserData(String uid)
    {
        DatabaseReference eventsRef = firebaseApp.getFirebaseDB().getReference().child("User").child(uid).getRef();
        ValueEventListener valueEventListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot)
            {
                User user = dataSnapshot.getValue(User.class);
                assert user != null;

                idProfileName.setValue(user.getFirstName()
                        + " " + user.getLastName());
                idEmail.setValue(user.getEmail());
                idNumber.setValue(user.getTelNumber());
                idCountryText.setValue(user.getCountry());
                idAddress.setValue(user.getAddress());
                idSince.setValue(app.getString(R.string.registration) + " " + user.getDate());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
//                Log.d(TAG, databaseError.getMessage()); //Don't ignore errors!
            }
        };
        eventsRef.addListenerForSingleValueEvent(valueEventListener);
    }

    public LiveData<String> getProfile()
    {
        return idProfileName;
    }
    public LiveData<String> getEmail()
    {
        return idEmail;
    }
    public LiveData<String> getNumber()
    {
        return idNumber;
    }
    public LiveData<String> getCountry()
    {
        return idCountryText;
    }
    public LiveData<String> getAddress()
    {
        return idAddress;
    }
    public LiveData<String> getRegistrationDate()
    {
        return idSince;
    }
}