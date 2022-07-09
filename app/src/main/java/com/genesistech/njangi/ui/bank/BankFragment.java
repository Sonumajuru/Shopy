package com.genesistech.njangi.ui.bank;

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
import com.genesistech.njangi.Controller;
import com.genesistech.njangi.databinding.FragmentBankBinding;

public class BankFragment extends Fragment {

    private FragmentBankBinding binding;
    private Controller controller;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        BankViewModel bankViewModel = new ViewModelProvider(this).get(BankViewModel.class);
        binding = FragmentBankBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        CardForm cardForm = binding.cardForm;
        Button btnAddPay = binding.btnSave;
        Button btnCancel = binding.btnCancel;
        controller = Controller.getInstance(requireContext());
        controller.setApplicationLanguage();

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