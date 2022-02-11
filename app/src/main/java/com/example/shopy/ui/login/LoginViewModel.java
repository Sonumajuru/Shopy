package com.example.shopy.ui.login;

import android.app.Application;
import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;
import androidx.navigation.NavController;
import androidx.navigation.NavHost;
import androidx.navigation.NavOptions;
import com.example.shopy.R;
import com.example.shopy.helper.FirebaseApp;
import org.jetbrains.annotations.NotNull;

public class LoginViewModel extends AndroidViewModel {
    
    public LoginViewModel(@NonNull @NotNull Application application) {
        super(application);
    }

    public void goToAccount(NavHost navHostFragment, NavOptions navOption)
    {
        if (navHostFragment != null)
        {
            NavController navController = navHostFragment.getNavController();
            navController.navigate(R.id.navigation_account, null, navOption);
        }
    }
}