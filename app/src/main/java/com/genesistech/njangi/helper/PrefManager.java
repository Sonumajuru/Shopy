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
    private Gson gson;
    private SharedPreferences sharedPreferences;
    SharedPreferences.Editor prefsEditor;

    public PrefManager(Context context) {
        this.context = context;
        gson = new Gson();
        sharedPreferences = context.getSharedPreferences("favorites", Context.MODE_PRIVATE);
        prefsEditor = sharedPreferences.edit();
    }

    public void saveQuantity(int quantity) {
        sharedPreferences = context.getSharedPreferences("Quantity", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putInt("Quantity", quantity);
        editor.apply();
    }

    public int getQuantity() {
        sharedPreferences = context.getSharedPreferences("Quantity", Context.MODE_PRIVATE);
        return (sharedPreferences.getInt("Quantity", quantity));
    }

    public void clearPref() {
        sharedPreferences = context.getSharedPreferences("Quantity", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.clear();
        editor.apply();
    }

    // create an empty list of MyQuote
    public void initializeMyQuoteList(List<Product> productList, String prodID) {
        String jsonText = gson.toJson(productList);
        prefsEditor.putString(prodID, jsonText);
        prefsEditor.apply();
    }

    //getting quote list
    public List<Product> getProductList() {
        List<Product> arrayItems;
        List<Product> anotherList = new ArrayList<>();
        Map<String,?> keys = getAll();

        for(Map.Entry<String,?> entry : keys.entrySet()){
            Log.d("map values",entry.getKey() + ": " + entry.getValue().toString());
            String serializedObject = sharedPreferences.getString(entry.getKey(), null);
            if (serializedObject != null) {
                gson = new Gson();
                Type type = new TypeToken<List<Product>>(){}.getType();
                arrayItems = gson.fromJson(serializedObject, type);
                anotherList.addAll(arrayItems);
            }
        }

        return anotherList;
    }

    public Map<String, ?> getAll() {
        return sharedPreferences.getAll();
    }

    //updating saved quote list
    public void updateQuoteList(String prodID) {
        prefsEditor.remove(prodID).apply();
    }

    //updating saved quote list
//    public void updateQuoteList(MyQuote quote){
//        List<MyQuote> quoteList = getQuoteList();
//        quoteList.add(quote);
//        String jsonText = gson.toJson(quoteList);
//        prefsEditor.putString("MYQUOTE_LIST", jsonText);
//        prefsEditor.apply();
//    }
}