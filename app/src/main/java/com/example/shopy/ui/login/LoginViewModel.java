package com.example.shopy.ui.login;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;
import androidx.navigation.NavController;
import androidx.navigation.NavHost;
import com.example.shopy.R;

public class LoginViewModel extends ViewModel {
    
    public LoginViewModel() {
//        mText.setValue("This is category fragment");
    }

    public void goToAccount(NavHost navHostFragment)
    {
        if (navHostFragment != null)
        {
            NavController navController = navHostFragment.getNavController();
            navController.navigate(R.id.navigation_account);
        }
    }
}