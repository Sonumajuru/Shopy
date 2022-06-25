package com.genesistech.njangi.ui.detail;

import android.app.Application;
import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import org.jetbrains.annotations.NotNull;
public class DetailViewModel extends AndroidViewModel {
    // TODO: Implement the ViewModel
    public DetailViewModel(@NonNull @NotNull Application application) {
        super(application);
        Application app = (Application) application.getApplicationContext();
    }
}