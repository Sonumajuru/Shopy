package com.genesistech.njangi.ui.profile;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;
import com.genesistech.njangi.R;
import com.genesistech.njangi.databinding.FragmentProfileBinding;

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
        Button idChat = binding.idChatText;

        assert getArguments() != null;
        profileViewModel.getUserData(getArguments().getString("uid"));
        profileViewModel.getProfile().observe(getViewLifecycleOwner(), idProfileName::setText);
        profileViewModel.getEmail().observe(getViewLifecycleOwner(), idEmail::setText);
        profileViewModel.getNumber().observe(getViewLifecycleOwner(), idNumber::setText);
        profileViewModel.getCountry().observe(getViewLifecycleOwner(), idCountryText::setText);
        profileViewModel.getAddress().observe(getViewLifecycleOwner(), idAddress::setText);
        profileViewModel.getRegistrationDate().observe(getViewLifecycleOwner(), idSince::setText);

        idChat.setOnClickListener(v -> {

            Bundle bundle = new Bundle();
            bundle.putString("uuid", profileViewModel.getCurrentUser());
            Navigation.findNavController(v).navigate(R.id.navigation_message, bundle);
        });

        return root;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}