package com.example.shopy.helper;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * Class for Shared Preference
 */
public class PrefManager {

    Context context;

    public PrefManager(Context context) {
        this.context = context;
    }

    public void saveProductDetails(String id, String uuid, String name) {
        SharedPreferences sharedPreferences = context.getSharedPreferences("ProductDetails", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("ProdId", id);
        editor.putString("Uuid", uuid);
        editor.putString("Name", name);
        editor.apply();
    }

    public String getProductPrefs() {
        SharedPreferences sharedPreferences = context.getSharedPreferences("ProductDetails", Context.MODE_PRIVATE);
        return sharedPreferences.getString("ProdId", "");
    }

    public boolean saveProductPrefs() {
        SharedPreferences sharedPreferences = context.getSharedPreferences("ProductDetails", Context.MODE_PRIVATE);
        boolean isProdIdEmpty = sharedPreferences.getString("ProdId", "").isEmpty();
        boolean isUuidEmpty = sharedPreferences.getString("Uuid", "").isEmpty();
        return isProdIdEmpty || isUuidEmpty;
    }
}