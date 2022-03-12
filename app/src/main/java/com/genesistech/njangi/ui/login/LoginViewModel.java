package com.genesistech.njangi.ui.login;

import android.app.Application;
import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.navigation.NavController;
import androidx.navigation.NavHost;
import androidx.navigation.NavOptions;
import com.genesistech.njangi.R;
import org.jetbrains.annotations.NotNull;

public class LoginViewModel extends AndroidViewModel {
    
    public LoginViewModel(@NonNull @NotNull Application application) {
        super(application);
    }

    public void goToAccount(NavHost navHostFragment, NavOptions navOption)
    {
        if (navHostFragment != null) {
            NavController navController = navHostFragment.getNavController();
//            navController.popBackStack(R.id.navigation_login, true);
            navController.navigate(R.id.navigation_account, null, navOption);
        }
    }
}