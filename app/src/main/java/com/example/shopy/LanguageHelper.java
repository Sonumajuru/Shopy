package com.example.shopy;

import android.content.Context;
import android.content.res.Configuration;
import android.content.res.Resources;

import java.util.Locale;

import static android.content.Context.MODE_PRIVATE;

public class LanguageHelper {

    private static final String GENERAL_STORAGE = "GENERAL_STORAGE";
    private static final String KEY_USER_LANGUAGE = "KEY_USER_LANGUAGE";

    /**
     * Update the app language
     *
     * @param language Language to switch to.
     */
    public static void updateLanguage(Context context, String language) {
        final Locale locale = new Locale(language);
        Locale.setDefault(locale);

        Resources res = context.getResources();
        Configuration cfg = new Configuration(res.getConfiguration());
        cfg.locale = locale;
        res.updateConfiguration(cfg, res.getDisplayMetrics());
    }

    /**
     * Store the language selected by the user.
     * /!\ SHOULD BE CALLED WHEN THE USER CHOOSE THE LANGUAGE
     */
    public static void storeUserLanguage(Context context, String language) {
        context.getSharedPreferences(GENERAL_STORAGE, MODE_PRIVATE)
                .edit()
                .putString(KEY_USER_LANGUAGE, language)
                .apply();
    }

    /**
     * @return The stored user language or null if not found.
     */
    public static String getUserLanguage(Context context) {
        return context.getSharedPreferences(GENERAL_STORAGE, MODE_PRIVATE)
                .getString(KEY_USER_LANGUAGE, null);
    }
}