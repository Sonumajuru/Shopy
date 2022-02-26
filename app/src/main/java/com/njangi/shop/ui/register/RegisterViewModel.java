package com.njangi.shop.ui.register;

import android.app.Application;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.navigation.NavController;
import androidx.navigation.NavHost;
import com.njangi.shop.R;
import com.njangi.shop.helper.FirebaseApp;
import com.njangi.shop.helper.LanguageHelper;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Locale;

public class RegisterViewModel extends AndroidViewModel {

    private final Application app;
    private final FirebaseApp firebaseApp;

    public RegisterViewModel(@NonNull @NotNull Application application) {
        super(application);
        app = (Application) application.getApplicationContext();
        firebaseApp = new FirebaseApp();
    }

    public void setCountryAdapter(Spinner country)
    {
        ArrayAdapter<String> adapter = new ArrayAdapter<>(app.getApplicationContext(), android.R.layout.simple_spinner_item, getCountryList());
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        country.setAdapter(adapter);
    }

    private List<String> getCountryList()
    {
        List<String> countriesList = new ArrayList<>();
        String[] locales = Locale.getISOCountries();

        for (String countryCode : locales)
        {
            if (countryCode.equals("CM") || countryCode.equals("NG")|| countryCode.equals("GH"))
            {
                Locale obj = new Locale("", countryCode);
                countriesList.add(obj.getDisplayCountry(Locale.ENGLISH));
                Collections.sort(countriesList);
            }
        }

        return countriesList;
    }

    public void setLanguage(Spinner language, List<String> langCode)
    {
        ArrayAdapter<String> adapter = new ArrayAdapter<>(app.getApplicationContext(), android.R.layout.simple_spinner_item, langCode);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        language.setAdapter(adapter);
    }

    public void getLanguages(Spinner country, Spinner language, List<String> langCode)
    {
        country.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                // your code here
                langCode.clear();
                if (country.getSelectedItem().equals("Cameroon"))
                {
                    langCode.add("FR " + LanguageHelper.countryCodeToEmoji("FR"));
                    langCode.add("ENG " + LanguageHelper.countryCodeToEmoji("UK"));
                }
                else
                {
                    langCode.add("ENG " + LanguageHelper.countryCodeToEmoji("UK"));
                }
                setLanguage(language, langCode);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parentView) {
                // your code here
            }
        });
    }

    public void goToAccount(NavHost navHostFragment)
    {
        if (navHostFragment != null)
        {
            firebaseApp.getAuth().addAuthStateListener(firebaseAuth -> {
                NavController navController = navHostFragment.getNavController();
                navController.navigate(R.id.navigation_account);
            });
        }
    }
}