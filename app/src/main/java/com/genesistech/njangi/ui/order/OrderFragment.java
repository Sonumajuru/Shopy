package com.genesistech.njangi.ui.order;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import com.genesistech.njangi.R;

public class OrderFragment extends Fragment {

    private OrderViewModel orderViewModel;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState)
    {
        return inflater.inflate(R.layout.fragment_order, container, false);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        OrderFragment binding = null;
    }
}