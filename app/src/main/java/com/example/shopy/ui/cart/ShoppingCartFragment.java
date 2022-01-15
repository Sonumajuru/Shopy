package com.example.shopy.ui.cart;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import com.example.shopy.R;
import com.example.shopy.databinding.FragmentCartBinding;

public class ShoppingCartFragment extends Fragment implements View.OnClickListener {

    private ShoppingCartViewModel shoppingCartViewModel;
    private FragmentCartBinding binding;
    private Button btnCheckOut;
    private TextView subTotal;
    private TextView shipCost;
    private TextView total;

    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        shoppingCartViewModel = new ViewModelProvider(this).get(ShoppingCartViewModel.class);

        binding = FragmentCartBinding.inflate(inflater, container, false);
        View root = binding.getRoot();
        btnCheckOut = binding.btnCheckout;
        subTotal = binding.subtotal;
        shipCost = binding.shippingCost;
        total = binding.total;
        btnCheckOut.setOnClickListener(this);

        final TextView textView = binding.cardOverview;
        shoppingCartViewModel.getText().observe(getViewLifecycleOwner(), new Observer<String>() {
            @Override
            public void onChanged(@Nullable String s) {
                textView.setText(s);
            }
        });
        return root;
    }

    @SuppressLint("NonConstantResourceId")
    @Override
    public void onClick(View v) {

        switch (v.getId())
        {
//            case btnCheckOut:
//                AccountFragment accountFragment = new AccountFragment();
//                swapFragment(accountFragment);
//                break;
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}