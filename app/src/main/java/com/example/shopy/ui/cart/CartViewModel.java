package com.example.shopy.ui.cart;

import android.app.Application;
import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import com.example.shopy.R;
import org.jetbrains.annotations.NotNull;

public class CartViewModel extends AndroidViewModel {

    private final MutableLiveData<String> emptyCart;
    private final Application app;

    public CartViewModel(@NonNull @NotNull Application application) {
        super(application);
        app = (Application) application.getApplicationContext();
        emptyCart = new MutableLiveData<>();
        emptyCart.setValue("njangi@support.com");
    }

    public LiveData<String> getStatusText()
    {
        int unicode = 0x1F60A;
        emptyCart.setValue(app.getString(R.string.one_step_away) + " "+ getEmojiByUnicode(unicode));
        return emptyCart;
    }

    public LiveData<String> getCartText()
    {
        int unicode = 0x1F62D;
        emptyCart.setValue(app.getString(R.string.empty_cart) + " "+ getEmojiByUnicode(unicode));
        return emptyCart;
    }

    public String getEmojiByUnicode(int unicode){
        return new String(Character.toChars(unicode));
    }
}