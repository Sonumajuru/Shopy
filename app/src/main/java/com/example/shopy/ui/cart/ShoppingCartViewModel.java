package com.example.shopy.ui.cart;

import android.app.Application;
import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import org.jetbrains.annotations.NotNull;

public class ShoppingCartViewModel extends AndroidViewModel {

    private final MutableLiveData<String> emptyCart;
    private final Application app;

    public ShoppingCartViewModel(@NonNull @NotNull Application application) {
        super(application);
        app = (Application) application.getApplicationContext();
        emptyCart = new MutableLiveData<>();
        emptyCart.setValue("njangi@support.com");
    }

    public LiveData<String> getStatusText()
    {
        int unicode = 0x1F60A;
        emptyCart.setValue("You are one step away " + getEmojiByUnicode(unicode));
        return emptyCart;
    }

    public LiveData<String> getCartText()
    {
        int unicode = 0x1F62D;
        emptyCart.setValue("Your shopping cart is empty " + getEmojiByUnicode(unicode));
        return emptyCart;
    }

    public String getEmojiByUnicode(int unicode){
        return new String(Character.toChars(unicode));
    }
}