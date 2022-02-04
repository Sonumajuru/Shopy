package com.example.shopy.ui.account;

import android.app.Activity;
import android.app.Application;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.net.Uri;
import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.navigation.NavController;
import androidx.navigation.NavHost;
import com.example.shopy.R;
import com.example.shopy.helper.FirebaseApp;
import com.example.shopy.helper.LanguageHelper;
import org.jetbrains.annotations.NotNull;

import java.util.Locale;

import static com.example.shopy.R.id.navigation_login;

public class AccountViewModel extends AndroidViewModel {

    private final MutableLiveData<String> emailSender;
    private final MutableLiveData<String> whatsAppNum;
    private final MutableLiveData<String> appVersion;
    private final Application app;
    private final FirebaseApp firebaseApp;

    public AccountViewModel(@NonNull @NotNull Application application) {
        super(application);
        app = (Application) application.getApplicationContext();
        firebaseApp = new FirebaseApp();
        emailSender = new MutableLiveData<>();
        whatsAppNum = new MutableLiveData<>();
        appVersion = new MutableLiveData<>();
        emailSender.setValue("njangi@support.com");
        whatsAppNum.setValue("Chat: WhatsApp");
    }

    public void setLocale(Activity activity, String languageCode)
    {
        Locale locale = new Locale(languageCode.substring(0,2));
        Locale.setDefault(locale);
        Resources resources = activity.getResources();
        Configuration config = resources.getConfiguration();
        config.setLocale(locale);
        resources.updateConfiguration(config, resources.getDisplayMetrics());

        LanguageHelper.storeUserLanguage(app, String.valueOf(locale));
        LanguageHelper.updateLanguage(app, String.valueOf(locale));
    }

    public void support(Activity activity)
    {
        String contact = "+237 666305349"; // use country code with your phone number
        String url = "https://api.whatsapp.com/send?phone=" + contact;
        try {
            PackageManager pm = activity.getPackageManager();
            pm.getPackageInfo("com.whatsapp", PackageManager.GET_ACTIVITIES);
            Intent i = new Intent(Intent.ACTION_VIEW);
            i.setData(Uri.parse(url));
            activity.startActivity(i);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
    }

    public void signOut(NavHost navHostFragment)
    {
        firebaseApp.getAuth().signOut();
        firebaseApp.getAuth().addAuthStateListener(firebaseAuth -> {
            NavController navController = navHostFragment.getNavController();
            if (firebaseApp.getAuth().getCurrentUser() == null) {
                navController.navigate(navigation_login);
            }
            else
            {
                navController.navigate(R.id.navigation_account);
            }
        });
    }

    public LiveData<String> getEmail()
    {
        return emailSender;
    }

    public LiveData<String> getChat()
    {
        return whatsAppNum;
    }

    public LiveData<String> getAppVersion()
    {
        try {
            PackageInfo pInfo = app.getApplicationContext().getPackageManager()
                    .getPackageInfo(app.getApplicationContext().getPackageName(), 0);
            appVersion.setValue(app.getApplicationContext().getString(R.string.version_number) +" "+ pInfo.versionName);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return appVersion;
    }
}