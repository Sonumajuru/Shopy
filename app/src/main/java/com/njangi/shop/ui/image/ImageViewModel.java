package com.njangi.shop.ui.image;

import android.app.Application;
import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import org.jetbrains.annotations.NotNull;

public class ImageViewModel extends AndroidViewModel {
    // TODO: Implement the ViewModel

    public ImageViewModel(@NonNull @NotNull Application application) {
        super(application);
        Application app = (Application) application.getApplicationContext();
    }
}