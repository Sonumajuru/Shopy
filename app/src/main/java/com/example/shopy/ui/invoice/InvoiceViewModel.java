package com.example.shopy.ui.invoice;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class InvoiceViewModel extends ViewModel {

    private MutableLiveData<String> mText;

    public InvoiceViewModel() {
        mText = new MutableLiveData<>();
        mText.setValue("This is invoice fragment");
    }

    public LiveData<String> getText() {
        return mText;
    }
}