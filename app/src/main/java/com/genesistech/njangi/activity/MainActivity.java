package com.genesistech.njangi.activity;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.net.ConnectivityManager;
import android.net.Network;
import android.net.NetworkCapabilities;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.splashscreen.SplashScreen;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;
import androidx.navigation.ui.NavigationUI;
import com.genesistech.njangi.Controller;
import com.genesistech.njangi.R;
import com.genesistech.njangi.databinding.ActivityMainBinding;
import com.genesistech.njangi.helper.DeviceType;
import com.genesistech.njangi.helper.LanguageHelper;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import org.jetbrains.annotations.NotNull;

import java.util.Locale;

import static android.os.Build.VERSION.SDK_INT;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";

    @SuppressLint("SourceLockedOrientationActivity")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTheme(R.style.ThemeNjangi);
        SplashScreen splashScreen = SplashScreen.installSplashScreen(this);
        @SuppressLint("ShowToast")
        Toast toast = Toast.makeText(getApplicationContext(),
                "There is no internet connectivity!", Toast.LENGTH_LONG);
        splashScreen.setKeepOnScreenCondition(() -> {
            if (!isNetworkAvailable())
            {
                setTheme(R.style.Theme_App_Starting);
                toast.show();
                return true;
            }
            toast.cancel();
            return false;
        });
        initLanguage();

        com.genesistech.njangi.databinding.ActivityMainBinding binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        Controller controller = new Controller(this);
        if (Controller.getDeviceType() == DeviceType.Phone)
        {
            this.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        }

        ConnectivityManager m = (ConnectivityManager) getSystemService(Service.CONNECTIVITY_SERVICE);
        m.registerDefaultNetworkCallback(new ConnectivityManager.NetworkCallback() {
            @Override
            public void onAvailable(Network network) {
                Log.e(TAG, "onAvailable: ");
            }

            @Override
            public void onLost(Network network) {
                Log.e(TAG, "onLost: ");
            }
        });

        BottomNavigationView navView = findViewById(R.id.nav_view);
        NavHostFragment navHostFragment = (NavHostFragment)
                getSupportFragmentManager().findFragmentById(R.id.nav_host_fragment_activity_main);

        if (navHostFragment != null)
        {
            NavController navController = navHostFragment.getNavController();
            NavigationUI.setupWithNavController(binding.navView, navController);
        }
    }

    public static void setLocale(Activity activity, String languageCode)
    {
        Locale locale = new Locale(languageCode);
        Locale.setDefault(locale);
        Resources resources = activity.getResources();
        Configuration config = resources.getConfiguration();
        config.setLocale(locale);
        activity.createConfigurationContext(config);
//        resources.updateConfiguration(config, resources.getDisplayMetrics());
    }

    @SuppressLint("ObsoleteSdkInt")
    private Boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        if (SDK_INT >= Build.VERSION_CODES.M) {
            Network nw = connectivityManager.getActiveNetwork();
            if (nw == null) return false;
            NetworkCapabilities actNw = connectivityManager.getNetworkCapabilities(nw);
            return actNw != null && (actNw.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) || actNw.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) || actNw.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET) || actNw.hasTransport(NetworkCapabilities.TRANSPORT_BLUETOOTH));
        } else {
            return false;
        }
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        setIntent(intent);
    }

    @Override
    public void onConfigurationChanged(@NotNull Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        initLanguage();
    }

    private void initLanguage() {
        String ul = LanguageHelper.getUserLanguage(this);
        // if null the language doesn't need to be changed as the user has not chosen one.
        if (ul != null) {
            LanguageHelper.updateLanguage(this, ul);
            setLocale(this, ul);
        }
    }
}