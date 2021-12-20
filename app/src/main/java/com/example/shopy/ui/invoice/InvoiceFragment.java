package com.example.shopy.ui.invoice;

import androidx.lifecycle.ViewModelProvider;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.example.shopy.R;

public class InvoiceFragment extends Fragment {

    private InvoiceViewModel invoiceViewModel;

    public static InvoiceFragment newInstance() {
        return new InvoiceFragment();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState)
    {
        return inflater.inflate(R.layout.fragment_invoice, container, false);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        invoiceViewModel = new ViewModelProvider(this).get(InvoiceViewModel.class);
        // TODO: Use the ViewModel
    }

}