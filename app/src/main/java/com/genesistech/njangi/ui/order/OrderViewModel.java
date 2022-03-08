package com.genesistech.njangi.ui.order;

import android.app.Application;
import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import org.jetbrains.annotations.NotNull;

public class OrderViewModel extends AndroidViewModel {

    private final MutableLiveData<String> mText;

    public OrderViewModel(@NonNull @NotNull Application application) {
        super(application);
        mText = new MutableLiveData<>();
        mText.setValue("This is order fragment");
    }

    public LiveData<String> getText() {
        return mText;
    }
}