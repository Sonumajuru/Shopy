package com.example.shopy.ui.cart;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class ShoppingCartViewModel extends ViewModel {

    private MutableLiveData<String> mText;

    public ShoppingCartViewModel() {
        mText = new MutableLiveData<>();
        mText.setValue("This is shopping cart fragment");
    }

    public LiveData<String> getText() {
        return mText;
    }
}