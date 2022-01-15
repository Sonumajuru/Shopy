package com.example.shopy.ui.cart;

import android.annotation.SuppressLint;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.shopy.adapter.CartAdapter;
import com.example.shopy.databinding.FragmentCartBinding;
import com.example.shopy.db.FavDB;
import com.example.shopy.model.CartItem;

import java.util.ArrayList;
import java.util.List;

public class ShoppingCartFragment extends Fragment {

    private ShoppingCartViewModel shoppingCartViewModel;
    private FragmentCartBinding binding;
    private Button btnCheckOut;
    private TextView subTotal;
    private TextView shipCost;
    private TextView total;
    private TextView emptyCart;

    private FavDB favDB;
    private List<CartItem> cartItemList;
    private RecyclerView recyclerView;
    int unicode;

    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        shoppingCartViewModel = new ViewModelProvider(this).get(ShoppingCartViewModel.class);

        binding = FragmentCartBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        favDB = new FavDB(getActivity());
        cartItemList = new ArrayList<>();
        recyclerView = binding.recyclerViewCart;
        btnCheckOut = binding.btnCheckout;
        subTotal = binding.subtotal;
        shipCost = binding.shippingCost;
        total = binding.total;
        emptyCart = binding.emptyCart;
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(requireActivity()));

        loadData();
//        btnCheckOut.setOnClickListener(this);

        return root;
    }

    @SuppressLint("Range")
    private void loadData() {
        if (cartItemList != null) {
            cartItemList.clear();
        }
        SQLiteDatabase db = favDB.getReadableDatabase();
        Cursor cursor = favDB.select_all_cart_list();
        try {
            while (cursor.moveToNext()) {
                String title = cursor.getString(cursor.getColumnIndex(FavDB.ITEM_TITLE));
                String id = cursor.getString(cursor.getColumnIndex(FavDB.KEY_ID));
                String image = cursor.getString(cursor.getColumnIndex(FavDB.ITEM_IMAGE));
                double price = cursor.getDouble(cursor.getColumnIndex(FavDB.ITEM_PRICE));
                double rating = cursor.getDouble(cursor.getColumnIndex(FavDB.ITEM_RATING));
                String currency = cursor.getString(cursor.getColumnIndex(FavDB.ITEM_CURRENCY));
                String uuid = cursor.getString(cursor.getColumnIndex(FavDB.ITEM_UUID));
                String desc = cursor.getString(cursor.getColumnIndex(FavDB.ITEM_DESCRIPTION));
                String category = cursor.getString(cursor.getColumnIndex(FavDB.ITEM_CATEGORY));
                String cartStatus = cursor.getString(cursor.getColumnIndex(FavDB.CART_STATUS));
                CartItem cartItem = new CartItem(title, desc, id, image, price, rating, currency, uuid, category, cartStatus);
                cartItemList.add(cartItem);
            }
        } finally {
            if (cursor != null && cursor.isClosed())
                cursor.close();
            db.close();
        }

        if (cartItemList.size() > 0)
        {
            unicode = 0x1F60A;
            emptyCart.setText("You are one step away " + getEmojiByUnicode(unicode));
            emptyCart.setVisibility(View.VISIBLE);
            CartAdapter cartAdapter = new CartAdapter(cartItemList, getActivity());
            recyclerView.setAdapter(cartAdapter);
        }
        else
        {
            unicode = 0x1F62D;
            emptyCart.setText("Your shopping cart is empty " + getEmojiByUnicode(unicode));
            emptyCart.setVisibility(View.VISIBLE);
        }
    }

    public String getEmojiByUnicode(int unicode){
        return new String(Character.toChars(unicode));
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}