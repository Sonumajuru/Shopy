package com.njangi.shop.ui.profile;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import com.njangi.shop.databinding.FragmentProfileBinding;

public class ProfileFragment extends Fragment {

    private FragmentProfileBinding binding;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        ProfileViewModel profileViewModel = new ViewModelProvider(this).get(ProfileViewModel.class);
        binding = FragmentProfileBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        TextView idProfileName = binding.idProfileName;
        TextView idEmail = binding.idEmail;
        TextView idNumber = binding.idNumber;
        TextView idCountryText = binding.idCountryText;
        TextView idAddress = binding.idAddress;
        TextView idSince = binding.idSince;

        assert getArguments() != null;
        profileViewModel.getUserData(getArguments().getString("uid"));
        profileViewModel.getProfile().observe(getViewLifecycleOwner(), idProfileName::setText);
        profileViewModel.getEmail().observe(getViewLifecycleOwner(), idEmail::setText);
        profileViewModel.getNumber().observe(getViewLifecycleOwner(), idNumber::setText);
        profileViewModel.getCountry().observe(getViewLifecycleOwner(), idCountryText::setText);
        profileViewModel.getAddress().observe(getViewLifecycleOwner(), idAddress::setText);
        profileViewModel.getRegistrationDate().observe(getViewLifecycleOwner(), idSince::setText);

        return root;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}