package com.njangi.shop.ui.product;

import android.app.Application;
import android.widget.ArrayAdapter;
import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import com.njangi.shop.R;
import com.njangi.shop.helper.FirebaseApp;
import com.njangi.shop.model.User;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.*;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

public class ProductViewModel extends AndroidViewModel {

    private final Application app;
    private final MutableLiveData<String> inputCurrency;
    private final FirebaseApp firebaseApp;

    public ProductViewModel(@NonNull @NotNull Application application) {
        super(application);
        app = (Application) application.getApplicationContext();
        firebaseApp = new FirebaseApp();
        inputCurrency = new MutableLiveData<>();
    }

    public ArrayAdapter<String> getAdapter()
    {
        // Make ENUM for Categories or Strings of ID R.id.String
        String[] ProductCategories = new String[]{app.getApplicationContext()
                .getString(R.string.electronics), app.getApplicationContext()
                .getString(R.string.computer), app.getApplicationContext()
                .getString(R.string.games), app.getApplicationContext()
                .getString(R.string.home_appliance), app.getApplicationContext()
                .getString(R.string.phones), app.getApplicationContext()
                .getString(R.string.clothing), app.getApplicationContext()
                .getString(R.string.shoes), app.getApplicationContext()
                .getString(R.string.books), app.getApplicationContext()
                .getString(R.string.cars), app.getApplicationContext()
                .getString(R.string.hairs),app.getApplicationContext()
                .getString(R.string.cosmetics),app.getApplicationContext()
                .getString(R.string.motorbike)};

        final List<String> categoryList = new ArrayList<>(Arrays.asList(ProductCategories));
        ArrayAdapter<String> adapter = new ArrayAdapter<>(app.getApplicationContext(), android.R.layout.simple_spinner_item, categoryList);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        return adapter;
    }

    public void getCurrency()
    {
        FirebaseUser user = firebaseApp.getAuth().getCurrentUser();
        String userid = Objects.requireNonNull(user).getUid();
        DatabaseReference reference = FirebaseDatabase
                .getInstance("https://shopy-a60b9-default-rtdb.europe-west1.firebasedatabase.app/")
                .getReference("User");
        reference.child(userid).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NotNull DataSnapshot dataSnapshot)
            {
                String country = Objects.requireNonNull(dataSnapshot.getValue(User.class)).getCountry();
                switch (country) {
                    case "Cameroon":
                        inputCurrency.setValue("CFA");
                        break;
                    case "Nigeria":
                        inputCurrency.setValue("NGN");
                        break;
                    case "Ghana":
                        inputCurrency.setValue("GH₵");
                        break;
                }
            }
            @Override
            public void onCancelled(@NotNull DatabaseError databaseError) {

            }
        });
    }

    public LiveData<String> getText() {
        return inputCurrency;
    }
}