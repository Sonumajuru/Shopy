package com.njangi.shop.ui.category;

import android.app.Application;
import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import org.jetbrains.annotations.NotNull;

public class CategoryViewModel extends AndroidViewModel {

    public CategoryViewModel(@NonNull @NotNull Application application) {
        super(application);
        Application app = (Application) application.getApplicationContext();
    }
}