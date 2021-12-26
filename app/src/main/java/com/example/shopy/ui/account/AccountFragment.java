package com.example.shopy.ui.account;

import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.example.shopy.R;
import com.example.shopy.databinding.FragmentAccountBinding;

import static com.example.shopy.R.id.*;

public class AccountFragment extends Fragment implements View.OnClickListener {

    private AccountViewModel accountViewModel;
    private FragmentAccountBinding binding;

    private Button btnOrder;
    private Button btnInvoice;
    private Button btnManageItem;
    private Button btnSettings;
    private Button btnSignOut;

    private LinearLayout linearLayout;

    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        accountViewModel = new ViewModelProvider(this).get(AccountViewModel.class);

        binding = FragmentAccountBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        btnOrder = binding.orderBtn;
        btnInvoice = binding.invoiceBtn;
        btnManageItem = binding.addRemoveBtn;
        btnSettings = binding.settingsBtn;
        btnSignOut = binding.signOutBtn;
        linearLayout = binding.supportLayout;

        btnOrder.setOnClickListener(this);
        btnInvoice.setOnClickListener(this);
        btnManageItem.setOnClickListener(this);
        btnSettings.setOnClickListener(this);
        btnSignOut.setOnClickListener(this);

        final TextView textView = binding.textAccount;
        accountViewModel.getText().observe(getViewLifecycleOwner(), new Observer<String>() {
            @Override
            public void onChanged(@Nullable String s) {
                textView.setText(s);
            }
        });
        return root;
    }

    @Override
    public void onClick(View v) {

        switch (v.getId())
        {
            case order_Btn:
                break;
            case invoice_Btn:
                break;
            case add_remove_Btn:
                break;
            case settings_Btn:
                break;
            case sign_out_Btn:
                break;
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}