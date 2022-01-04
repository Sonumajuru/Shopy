package com.example.shopy.ui.view;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavHost;
import androidx.navigation.fragment.NavHostFragment;
import com.example.shopy.R;
import com.example.shopy.databinding.FragmentAccountBinding;
import com.example.shopy.databinding.FragmentViewItemBinding;
import com.example.shopy.model.User;
import com.example.shopy.ui.account.AccountViewModel;
import com.google.firebase.auth.FirebaseAuth;

public class ViewItemFragment extends Fragment {

    private ViewItemViewModel viewItemViewModel;
    private FragmentViewItemBinding binding;

    private User njangiUser;
    private NavHost navHostFragment;
    private FirebaseAuth mAuth;

    public static ViewItemFragment newInstance() {
        return new ViewItemFragment();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        viewItemViewModel = new ViewModelProvider(this).get(ViewItemViewModel.class);
        binding = FragmentViewItemBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        mAuth = FirebaseAuth.getInstance();
        navHostFragment = (NavHostFragment) requireActivity().getSupportFragmentManager()
                .findFragmentById(R.id.nav_host_fragment_activity_main);

        njangiUser = new User();

        return root;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}