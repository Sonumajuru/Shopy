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
import androidx.appcompat.app.ActionBar;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.NavHost;
import androidx.navigation.fragment.NavHostFragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.shopy.R;
import com.example.shopy.adapter.CartAdapter;
import com.example.shopy.adapter.ProductAdapter;
import com.example.shopy.databinding.FragmentCartBinding;
import com.example.shopy.db.FavDB;
import com.example.shopy.model.CartItem;
import com.example.shopy.model.Product;

import java.util.ArrayList;
import java.util.List;

import static com.example.shopy.R.id.navigation_detail;

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
    private double subTotalCost;
    private double totalCost;
    private String currency;
    private ActionBar actionBar;

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
                this.currency = currency;
                CartItem cartItem = new CartItem(title, desc, id, image, price, rating, currency, uuid, category, cartStatus);
                cartItemList.add(cartItem);
            }
        } finally {
            if (cursor != null && cursor.isClosed())
                cursor.close();
            db.close();
        }

        updateScreen();
        CartAdapter cartAdapter = new CartAdapter(cartItemList, getActivity(), new CartAdapter.OnItemClickListener() {
            @Override
            public void onItemClicked(int position, Object object) {
                // Handle Object of list item here
                subTotalCost = 0;
                totalCost = 0;
                updateScreen();
            }
        });
        recyclerView.setAdapter(cartAdapter);
    }

    @SuppressLint("SetTextI18n")
    private void updateScreen()
    {
        int unicode;
        if (cartItemList.size() > 0)
        {
            unicode = 0x1F60A;
            emptyCart.setText("You are one step away " + getEmojiByUnicode(unicode));
            emptyCart.setVisibility(View.VISIBLE);

            for (int i = 0; i < cartItemList.size(); i++)
            {
                subTotalCost += cartItemList.get(i).getPrice();
                totalCost += cartItemList.get(i).getPrice() + 0;
            }

            subTotal.setText(subTotalCost + " " +currency);
            shipCost.setText(Double.toString(0));
            total.setText(totalCost + " " +currency);
        }
        else
        {
            unicode = 0x1F62D;
            emptyCart.setText("Your shopping cart is empty " + getEmojiByUnicode(unicode));
            emptyCart.setVisibility(View.VISIBLE);
            subTotal.setText(subTotalCost + " " +currency);
            shipCost.setText(Double.toString(0));
            total.setText(totalCost + " " +currency);
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