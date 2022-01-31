package com.example.shopy.ui.profile;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import com.example.shopy.R;
import com.example.shopy.adapter.ParentViewAdapter;
import com.example.shopy.adapter.SliderAdapter;
import com.example.shopy.databinding.FragmentDetailBinding;
import com.example.shopy.databinding.FragmentProfileBinding;
import com.example.shopy.model.ParentModel;
import com.example.shopy.model.Product;
import com.example.shopy.model.User;
import com.example.shopy.ui.account.AccountFragment;
import com.example.shopy.ui.detail.DetailViewModel;
import com.example.shopy.ui.home.HomeFragment;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.*;
import org.jetbrains.annotations.NotNull;
import org.w3c.dom.Text;

import java.util.Comparator;
import java.util.Objects;
import java.util.TreeSet;
import java.util.stream.Collectors;

public class ProfileFragment extends Fragment {

    private ProfileViewModel profileViewModel;
    private FragmentProfileBinding binding;

    private TextView idProfileName;
    private TextView idEmail;
    private TextView idNumber;
    private TextView idCountryText;
    private TextView idAddress;
    private TextView idSince;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        profileViewModel = new ViewModelProvider(this).get(ProfileViewModel.class);
        binding = FragmentProfileBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        idProfileName = binding.idProfileName;
        idEmail = binding.idEmail;
        idNumber = binding.idNumber;
        idCountryText = binding.idCountryText;
        idAddress = binding.idAddress;
        idSince = binding.idSince;

        assert getArguments() != null;
        getUserData(getArguments().getString("uid"));

        return root;
    }

    @SuppressLint("SetTextI18n")
    private void getUserData(String uid)
    {
        DatabaseReference rootRef = FirebaseDatabase.getInstance().getReference();
        DatabaseReference eventsRef = rootRef.child("User").child(uid).getRef();
        ValueEventListener valueEventListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot)
            {
                User user = dataSnapshot.getValue(User.class);
                assert user != null;

                idProfileName.setText(user.getName()
                        + " " + user.getSurname());
                idEmail.setText(user.getEmail());
                idNumber.setText(user.getTelNumber());
                idCountryText.setText(user.getCountry());
                idAddress.setText(user.getAddress());
                idSince.setText(user.getEmail());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
//                Log.d(TAG, databaseError.getMessage()); //Don't ignore errors!
            }
        };
        eventsRef.addListenerForSingleValueEvent(valueEventListener);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

}