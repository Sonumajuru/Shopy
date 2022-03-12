package com.genesistech.njangi.ui.product;

import android.app.Application;
import android.widget.ArrayAdapter;
import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import com.genesistech.njangi.R;
import com.genesistech.njangi.helper.FirebaseApp;
import com.genesistech.njangi.model.User;
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
    private String seller;
    private final FirebaseApp firebaseApp;
    private final MutableLiveData<String> btnChoose;
    private final MutableLiveData<String> btnUpload;
    private final MutableLiveData<String> btnStock;

    public ProductViewModel(@NonNull @NotNull Application application) {
        super(application);
        app = (Application) application.getApplicationContext();
        btnChoose = new MutableLiveData<>();
        btnUpload = new MutableLiveData<>();
        btnStock = new MutableLiveData<>();
        firebaseApp = new FirebaseApp();
        inputCurrency = new MutableLiveData<>();
        seller = "";
    }

    public ArrayAdapter<String> getAdapter() {
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
                .getString(R.string.babyandtoys),app.getApplicationContext()
                .getString(R.string.accessories),app.getApplicationContext()
                .getString(R.string.cosmetics),app.getApplicationContext()
                .getString(R.string.motorbike)};

        final List<String> categoryList = new ArrayList<>(Arrays.asList(ProductCategories));
        ArrayAdapter<String> adapter = new ArrayAdapter<>(app.getApplicationContext(), android.R.layout.simple_spinner_item, categoryList);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        return adapter;
    }

    public void getCurrency() {
        FirebaseUser user = firebaseApp.getAuth().getCurrentUser();
        String userid = Objects.requireNonNull(user).getUid();
        firebaseApp.getFirebaseDB()
                .getReference()
                .child("User").child(userid).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NotNull DataSnapshot dataSnapshot)
            {
                String country = Objects.requireNonNull(dataSnapshot.getValue(User.class)).getCountry();
                seller = Objects.requireNonNull(dataSnapshot.getValue(User.class)).getFirstName();
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

    public LiveData<String> getCurrencySign() {
        return inputCurrency;
    }

    public String getSeller() {
        return seller;
    }

    public LiveData<String> getTextChoose() {
        btnChoose.setValue(app.getString(R.string.photos));
        return btnChoose;
    }

    public LiveData<String> getTextUpload(boolean user) {
        if (user) {
            btnUpload.setValue(app.getString(R.string.upload));
        }
        else {
            btnUpload.setValue(app.getString(R.string.update));
        }
        return btnUpload;
    }

    public LiveData<String> getTextStock(boolean user) {
        if (user) {
            btnStock.setValue(app.getString(R.string.boutique));
        }
        else {
            btnStock.setValue(app.getString(R.string.back));
        }
        return btnStock;
    }
}