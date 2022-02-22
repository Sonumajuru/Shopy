package com.example.shopy.ui.reset;

import android.app.Application;
import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import org.jetbrains.annotations.NotNull;

public class ResetViewModel extends AndroidViewModel {
    // TODO: Implement the ViewModel

    public ResetViewModel(@NonNull @NotNull Application application) {
        super(application);
        Application app = (Application) application.getApplicationContext();
    }
}