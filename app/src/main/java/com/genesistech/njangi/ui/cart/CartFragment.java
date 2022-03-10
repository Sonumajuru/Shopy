package com.genesistech.njangi.ui.cart;

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
import com.genesistech.njangi.Controller;
import com.genesistech.njangi.R;
import com.genesistech.njangi.adapter.CartAdapter;
import com.genesistech.njangi.databinding.FragmentCartBinding;
import com.genesistech.njangi.db.FavDB;
import com.genesistech.njangi.helper.PrefManager;
import com.genesistech.njangi.interfaces.FragmentCallback;
import com.genesistech.njangi.model.CartItem;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.*;
import java.util.stream.Collectors;

public class CartFragment extends Fragment {

    private CartViewModel cartViewModel;
    private FragmentCartBinding binding;

    private String currency;
    private Button btnCheckOut;
    private TextView subTotal;
    private TextView shipCost;
    private TextView total;
    private TextView emptyCart;

    private double subTotalCost;
    private double totalCost;

    private FavDB favDB;
    private Controller controller;
    private List<CartItem> cartItemList;
    private List<CartItem> newCartItemList;
    private RecyclerView recyclerView;

    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        cartViewModel = new ViewModelProvider(this).get(CartViewModel.class);
        binding = FragmentCartBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        favDB = new FavDB(getActivity());
        cartItemList = new ArrayList<>();
        newCartItemList = new ArrayList<>();
        controller = Controller.getInstance(requireContext());
        controller.setNavView(requireActivity().findViewById(R.id.nav_view));
        recyclerView = binding.recyclerViewCart;
        btnCheckOut = binding.btnCheckout;
        subTotal = binding.subtotal;
        shipCost = binding.shippingCost;
        total = binding.total;
        emptyCart = binding.emptyCart;
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));

        loadData();
        btnCheckOut.setOnClickListener(view -> {

        });
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
                JSONObject json = new JSONObject(cursor.getString(cursor.getColumnIndex(String.valueOf(FavDB.ITEM_IMAGE))));
                List<String> images = new ArrayList<>();
                JSONArray jArray = json.optJSONArray("images");
                if (jArray != null) {
                    for (int i = 0; i < jArray.length(); i++) {
                        images.add(jArray.optString(i));  //<< jget value from jArray
                    }
                }
                double price = cursor.getDouble(cursor.getColumnIndex(FavDB.ITEM_PRICE));
                double rating = cursor.getDouble(cursor.getColumnIndex(FavDB.ITEM_RATING));
                String currency = cursor.getString(cursor.getColumnIndex(FavDB.ITEM_CURRENCY));
                String uuid = cursor.getString(cursor.getColumnIndex(FavDB.ITEM_UUID));
                String desc = cursor.getString(cursor.getColumnIndex(FavDB.ITEM_DESCRIPTION));
                String category = cursor.getString(cursor.getColumnIndex(FavDB.ITEM_CATEGORY));
                String seller = cursor.getString(cursor.getColumnIndex(FavDB.ITEM_SELLER));
                String cartStatus = cursor.getString(cursor.getColumnIndex(FavDB.CART_STATUS));
                this.currency = currency;
                CartItem cartItem = new CartItem(title, seller, desc, id, images, price, rating, currency, uuid, category, cartStatus, 0);
                cartItemList.add(cartItem);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        } finally {
            if (cursor != null && cursor.isClosed())
                cursor.close();
            db.close();
        }

        // hashmap to store the frequency of element
        Map<String, Integer> map = new HashMap<>();

        for (CartItem value : cartItemList) {
            String id = value.getKey_id();
            Integer count = map.get(id);
            map.put(id, (count == null) ? 1 : count + 1);
        }

        List<CartItem> tempList = new ArrayList<>();
        for (Map.Entry<String, Integer> val : map.entrySet()) {
            System.out.println("Element " + val.getKey() + " " + "occurs" + ": " + val.getValue() + " times");
            for (CartItem value : cartItemList)
            {
                if (val.getKey().equals(value.getKey_id()))
                {
                    value.setQuantity(val.getValue());
                    tempList.add(value);
                }
            }
        }

        TreeSet<CartItem> set = tempList.stream()
                .collect(Collectors.toCollection(() ->
                        new TreeSet<>(Comparator.comparing(CartItem::getKey_id))));

        newCartItemList.addAll(set);

        updateScreen();

        // Handle Object of list item here
        FragmentCallback callback = new FragmentCallback() {
            @Override
            public void doSomething() {
            }

            @Override
            public void onItemClicked(int position, Object object) {
                // Handle Object of list item here
                subTotalCost = 0;
                totalCost = 0;
                updateScreen();
            }

            @Override
            public void onItemClicked(int position, Object object, int id) {

            }
        };
        CartAdapter cartAdapter = new CartAdapter(newCartItemList, getActivity(), callback);
        recyclerView.setAdapter(cartAdapter);
    }

    @SuppressLint("SetTextI18n")
    private void updateScreen()
    {
        if (newCartItemList.size() > 0)
        {
            cartViewModel.getStatusText().observe(getViewLifecycleOwner(), s -> {
                emptyCart.setText(s);
                emptyCart.setVisibility(View.VISIBLE);
                btnCheckOut.setEnabled(true);
            });
            for (CartItem cartItem : newCartItemList) {
                subTotalCost += cartItem.getPrice();
                totalCost += cartItem.getPrice() + 0;
            }
        }
        else
        {
            cartViewModel.getCartText().observe(getViewLifecycleOwner(), s -> {
                emptyCart.setText(s);
                emptyCart.setVisibility(View.VISIBLE);
                btnCheckOut.setEnabled(false);
            });
        }
        subTotal.setText(subTotalCost + " " +currency);
        shipCost.setText(Double.toString(0));
        total.setText(totalCost + " " +currency);
        if (totalCost == 0)
        {
            controller.removeBadge();
            PrefManager prefManager = new PrefManager(requireContext());
            prefManager.clearPref();
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}