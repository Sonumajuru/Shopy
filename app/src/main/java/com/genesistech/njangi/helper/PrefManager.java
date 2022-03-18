package com.genesistech.njangi.helper;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;
import com.genesistech.njangi.model.Product;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Class for Shared Preference
 */
public class PrefManager {

    private final Context context;
    private int quantity;
    private int count;
    private Gson gson;
    private SharedPreferences favPreferences;
    private SharedPreferences.Editor prefsFavEditor;
    private SharedPreferences cartPreferences;
    private SharedPreferences.Editor prefsCartEditor;

    public PrefManager(Context context) {
        this.context = context;
        gson = new Gson();
        try {
            favPreferences = context.getSharedPreferences("favorites", Context.MODE_PRIVATE);
            cartPreferences = context.getSharedPreferences("cart", Context.MODE_PRIVATE);
            prefsFavEditor = favPreferences.edit();
            prefsCartEditor = cartPreferences.edit();
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    public void saveBadge(int count) {
        cartPreferences = context.getSharedPreferences("Badge", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = cartPreferences.edit();
        editor.putInt("Badge", count);
        editor.apply();
    }

    public int getBadgeCount() {
        cartPreferences = context.getSharedPreferences("Badge", Context.MODE_PRIVATE);
        return (cartPreferences.getInt("Badge", count));
    }

    public void saveQuantity(int quantity, String prodID) {
        cartPreferences = context.getSharedPreferences("Quantity", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = cartPreferences.edit();
        editor.putInt(prodID, quantity);
        editor.apply();
    }

    public int getQuantity(String key) {
        cartPreferences = context.getSharedPreferences("Quantity", Context.MODE_PRIVATE);
        return (cartPreferences.getInt(key, quantity));
    }

    public void clearPref() {
        cartPreferences = context.getSharedPreferences("Quantity", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = cartPreferences.edit();
        editor.clear();
        editor.apply();
    }

    // create an empty list of MyQuote
    public void saveFavList(List<Product> productList, String prodID) {
        String jsonText = gson.toJson(productList);
        prefsFavEditor.putString(prodID, jsonText);
        prefsFavEditor.apply();
    }

    //getting quote list
    public List<Product> getFavList() {
        List<Product> arrayItems;
        List<Product> anotherList = new ArrayList<>();
        Map<String,?> keys = favPreferences.getAll();

        for(Map.Entry<String,?> entry : keys.entrySet()){
            Log.d("map values",entry.getKey() + ": " + entry.getValue().toString());
            String serializedObject = favPreferences.getString(entry.getKey(), null);
            if (serializedObject != null) {
                gson = new Gson();
                Type type = new TypeToken<List<Product>>(){}.getType();
                arrayItems = gson.fromJson(serializedObject, type);
                anotherList.addAll(arrayItems);
            }
        }

        return anotherList;
    }

    //updating saved quote list
    public void updateFavList(String prodID) {
        prefsFavEditor.remove(prodID).apply();
    }

    //updating saved quote list
//    public void updateQuoteList(MyQuote quote){
//        List<MyQuote> quoteList = getQuoteList();
//        quoteList.add(quote);
//        String jsonText = gson.toJson(quoteList);
//        prefsEditor.putString("MYQUOTE_LIST", jsonText);
//        prefsEditor.apply();
//    }

    // create an empty list of MyQuote
    public void saveCartList(List<Product> productList, String prodID) {
        String jsonText = gson.toJson(productList);
        prefsCartEditor.putString(prodID, jsonText);
        prefsCartEditor.apply();
    }

    //getting quote list
    public List<Product> getCartList() {
        List<Product> arrayItems;
        List<Product> anotherList = new ArrayList<>();
        Map<String,?> keys = cartPreferences.getAll();

        for(Map.Entry<String,?> entry : keys.entrySet()){
            Log.d("map values",entry.getKey() + ": " + entry.getValue().toString());
            String serializedObject = cartPreferences.getString(entry.getKey(), null);
            if (serializedObject != null) {
                gson = new Gson();
                Type type = new TypeToken<List<Product>>(){}.getType();
                arrayItems = gson.fromJson(serializedObject, type);
                anotherList.addAll(arrayItems);
            }
        }

        return anotherList;
    }

    //updating saved quote list
    public void updateCartList(String prodID) {
        prefsCartEditor.remove(prodID).apply();
    }
}