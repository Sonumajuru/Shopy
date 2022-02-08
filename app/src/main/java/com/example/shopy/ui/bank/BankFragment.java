package com.example.shopy.ui.bank;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import com.braintreepayments.cardform.view.CardForm;
import com.example.shopy.databinding.FragmentBankBinding;

public class BankFragment extends Fragment {

    private BankViewModel bankViewModel;
    private FragmentBankBinding binding;

    private CardForm cardForm;
    private Button btnAddPay;
    private Button btnCancel;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        bankViewModel = new ViewModelProvider(this).get(BankViewModel.class);
        binding = FragmentBankBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        cardForm = binding.cardForm;
        btnAddPay = binding.btnSave;
        btnCancel = binding.btnCancel;

        cardForm.cardRequired(true)
                .expirationRequired(true)
                .cvvRequired(true)
                .postalCodeRequired(true)
                .mobileNumberRequired(true)
                .mobileNumberExplanation("SMS is required on this number")
                .setup(requireActivity());

        return root;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}