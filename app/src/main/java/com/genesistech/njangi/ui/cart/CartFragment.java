package com.genesistech.njangi.ui.cart;

import android.annotation.SuppressLint;
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
import com.genesistech.njangi.helper.PrefManager;
import com.genesistech.njangi.interfaces.FragmentCallback;
import com.genesistech.njangi.model.Product;

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

    private Controller controller;
    private List<Product> cartItemList;
    private List<Product> newCartItemList;
    private RecyclerView recyclerView;
    private PrefManager prefManager;

    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        cartViewModel = new ViewModelProvider(this).get(CartViewModel.class);
        binding = FragmentCartBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        cartItemList = new ArrayList<>();
        newCartItemList = new ArrayList<>();
        prefManager = new PrefManager(requireContext());
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

        // hashmap to store the frequency of element
        Map<String, Integer> map = new HashMap<>();
        cartItemList = prefManager.getProductList();

        for (Product value : cartItemList) {
            String id = value.getProdID();
            Integer count = map.get(id);
            map.put(id, (count == null) ? 1 : count + 1);
        }

        List<Product> tempList = new ArrayList<>();
        for (Map.Entry<String, Integer> val : map.entrySet()) {
            System.out.println("Element " + val.getKey() + " " + "occurs" + ": " + val.getValue() + " times");
            for (Product value : cartItemList) {
                if (value.getCartStatus() != null)
                if (val.getKey().equals(value.getProdID()) && value.getCartStatus().equals("1")) {
                    value.setQuantity(val.getValue());
                    tempList.add(value);
                }
            }
        }

        TreeSet<Product> set = tempList.stream().collect(Collectors.toCollection(() ->
                        new TreeSet<>(Comparator.comparing(Product::getProdID))));

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
    private void updateScreen() {
        if (newCartItemList.size() > 0)
        {
            cartViewModel.getStatusText().observe(getViewLifecycleOwner(), s -> {
                emptyCart.setText(s);
                emptyCart.setVisibility(View.VISIBLE);
                btnCheckOut.setEnabled(true);
            });
            for (Product cartItem : newCartItemList) {
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