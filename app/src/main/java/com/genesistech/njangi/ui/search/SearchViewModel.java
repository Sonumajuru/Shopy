package com.genesistech.njangi.ui.search;

import android.app.Application;
import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import org.jetbrains.annotations.NotNull;
public class SearchViewModel extends AndroidViewModel {
    private final MutableLiveData<String> mText;
    public SearchViewModel(@NonNull @NotNull Application application) {
        super(application);
        mText = new MutableLiveData<>();
        mText.setValue("");
    }
    public LiveData<String> getText() {
        return mText;
    }
}