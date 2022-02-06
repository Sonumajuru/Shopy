package com.example.shopy.ui.detail;

import android.app.Application;
import android.text.InputFilter;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import org.jetbrains.annotations.NotNull;

public class DetailViewModel extends AndroidViewModel {
    // TODO: Implement the ViewModel

    private final Application app;

    public DetailViewModel(@NonNull @NotNull Application application) {
        super(application);
        app = (Application) application.getApplicationContext();
    }
}