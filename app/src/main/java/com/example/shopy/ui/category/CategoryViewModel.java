package com.example.shopy.ui.category;

import android.app.Application;
import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;
import com.example.shopy.R;
import org.jetbrains.annotations.NotNull;

public class CategoryViewModel extends AndroidViewModel {

    private final Application app;

    public CategoryViewModel(@NonNull @NotNull Application application) {
        super(application);
        app = (Application) application.getApplicationContext();
    }
}