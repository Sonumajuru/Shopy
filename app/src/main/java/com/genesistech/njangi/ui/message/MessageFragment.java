package com.genesistech.njangi.ui.message;

import androidx.lifecycle.ViewModelProvider;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.genesistech.njangi.R;
import com.genesistech.njangi.databinding.FragmentMessageBinding;
import com.genesistech.njangi.databinding.FragmentOverviewBinding;
import com.genesistech.njangi.helper.FirebaseApp;
import com.genesistech.njangi.ui.message.MessageViewModel;

import java.util.ArrayList;

public class MessageFragment extends Fragment {

    private MessageViewModel mViewModel;
    private FragmentMessageBinding binding;
    private FirebaseApp firebaseApp;

    private RecyclerView recyclerView;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        MessageViewModel overviewViewModel = new ViewModelProvider(this).get(MessageViewModel.class);
        binding = FragmentMessageBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        firebaseApp = new FirebaseApp();
        recyclerView = binding.recyclerGchat;
        recyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));

        return root;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}